package com.yueding.food;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yueding.food.db.Restaurant;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

public class AddActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editRemarks;
    private Button buttonAdd;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        LitePal.getDatabase();
        buttonAdd = findViewById(R.id.bt_ok);
        editName = findViewById(R.id.editName);
        editRemarks = findViewById(R.id.editRemakes);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurant = new Restaurant();
                restaurant.setName(editName.getText().toString());
                restaurant.setRemarks(editRemarks.getText().toString());
                if (!"".equals(restaurant.getName())) {
                    restaurant.save();
                    int id = getIntent().getIntExtra("currentId", 0);
                    Intent intent = new Intent(AddActivity.this, FoodActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AddActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
