package com.yueding.food.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yueding.food.R;
import com.yueding.food.db.Restaurant;

import java.util.List;

/**
 * Created by yueding on 2017/11/4.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<Restaurant> mRestaurantList;

    private OnItemClickListener mItemClickListener = null;

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null)
            mItemClickListener.onItemClick(v, (Integer) v.getTag());
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public RestaurantAdapter(List<Restaurant> restaurants) {
        mRestaurantList = restaurants;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.restaurant_item, parent, false);
        view.setOnClickListener(this);
        /*holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FoodActivity.class);
                intent.putExtra("code", holder.code);
                mContext.startActivity(intent);
            }
        });*/
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Restaurant restaurant = mRestaurantList.get(position);
        holder.listItem.setTag(position);
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
            textName = itemView.findViewById(R.id.textName);
            textRemarks = itemView.findViewById(R.id.textRemarks);
        }
    }
}
