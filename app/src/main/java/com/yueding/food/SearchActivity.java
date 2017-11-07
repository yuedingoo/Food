package com.yueding.food;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yueding.food.adapter.FoodAdapter;
import com.yueding.food.adapter.RestaurantAdapter;
import com.yueding.food.db.Food;
import com.yueding.food.db.Restaurant;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "yueding";
    private EditText editSearch;
    private Button buttonSearch;
    private RecyclerView searchRecyclerView;
    private List<Restaurant> restaurantList = null;
    private List<Food> foodList = null;
    private RestaurantAdapter restaurantAdapter;
    private FoodAdapter foodAdapter;
    private String activityName;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        activityName = intent.getStringExtra("activityName");
        editSearch = findViewById(R.id.edit_search);
        buttonSearch = findViewById(R.id.bt_search);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        searchRecyclerView.setLayoutManager(manager);
        if ("FavoritesActivity".equals(activityName)) {
            restaurantList = DataSupport.findAll(Restaurant.class);
            restaurantAdapter = new RestaurantAdapter(restaurantList);
            searchRecyclerView.setAdapter(restaurantAdapter);
        } else if ("FoodActivity".equals(activityName)) {
            id = intent.getIntExtra("id", 0);
            foodList = DataSupport.where("code = ?", Integer.toString(id)).find(Food.class);
            foodAdapter = new FoodAdapter(foodList);
            searchRecyclerView.setAdapter(foodAdapter);
        }
//        监听输入框文字变化
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                refresh();
            }
        });
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }

    private void refresh() {
        String key = editSearch.getText().toString();

        if ("FavoritesActivity".equals(activityName)) {
            List<Restaurant> searchList = DataSupport.where("name like ?", "%"+key+"%").find(Restaurant.class);
            restaurantList.clear();
            restaurantList.addAll(searchList);
            restaurantAdapter.notifyDataSetChanged();
        } else if ("FoodActivity".equals(activityName)) {
            List<Food> searchList = new ArrayList<>();
//            使用原始SQL语句查询
            Cursor cursor = DataSupport.findBySQL("select * from Food where name like ? and code = ?", "%" + key + "%", String.valueOf(id));
            if (cursor.moveToFirst()) {
                do {
                    Food food = new Food();
                    food.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    food.setCode(cursor.getInt(cursor.getColumnIndex("code")));
                    food.setName(cursor.getString(cursor.getColumnIndex("name")));
                    food.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
                    food.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                    searchList.add(food);
                } while (cursor.moveToNext());
            }
            cursor.close();
            foodList.clear();
            foodList.addAll(searchList);
            foodAdapter.notifyDataSetChanged();
        }

    }
}
