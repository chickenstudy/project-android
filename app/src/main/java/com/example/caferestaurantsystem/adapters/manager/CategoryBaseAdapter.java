package com.example.caferestaurantsystem.adapters.manager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.models.CategoryModel;

import java.util.ArrayList;

public class CategoryBaseAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CategoryModel> categoryModels;

    public CategoryBaseAdapter(Context context, ArrayList<CategoryModel> categoryModels) {
        this.context = context;
        this.categoryModels = categoryModels;
    }

    @Override
    public int getCount() {
        return categoryModels.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_base_item,parent,false);

        TextView txtTypeName = root.findViewById(R.id.create_product_Category_item);
        txtTypeName.setText(String.valueOf(categoryModels.get(position).getCategoryName()));
        Log.d("TAG", "getView: "+ txtTypeName.getText().toString());
        return root;
    }
}
