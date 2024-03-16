package com.example.caferestaurantsystem.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.adapters.NavMenuItemAdapter;
import com.example.caferestaurantsystem.models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NavMenuProductActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView categoryImageView;
    FloatingActionButton btnMyCart;
    List<ProductModel> productModels;
    NavMenuItemAdapter adapter;

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_menu_products_list);

        db = FirebaseFirestore.getInstance();

        int categoryId = getIntent().getIntExtra("CategoryId", 0);

        //Nhan Category Image
        String categoryImageURL = getIntent().getStringExtra("CategoryImage");
        categoryImageView = findViewById(R.id.nav_menu_item_category_img);
        if (categoryImageURL != null) {
            Glide.with(this).load(categoryImageURL).into(categoryImageView);

            recyclerView = findViewById(R.id.nav_menu_products_rec);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            productModels = new ArrayList<>();
            adapter = new NavMenuItemAdapter(this, productModels);
            recyclerView.setAdapter(adapter);


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
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(NavMenuProductActivity.this, "Err" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }
    }
}