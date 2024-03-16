package com.example.caferestaurantsystem.adapters.manager;

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
import com.example.caferestaurantsystem.activities.manager.UpdateProductActivity;
import com.example.caferestaurantsystem.models.ProductModel;

import java.io.Serializable;
import java.util.ArrayList;

public class ManageProductAdapter extends RecyclerView.Adapter<ManageProductAdapter.ViewHolder> {
    Context context;
    ArrayList<ProductModel> productModels;

    public ManageProductAdapter(Context context, ArrayList<ProductModel> productModels) {
        this.context = context;
        this.productModels = productModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ManageProductAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_products_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(productModels.get(position).getProductImage()).into(holder.productImage);
        holder.productName.setText(productModels.get(position).getProductName());
        holder.productDescription.setText(productModels.get(position).getProductDescription());
        holder.productRating.setText(String.format("%.1f", productModels.get(position).getProductRating()));
        holder.productPrice.setText("Price "+ String.format("%,d", productModels.get(position).getProductPrice()));
        holder.productDiscount.setText("Discount "+ productModels.get(position).getProductDiscount()+ "%");

        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductModel selectedProduct = productModels.get(position);
                Intent intent = new Intent(context, UpdateProductActivity.class);

                intent.putExtra("product", (Serializable) selectedProduct);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productDescription, productPrice, productRating, productDiscount;
        ImageView btnUpdate, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.manage_product_name);
            productImage = itemView.findViewById(R.id.manage_product_img);
            productDescription = itemView.findViewById(R.id.manage_product_description);
            productPrice = itemView.findViewById(R.id.manage_product_price);
            productRating = itemView.findViewById(R.id.manage_product_rating);
            productDiscount = itemView.findViewById(R.id.manage_product_discount);
            btnUpdate = itemView.findViewById(R.id.manage_product_update);
            btnDelete = itemView.findViewById(R.id.manage_product_delete);



        }
    }
}
