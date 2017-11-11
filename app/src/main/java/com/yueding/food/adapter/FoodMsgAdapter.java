package com.yueding.food.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yueding.food.R;
import com.yueding.food.db.Food;
import com.yueding.food.db.Restaurant;

import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 */

public class FoodMsgAdapter extends RecyclerView.Adapter<FoodMsgAdapter.ViewHolder> {

    private Context mContext;
    private List<Food> mFoodList;
    private List<Restaurant> mRestaurantList;
    private OnItemClickListener mItemClickListener = null;

    public FoodMsgAdapter(List<Food> mFoodList, List<Restaurant> mRestaurantList) {
        this.mFoodList = mFoodList;
        this.mRestaurantList = mRestaurantList;
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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.textMsg.setText("餐馆: ");
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
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, holder.getAdapterPosition());
                }
            }
        });
        Glide.with(mContext).load(food.getUri()).into(holder.imageView);
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
        ImageView imageView;
        RelativeLayout itemLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textRestaurant = itemView.findViewById(R.id.textRemarks);
            textPrice = itemView.findViewById(R.id.textPrice);
            textMsg = itemView.findViewById(R.id.textMsg);
            imageView = itemView.findViewById(R.id.imageMin);
            itemLayout = itemView.findViewById(R.id.list_content);
        }
    }
}
