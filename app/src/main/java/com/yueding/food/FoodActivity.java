package com.yueding.food;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yueding.food.adapter.FoodAdapter;
import com.yueding.food.db.Food;
import com.yueding.food.db.Restaurant;

import org.litepal.crud.DataSupport;

import java.io.FileNotFoundException;
import java.util.List;

public class FoodActivity extends AppCompatActivity {

    private static final int UPDATE = 100;
    private static final String TAG = "yueding";
    private TextView textName;
    private TextView textRemarks;
    private Button buttonAdd;
    private Button buttonImage;
    private RecyclerView recyclerView;
    private ImageView imageHome;
    private int idCode;
    private List<Restaurant> theRestaurant;
    private List<Food> foodList;
    private String id;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:
                    foodList.clear();
                    List<Food> foods = DataSupport.where("code = ?", id).find(Food.class);
                    foodList.addAll(foods);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private FoodAdapter adapter;
    private String imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        textName = findViewById(R.id.textFoodName);
        textRemarks = findViewById(R.id.textFoodRemarks);
        buttonAdd = findViewById(R.id.bt_add);
        recyclerView = findViewById(R.id.recyclerFood);
        imageHome = findViewById(R.id.imageHome);
        buttonImage = findViewById(R.id.bt_image);
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodActivity.this, PhotoActivity.class);
                intent.putExtra("id", idCode);
                startActivity(intent);
            }
        });
        imageHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodActivity.this, ImageViewActivity.class);
                theRestaurant = DataSupport.where("id = ?", id).find(Restaurant.class);
                imageUri = theRestaurant.get(0).getUri();
                intent.putExtra("path", imageUri);
                if (null == imageUri) {
                    Toast.makeText(FoodActivity.this, "未设置照片", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(intent);
                }
            }
        });
        Intent intent = getIntent();
        idCode = intent.getIntExtra("id", 0);
        id = Integer.toString(idCode);
        theRestaurant = DataSupport.where("id = ?", id).find(Restaurant.class);
        foodList = DataSupport.where("code = ?", id).find(Food.class);
        String restaurantName = theRestaurant.get(0).getName();
        String restaurantRemarks = theRestaurant.get(0).getRemarks();
        loadImage();
        textName.setText(restaurantName);
        textRemarks.setText(restaurantRemarks);
//        添加菜单处理
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodActivity.this);
                View view = getLayoutInflater().inflate(R.layout.dialog2_view, null);
                final EditText editName = view.findViewById(R.id.dialog2NameEdit);
                final EditText editRemarks = view.findViewById(R.id.dialog2RemarksEdit);
                final EditText editPrice = view.findViewById(R.id.dialog2Price);
                builder.setView(view)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Food food = new Food();
                                food.setName(editName.getText().toString());
                                food.setRemarks(editRemarks.getText().toString());
                                food.setPrice(Double.parseDouble(editPrice.getText().toString()));
                                food.setCode(idCode);
                                food.save();
                                handler.sendEmptyMessage(UPDATE);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        adapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(adapter);
//        修改菜单处理
        adapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Toast.makeText(FoodActivity.this, "click item " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImageClick(View view, int position) {
                Toast.makeText(FoodActivity.this, "click image " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onButtonClick(View view, int position) {
                Food theFood = foodList.get(position);
                final int foodId = theFood.getId();
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodActivity.this);
                View v = getLayoutInflater().inflate(R.layout.dialog2_view, null);
                final TextView textTitle = v.findViewById(R.id.dialog2Title);
                final EditText editName = v.findViewById(R.id.dialog2NameEdit);
                final EditText editRemarks = v.findViewById(R.id.dialog2RemarksEdit);
                final EditText editPrice = v.findViewById(R.id.dialog2Price);
                textTitle.setText("修改");
                editName.setText(theFood.getName());
                editRemarks.setText(theFood.getRemarks());
                editPrice.setText(String.format("%s", theFood.getPrice()));
                builder.setView(v)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Food foodUpdate = new Food();
                                foodUpdate.setName(editName.getText().toString());
                                foodUpdate.setRemarks(editRemarks.getText().toString());
                                foodUpdate.setPrice(Double.parseDouble(editPrice.getText().toString()));
                                foodUpdate.update(foodId);
                                handler.sendEmptyMessage(UPDATE);
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataSupport.delete(Food.class, foodId);
                                handler.sendEmptyMessage(UPDATE);
                            }
                        }).show();
            }
        });
    }

    private void loadImage() {
        theRestaurant = DataSupport.where("id = ?", id).find(Restaurant.class);
        imageUri = theRestaurant.get(0).getUri();
        Glide.with(this).load(imageUri).into(imageHome);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                Intent intent = new Intent(FoodActivity.this, SearchActivity.class);
                intent.putExtra("activityName", "FoodActivity");
                intent.putExtra("id", idCode);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restaurant_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("删除")
                        .setMessage("确认删除本店收藏？")
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataSupport.deleteAll(Restaurant.class, "id = ?", id);
                                DataSupport.deleteAll(Food.class, "code = ?", id);
                                finish();
                            }
                        }).show();
                return true;
            case R.id.menu_item_edit:
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(FoodActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_view, null);
                final EditText dialogName = view.findViewById(R.id.dialogNameEdit);
                final EditText dialogRemarks = view.findViewById(R.id.dialogRemarksEdit);
                dialogName.setText(theRestaurant.get(0).getName());
                dialogRemarks.setText(theRestaurant.get(0).getRemarks());
                AlertDialog dialog = builder1.setView(view)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Restaurant restaurantUpdate = new Restaurant();
                                String updateName = dialogName.getText().toString();
                                String updateRemarks = dialogRemarks.getText().toString();
                                restaurantUpdate.setName(updateName);
                                restaurantUpdate.setRemarks(updateRemarks);
                                restaurantUpdate.update(idCode);
                                textName.setText(updateName);
                                textRemarks.setText(updateRemarks);
                            }
                        }).create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadImage();
    }
}
