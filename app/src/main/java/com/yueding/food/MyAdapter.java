package com.yueding.food;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yueding.food.db.Restaurant;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by yueding on 2017/11/4.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context mContext;
    private List<Restaurant> mRestaurantList;

    public MyAdapter() {
        mRestaurantList = DataSupport.findAll(Restaurant.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.restaurant_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, holder.textName.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Restaurant restaurant = mRestaurantList.get(position);
        holder.textName.setText(restaurant.getName());
        holder.textRemarks.setText(restaurant.getRemarks());
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout listItem;
        TextView textName;
        TextView textRemarks;
        public ViewHolder(View itemView) {
            super(itemView);
            listItem = (LinearLayout) itemView;
            textName = (TextView) itemView.findViewById(R.id.textName);
            textRemarks = (TextView) itemView.findViewById(R.id.textRemarks);
        }
    }
}
