package com.yueding.food;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yueding.food.db.Food;
import com.yueding.food.db.Restaurant;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class PhotoActivity extends AppCompatActivity {

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private ImageView previewImage;
    private Uri imageUri;
    private String checkImageUri;
    private FloatingActionButton fab;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Button takePhoto = findViewById(R.id.bt_takePhoto);
        Button choosePhoto = findViewById(R.id.bt_choosePhoto);
        previewImage = findViewById(R.id.imagePreview);
        fab = findViewById(R.id.checkImage);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        final String idType = intent.getStringExtra("idType");
//        处理选择照片后的事件
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PhotoActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                }
                if ("restaurant".equals(idType)) {
                    Restaurant restaurantUpdate = new Restaurant();
                    restaurantUpdate.setUri(checkImageUri);
                    restaurantUpdate.update(id);
                    finish();
                } else if ("food".equals(idType)) {
                    Food foodUpdate = new Food();
                    foodUpdate.setUri(checkImageUri);
                    foodUpdate.update(id);
                    finish();
                }
            }
        });
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PhotoActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PhotoActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    takephoto();
                }
            }
        });
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PhotoActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    choosephoto();
                }
            }
        });

    }

    private void choosephoto() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    private void takephoto() {
        File outputImage = new File(getExternalCacheDir(), "takePhoto.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(PhotoActivity.this,
                    "com.yueding.food.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        Glide.with(PhotoActivity.this).load(bitmap).into(previewImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    checkImageUri = saveBitmap(bitmap);
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    String imagePath;
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        imagePath = handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        imagePath = handleImageBeforeKitKat(data);
                    }
                    Glide.with(PhotoActivity.this).load(imagePath).into(previewImage);
                    checkImageUri = imagePath;
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private String handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        return imagePath;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public String saveBitmap(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            Toast.makeText(this, "内存卡异常，请检查内存卡插入是否正确", Toast.LENGTH_SHORT).show();
            return "";
        }
        String path = System.currentTimeMillis() + ".jpg";
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myfoodphoto/");
        if (!f.exists()) {
            f.mkdir();
        }
        File file = new File(f, path);
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        /*try {
            FileOutputStream fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            return f.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takephoto();
                } else {
                    Log.i("yueding", "onRequestPermissionsResult: no camera permission");
                }
                break;
            case 2:
                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosephoto();
                } else {
                    Log.i("yueding", "onRequestPermissionsResult: no write permission");
                }
                break;
            default:
        }
    }
}
