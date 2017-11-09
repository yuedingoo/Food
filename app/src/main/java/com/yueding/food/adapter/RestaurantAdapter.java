package com.yueding.food.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yueding.food.R;
import com.yueding.food.db.Restaurant;

import java.util.List;

/**
 * Created by yueding on 2017/11/4.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private Context mContext;
    private List<Restaurant> mRestaurantList;

    private OnItemClickListener mItemClickListener = null;


    public interface OnItemClickListener{
        void onItemClick(View view, int position);
        void onImageClick(View view, int position);
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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Restaurant restaurant = mRestaurantList.get(position);
        holder.textName.setText(restaurant.getName());
        holder.textRemarks.setText(restaurant.getRemarks());
        Glide.with(mContext).load(restaurant.getUri()).into(holder.imageView);
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, holder.getAdapterPosition());
                }
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onImageClick(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout listItem;
        TextView textName;
        TextView textRemarks;
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            listItem = itemView.findViewById(R.id.restaurant_content);
            textName = itemView.findViewById(R.id.textName);
            textRemarks = itemView.findViewById(R.id.textRemarks);
            imageView = itemView.findViewById(R.id.imageShandian);
        }
    }
}
