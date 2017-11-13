package com.yueding.food;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yueding.food.db.Food;
import com.yueding.food.db.Restaurant;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ContentActivity extends AppCompatActivity {

    private Food food;
    private TextView textName;
    private TextView textRemarks;
    private TextView textCGName;
    private TextView textPrice;
    private ImageView imageView;
    private Button button;
    private int id;
    private String restaurant;
    private List<Food> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Intent intent = getIntent();
        restaurant = intent.getStringExtra("restaurant");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("美食");
        }
        id = intent.getIntExtra("id", 0);
        list = DataSupport.findAll(Food.class, id);
        food = list.get(0);
        textName = findViewById(R.id.contentName);
        textCGName = findViewById(R.id.contentCanguan);
        textRemarks = findViewById(R.id.contentRemarks);
        textPrice = findViewById(R.id.contentPrice);
        imageView = findViewById(R.id.contentImage);
        button = findViewById(R.id.contentButton);
        textName.setText(food.getName());
        textCGName.setText("餐馆: " + restaurant);
        textRemarks.setText("备注: "+ food.getRemarks());
        textPrice.setText(food.getPrice() + "元");
        Glide.with(ContentActivity.this).load(food.getUri()).into(imageView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentActivity.this, PhotoActivity.class);
                intent.putExtra("id", food.getId());
                intent.putExtra("idType", "food");
                startActivity(intent);
            }
        });
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
            case R.id.menu_item_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("删除")
                        .setMessage("确认删除本收藏？")
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataSupport.deleteAll(Food.class, "id = ?", String.valueOf(id));
                                finish();
                            }
                        }).show();
                return true;
            case R.id.menu_item_edit:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ContentActivity.this);
                View v = getLayoutInflater().inflate(R.layout.dialog2_view, null);
                final TextView textTitle = v.findViewById(R.id.dialog2Title);
                final EditText editName = v.findViewById(R.id.dialog2NameEdit);
                final EditText editRemarks = v.findViewById(R.id.dialog2RemarksEdit);
                final EditText editPrice = v.findViewById(R.id.dialog2Price);
                textTitle.setText("修改");
                editName.setText(food.getName());
                editRemarks.setText(food.getRemarks());
                editPrice.setText(String.format("%s", food.getPrice()));
                builder1.setView(v)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Food foodUpdate = new Food();
                                foodUpdate.setName(editName.getText().toString());
                                foodUpdate.setRemarks(editRemarks.getText().toString());
                                foodUpdate.setPrice(Double.parseDouble(editPrice.getText().toString()));
                                foodUpdate.update(id);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        list = DataSupport.findAll(Food.class, id);
                                        food = list.get(0);
                                        textName.setText(food.getName());
                                        textCGName.setText("餐馆: " + restaurant);
                                        textRemarks.setText("备注: "+ food.getRemarks());
                                        textPrice.setText(food.getPrice() + "元");
                                        Glide.with(ContentActivity.this).load(food.getUri()).into(imageView);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        List<Food> list = DataSupport.findAll(Food.class, id);
        food = list.get(0);
        Glide.with(ContentActivity.this).load(food.getUri()).into(imageView);
    }
}
