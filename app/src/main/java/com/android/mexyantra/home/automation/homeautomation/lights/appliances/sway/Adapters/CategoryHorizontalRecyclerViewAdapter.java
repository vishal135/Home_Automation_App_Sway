package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Models.CategoryModel;
import com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.R;

import java.util.List;

public class CategoryHorizontalRecyclerViewAdapter extends RecyclerView.Adapter<CategoryHorizontalRecyclerViewAdapter.CategoryViewHolder> {

    private List<CategoryModel> categoryModels;
    private Context context;

    private OnCategoryListener onCategoryListener;

    public CategoryHorizontalRecyclerViewAdapter(){

    }

    public CategoryHorizontalRecyclerViewAdapter(Context context, List<CategoryModel> categoryModels, OnCategoryListener onCategoryListener) {
        this.categoryModels = categoryModels;
        this.context = context;
        this.onCategoryListener = onCategoryListener;
    }

    @NonNull
    @Override
    public CategoryHorizontalRecyclerViewAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pager_item, parent, false);
        return  new CategoryViewHolder(view, onCategoryListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHorizontalRecyclerViewAdapter.CategoryViewHolder holder, int position) {
        holder.category_Img.setImageResource(categoryModels.get(position).getImage());
        holder.category_Name.setText(categoryModels.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView category_Img;
        TextView category_Name;

        OnCategoryListener onCategoryListener;

        public CategoryViewHolder(@NonNull View itemView, OnCategoryListener onCategoryListener) {
            super(itemView);

            category_Img = itemView.findViewById(R.id.categoryImg);
            category_Name = itemView.findViewById(R.id.categoryName);
            this.onCategoryListener = onCategoryListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCategoryListener.OnCategoryClick(getAdapterPosition());
        }
    }

    public interface OnCategoryListener{
        void OnCategoryClick(int i);
    }

}
