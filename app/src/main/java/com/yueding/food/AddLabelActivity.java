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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yueding.food.Utils.SpUtil;
import com.yueding.food.db.Restaurant;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

public class AddLabelActivity extends AppCompatActivity {

    private TagFlowLayout mLabelLayout;
    private TagFlowLayout mAllLabelLayout;
    private TagAdapter<String> adapter1;
    private TagAdapter<String> adapter2;
    final List<String> selectLabel = new ArrayList<>();
    final List<String> allLabel = new ArrayList<>();
    private List<String> defLabel = new ArrayList<>();
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_label);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("添加标签");
        }
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        mLabelLayout = findViewById(R.id.selectLabelLayout);
        mAllLabelLayout = findViewById(R.id.allLabelLayout);
        final LayoutInflater inflater = LayoutInflater.from(this);
        selectLabel.add("+自定义标签");
        if (SpUtil.getStringList(AddLabelActivity.this, "def").size() == 0) {
            defLabel.add("好吃");
            defLabel.add("川菜馆");
            defLabel.add("素菜");
            defLabel.add("湘菜馆");
            defLabel.add("火锅");
            defLabel.add("烧烤");
            defLabel.add("有点辣");
            defLabel.add("甜食");
            defLabel.add("麻辣烫");
            SpUtil.putStringList(AddLabelActivity.this, "def", defLabel);
        } else {
            defLabel = SpUtil.getStringList(AddLabelActivity.this, "def");
        }
        allLabel.addAll(defLabel);
        adapter1 = new TagAdapter<String>(selectLabel) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView textView = (TextView) inflater.inflate(R.layout.fl_item, mLabelLayout, false);
                textView.setTextSize(16);
                textView.setText(s);
                return textView;
            }
        };
        mLabelLayout.setAdapter(adapter1);
        mLabelLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if (position != selectLabel.size() - 1) {
                    allLabel.add(selectLabel.get(position));
                    selectLabel.remove(position);
                } else {
                    final EditText edit = new EditText(AddLabelActivity.this);
                    new AlertDialog.Builder(AddLabelActivity.this)
                            .setTitle("添加标签")
                            .setView(edit)
                            .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    添加自定义标签事件
                                    if (isOver()) {
                                        allLabel.add(edit.getText().toString());
                                        adapter2.notifyDataChanged();
                                        Toast.makeText(AddLabelActivity.this, "最多选6个标签",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        selectLabel.remove(selectLabel.size()-1);
                                        selectLabel.add(edit.getText().toString());
                                        selectLabel.add("+自定义标签");
                                        adapter1.notifyDataChanged();
                                    }
                                    defLabel.add(edit.getText().toString());
                                    SpUtil.putStringList(AddLabelActivity.this, "def", defLabel);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
                adapter1.notifyDataChanged();
                adapter2.notifyDataChanged();
                return true;
            }
        });
        adapter2 = new TagAdapter<String>(allLabel) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView textView = (TextView) inflater.inflate(R.layout.fl_item, mAllLabelLayout, false);
                textView.setTextSize(16);
                textView.setText(s);
                return textView;            }
        };
        mAllLabelLayout.setAdapter(adapter2);
        mAllLabelLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if (isOver()) {
                    Toast.makeText(AddLabelActivity.this, "最多选6个标签", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    selectLabel.remove(selectLabel.size()-1);
                    selectLabel.add(allLabel.get(position));
                    allLabel.remove(position);
                    selectLabel.add("+自定义标签");
                    adapter1.notifyDataChanged();
                    adapter2.notifyDataChanged();
                    return true;
                }

            }
        });
    }
    boolean isOver (){
        if (selectLabel.size() >= 7) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ok_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok_menu:
                String sect = selectLabel.toString();
                sect = sect.substring(1, sect.indexOf("+") - 2);

                //把选择好的标签存到数据库
                Restaurant restaurantUpdate = new Restaurant();
                restaurantUpdate.setLabel(sect);
                restaurantUpdate.update(id);

                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
