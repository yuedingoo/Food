package com.yueding.food.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yueding.food.R;
import com.yueding.food.db.Food;
import com.yueding.food.db.Restaurant;

import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 */

public class FoodMsgAdapter extends RecyclerView.Adapter<FoodMsgAdapter.ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<Food> mFoodList;
    private List<Restaurant> mRestaurantList;
    private OnItemClickListener mItemClickListener = null;

    public FoodMsgAdapter(List<Food> mFoodList, List<Restaurant> mRestaurantList) {
        this.mFoodList = mFoodList;
        this.mRestaurantList = mRestaurantList;
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null)
            mItemClickListener.onItemClick(v, (Integer) v.getTag());
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.food_item, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textMsg.setText("商家: ");
        Food food = mFoodList.get(position);
        String resName = null;
        for (Restaurant restaurant : mRestaurantList) {
            if (restaurant.getId() == food.getCode()) {
                resName = restaurant.getName();
                break;
            }
        }
        holder.textName.setText(food.getName());
        holder.textRestaurant.setText(resName);
        holder.textPrice.setText(String.format("%s", food.getPrice()));
        holder.button.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mFoodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textName;
        TextView textRestaurant;
        TextView textPrice;
        TextView textMsg;
        Button button;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textRestaurant = itemView.findViewById(R.id.textRemarks);
            textPrice = itemView.findViewById(R.id.textPrice);
            textMsg = itemView.findViewById(R.id.textMsg);
            button = itemView.findViewById(R.id.bt_revise);
        }
    }
}
