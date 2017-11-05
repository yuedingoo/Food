package com.yueding.food;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yueding.food.db.Restaurant;

import org.litepal.LitePal;

public class AddActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editRemarks;
    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        LitePal.getDatabase();
        buttonAdd = (Button) findViewById(R.id.bt_ok);
        editName = (EditText) findViewById(R.id.editName);
        editRemarks = (EditText) findViewById(R.id.editRemakes);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Restaurant restaurant = new Restaurant();
                restaurant.setName(editName.getText().toString());
                restaurant.setRemarks(editRemarks.getText().toString());
                restaurant.save();
                Intent intent = new Intent(AddActivity.this, FavoritesActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
