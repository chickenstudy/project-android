package com.example.caferestaurantsystem.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.interfaces.HomeLoadProducts;
import com.example.caferestaurantsystem.models.CategoryModel;
import com.example.caferestaurantsystem.models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder> {

    FirebaseFirestore db;
    Context context;
    ArrayList<CategoryModel> categoryModelList;
    Activity activity;
    HomeLoadProducts homeLoadProducts ;

    ArrayList<ProductModel> productModels = new ArrayList<>();



    boolean check = true;
    boolean selected = true;
    int row_index = -1;

    public HomeCategoryAdapter(Context context, ArrayList<CategoryModel> categoryModelList, Activity activity, HomeLoadProducts homeLoadProducts) {
        this.context = context;
        this.categoryModelList = categoryModelList;
        this.activity = activity;
        this.homeLoadProducts = homeLoadProducts;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db = FirebaseFirestore.getInstance();

        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_category_items,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(categoryModelList.get(position).getCategoryImage()).into(holder.categoryImage);
        holder.categoryName.setText(categoryModelList.get(position).getCategoryName());

        if(check) {
            db.collection("Products")
                    .whereEqualTo("categoryId", (position+1))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    ProductModel productModel = document.toObject(ProductModel.class);
                                    productModels.add(productModel);

                                }

                                homeLoadProducts.CallBack(position+1, productModels);
                            } else {
                                Toast.makeText(context, "Err" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            check = false;
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productModels.clear();
                int categoryId = categoryModelList.get(position).getCategoryId();
                row_index = position;
                notifyDataSetChanged();

                db.collection("Products")
                        .whereEqualTo("categoryId", categoryId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ProductModel productModel = document.toObject(ProductModel.class);
                                        productModels.add(productModel);

                                    }
                                    Log.d("ProductModelsSize", "Size: "+productModels.size() + "cateID"+ categoryId + "pos " + position );
                                    homeLoadProducts.CallBack(categoryId,productModels);
                                } else {
                                    Toast.makeText(context, "Err" + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                //Log.d("ProductModelsSize", "Size: "+productModels.size() + "cateID"+ categoryId + "pos " + position);


            }
        });

        if(selected){
            if(position == 0){
                holder.cardView.setBackgroundResource(R.drawable.change_bg);
                selected = false;
            }
        }
        else {
            if(row_index == position){
                holder.cardView.setBackgroundResource(R.drawable.change_bg);
            }else {
                holder.cardView.setBackgroundResource(R.drawable.default_bg);
            }
        }
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.home_category_img);
            categoryName = itemView.findViewById(R.id.home_category_name);
            cardView = itemView.findViewById(R.id.home_category_item_cardView);
        }
    }
}
