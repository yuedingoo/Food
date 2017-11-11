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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yueding.food.R;
import com.yueding.food.db.Food;

import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private Context mContext;
    private List<Food> mFoodList;
    private OnItemClickListener mItemClickListener = null;

    public FoodAdapter(List<Food> mFoodList) {
        this.mFoodList = mFoodList;
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
        Food food = mFoodList.get(position);
        holder.textName.setText(food.getName());
        holder.textRemarks.setText(food.getRemarks());
        holder.textPrice.setText(String.format("%s", food.getPrice()));
        Glide.with(mContext).load(food.getUri()).into(holder.imageView);
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFoodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout listItem;
        TextView textName;
        TextView textRemarks;
        TextView textPrice;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            listItem = itemView.findViewById(R.id.list_content);
            textName = itemView.findViewById(R.id.textName);
            textRemarks = itemView.findViewById(R.id.textRemarks);
            textPrice = itemView.findViewById(R.id.textPrice);
            imageView = itemView.findViewById(R.id.imageMin);
        }
    }
}
