package com.yueding.food;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yueding.food.adapter.RestaurantAdapter;
import com.yueding.food.db.Restaurant;

import org.litepal.crud.DataSupport;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private static final String TAG = "yueding";
    private FloatingActionButton fab;
    private RestaurantAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private List<Restaurant> restaurantList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        restaurantList = DataSupport.findAll(Restaurant.class);
        refreshLayout = findViewById(R.id.refresh);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoritesActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new RestaurantAdapter(restaurantList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RestaurantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Restaurant> restaurants = DataSupport.findAll(Restaurant.class);
                Intent intent = new Intent(FavoritesActivity.this, FoodActivity.class);
                intent.putExtra("id", restaurants.get(position).getId());
                startActivity(intent);
            }
        });
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void refresh() {
        List<Restaurant> userList = DataSupport.findAll(Restaurant.class);
        restaurantList.clear();
        restaurantList.addAll(userList);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refresh();
    }
}
