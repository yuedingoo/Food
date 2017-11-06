package com.yueding.food.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yueding.food.R;
import com.yueding.food.db.Food;

import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<Food> mFoodList;
    private OnItemClickListener mItemClickListener = null;

    public FoodAdapter(List<Food> mFoodList) {
        this.mFoodList = mFoodList;
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
        Food food = mFoodList.get(position);
        holder.textName.setText(food.getName());
        holder.textRemarks.setText(food.getRemarks());
        holder.textPrice.setText(String.format("%s", food.getPrice()));
        holder.listItem.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mFoodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout listItem;
        TextView textName;
        TextView textRemarks;
        TextView textPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            listItem = itemView.findViewById(R.id.list_item);
            textName = itemView.findViewById(R.id.textName);
            textRemarks = itemView.findViewById(R.id.textRemarks);
            textPrice = itemView.findViewById(R.id.textPrice);
        }
    }
}
