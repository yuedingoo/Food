package com.yueding.food;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.yueding.food.adapter.FoodAdapter;
import com.yueding.food.adapter.FoodMsgAdapter;
import com.yueding.food.adapter.RestaurantAdapter;
import com.yueding.food.db.Food;
import com.yueding.food.db.Restaurant;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "yueding";
    private EditText editSearch;
    private RecyclerView searchRecyclerView;
    private List<Restaurant> restaurantList = null;
    private List<Food> foodList = null;
    private RestaurantAdapter restaurantAdapter;
//    private FoodAdapter foodAdapter;
    private FoodMsgAdapter foodMsgAdapter;
    private String activityName;
    private int id;
    private Spinner spinner;
    private int select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        activityName = intent.getStringExtra("activityName");
        editSearch = findViewById(R.id.edit_search);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        spinner = findViewById(R.id.spinner);
        /*String[] mItems = getResources().getStringArray(R.array.searchList);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mItems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);*/
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
//                        餐馆
                        editSearch.setText("");
                        restaurantList = DataSupport.findAll(Restaurant.class);
                        restaurantAdapter = new RestaurantAdapter(restaurantList);
                        searchRecyclerView.setAdapter(restaurantAdapter);
                        restaurantAdapter.setOnItemClickListener(new RestaurantAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(SearchActivity.this, FoodActivity.class);
                                intent.putExtra("id", restaurantList.get(position).getId());
                                startActivity(intent);
                            }

                            @Override
                            public void onImageClick(View view, int position) {
                                onItemClick(view, position);
                            }
                        });
                        select = 0;
                        break;
                    case 1:
//                        菜名
                        editSearch.setText("");
                        foodList = DataSupport.findAll(Food.class);
                        restaurantList = DataSupport.findAll(Restaurant.class);
                        foodMsgAdapter = new FoodMsgAdapter(foodList, restaurantList);
                        foodMsgAdapter.setOnItemClickListener(new FoodMsgAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                int code = foodList.get(0).getCode();
                                List<Restaurant> restaurants= DataSupport.findAll(Restaurant.class, code);
                                Intent intent = new Intent(SearchActivity.this, ContentActivity.class);
                                intent.putExtra("id", foodList.get(position).getId());
                                intent.putExtra("restaurant", restaurants.get(0).getName());
                                startActivity(intent);
                            }
                        });
                        searchRecyclerView.setAdapter(foodMsgAdapter);
                        select = 1;
                        break;
                    default:
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        searchRecyclerView.setLayoutManager(manager);

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
                if (!"".equals(editSearch.getText().toString())) {
                    refresh();
                }
            }
        });
    }

    private void refresh() {
        String key = editSearch.getText().toString();
        switch (select) {
//            餐馆搜索
            case 0:
                List<Restaurant> searchList = DataSupport.where("name like ?", "%"+key+"%").find(Restaurant.class);
                restaurantList.clear();
                restaurantList.addAll(searchList);
                restaurantAdapter.notifyDataSetChanged();
                break;

//             菜名搜索
            case 1:
                List<Food> searchList1 = DataSupport.where("name like ?", "%"+key+"%").find(Food.class);
                foodList.clear();
                foodList.addAll(searchList1);
                foodMsgAdapter.notifyDataSetChanged();
                break;
        }

    }
}
