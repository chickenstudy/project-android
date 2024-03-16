package com.example.caferestaurantsystem.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.activities.NavMenuProductActivity;
import com.example.caferestaurantsystem.models.CategoryModel;

import java.util.List;

public class NavRestaurantMenuAdapter extends RecyclerView.Adapter<NavRestaurantMenuAdapter.ViewHolder> {
    Context context;
    List<CategoryModel> categoryModelList;

    public NavRestaurantMenuAdapter(Context context, List<CategoryModel> categoryModelList) {
        this.context = context;
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_restaurant_menu_items,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(categoryModelList.get(position).getCategoryImage()).into(holder.categoryImage);
        holder.categoryName.setText(categoryModelList.get(position).getCategoryName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NavMenuProductActivity.class);
                intent.putExtra("CategoryId",categoryModelList.get(position).getCategoryId());
                intent.putExtra("CategoryImage",categoryModelList.get(position).getCategoryImage());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.nav_restaurant_menu_img);
            categoryName = itemView.findViewById(R.id.nav_restaurant_menu_name);
        }
    }
}
