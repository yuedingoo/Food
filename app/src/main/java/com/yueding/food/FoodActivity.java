package com.yueding.food;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.yueding.food.adapter.FoodAdapter;
import com.yueding.food.baiduMap.GetPosActivity;
import com.yueding.food.db.Food;
import com.yueding.food.db.Restaurant;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodActivity extends AppCompatActivity {

    private static final int UPDATE = 100;
    private TextView textName;
    private TextView textRemarks;
    private Button buttonAdd;
    private Button buttonImage;
    private RecyclerView recyclerView;
    private ImageView imageHome;
    private int idCode;    //restaurant id (int)
    private String id;     //restaurant id (String)
    private List<Restaurant> theRestaurant;
    private List<Food> foodList;
    private TagFlowLayout mFlowLayout;
    private TagAdapter<String> flowAdapter;
    final List<String> mVals = new ArrayList<>();
    private FoodAdapter adapter;
    private String imageUri;
    private Button buttonGetPos;
    private TextView textPos;

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
                    //标签更新
                    theRestaurant = DataSupport.where("id = ?", id).find(Restaurant.class);
                    String labels = theRestaurant.get(0).getLabel();
                    mVals.clear();
                    mVals.addAll(Arrays.asList(labels.split(", ")));
                    mVals.add("+添加标签");
                    flowAdapter.notifyDataChanged();
                    break;
            }
        }
    };

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
        mFlowLayout = findViewById(R.id.flowLayout);
        buttonGetPos = findViewById(R.id.bt_pos);
        textPos = findViewById(R.id.textPos);
        ActionBar actionBar = getSupportActionBar();


        Intent intent = getIntent();
        idCode = intent.getIntExtra("id", 0);
        id = Integer.toString(idCode);
        theRestaurant = DataSupport.where("id = ?", id).find(Restaurant.class);
        foodList = DataSupport.where("code = ?", id).find(Food.class);
        String restaurantName = theRestaurant.get(0).getName();
        String restaurantRemarks = theRestaurant.get(0).getRemarks();
        if (actionBar != null) {
            actionBar.setTitle("餐馆");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        loadImage();
        textName.setText(restaurantName);
        textRemarks.setText("备注:"+restaurantRemarks);

        //        设置图片
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodActivity.this, PhotoActivity.class);
                intent.putExtra("id", idCode);
                intent.putExtra("idType", "restaurant");
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
//        菜单点击事件处理，点击图片和内容
        adapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Intent intent1 = new Intent(FoodActivity.this, ContentActivity.class);
                Food theFood = foodList.get(position);
                intent1.putExtra("id", theFood.getId());
                intent1.putExtra("restaurant", theRestaurant.get(0).getName());
                startActivity(intent1);
            }
        });

        //        设置标签
        final LayoutInflater inflater = LayoutInflater.from(this);
        String labels = theRestaurant.get(0).getLabel();
        if (labels != null) {
            mVals.addAll(Arrays.asList(labels.split(", ")));
        }
        mVals.add("+添加标签");
        flowAdapter = new TagAdapter<String>(mVals) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView textView = (TextView) inflater.inflate(R.layout.fl_item, mFlowLayout, false);
                textView.setText(s);
                return textView;
            }
        };
        mFlowLayout.setAdapter(flowAdapter);
//        添加标签点击事件
        mFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if (position == mVals.size() - 1) {
//                    处理添加标签
                    Intent intent = new Intent(FoodActivity.this, AddLabelActivity.class);
                    intent.putExtra("id", idCode);
                    startActivity(intent);
                }
                return true;
            }
        });

        buttonGetPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(FoodActivity.this, GetPosActivity.class);
                intent1.putExtra("id", idCode);
                startActivity(intent1);
            }
        });

    }

    private void loadImage() {
        theRestaurant = DataSupport.where("id = ?", id).find(Restaurant.class);
        imageUri = theRestaurant.get(0).getUri();
        Glide.with(this).load(imageUri).into(imageHome);

        //方便更新 就添加到该方法中
        String addr = theRestaurant.get(0).getAddress();
        if (null != addr) {
            textPos.setText("地址:"+addr);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restaurant_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
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
        handler.sendEmptyMessage(UPDATE);
        loadImage();
    }
}
