package com.yueding.food;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yueding.food.adapter.FoodAdapter;
import com.yueding.food.db.Food;
import com.yueding.food.db.Restaurant;

import org.litepal.crud.DataSupport;

import java.util.List;

public class FoodActivity extends AppCompatActivity {

    private TextView textName;
    private TextView textRemarks;
    private Button buttonEdit;
    private Button buttonDelete;
    private Button buttonAdd;
    private RecyclerView recyclerView;
    private int idCode;
    private List<Restaurant> theRestaurant;
    private List<Food> foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        textName = findViewById(R.id.textFoodName);
        textRemarks = findViewById(R.id.textFoodRemarks);
        buttonDelete = findViewById(R.id.bt_delete);
        buttonEdit = findViewById(R.id.bt_edit);
        buttonAdd = findViewById(R.id.bt_add);
        recyclerView = findViewById(R.id.recyclerFood);
        Intent intent = getIntent();
        idCode = intent.getIntExtra("id", 0);
        final String id = Integer.toString(idCode);
        theRestaurant = DataSupport.where("id = ?", id).find(Restaurant.class);
        foodList = DataSupport.where("code = ?", id).find(Food.class);
        String restaurantName = theRestaurant.get(0).getName();
        String restaurantRemarks = theRestaurant.get(0).getRemarks();
        textName.setText(restaurantName);
        textRemarks.setText(restaurantRemarks);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataSupport.deleteAll(Restaurant.class, "id = ?", id);
                finish();
            }
        });
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_view, null);
                final EditText dialogName = view.findViewById(R.id.dialogNameEdit);
                final EditText dialogRemarks = view.findViewById(R.id.dialogRemarksEdit);
                dialogName.setText(theRestaurant.get(0).getName());
                dialogRemarks.setText(theRestaurant.get(0).getRemarks());
                AlertDialog dialog = builder.setView(view)
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
            }
        });
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Food food = new Food();
                food.setName("123hehhe");
                food.setRemarks("gui");
                food.setPrice(15.63);
                food.setCode(idCode);
                food.save();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        FoodAdapter adapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "clicked" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
