package com.example.caferestaurantsystem.activities.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.adapters.manager.ManageProductAdapter;
import com.example.caferestaurantsystem.models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ManageProductsActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ArrayList<ProductModel> productModels;
    RecyclerView manageProductsRec;
    ManageProductAdapter manageProductAdapter;
    ProgressBar progressBar;
    ScrollView scrollView;
    Button btnCreateProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        //Tool Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Manage Products");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Progressbar
        progressBar = findViewById(R.id.manage_products_progressbar);
        scrollView = findViewById(R.id.manageProduct_scroll_view);
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();

        manageProductsRec = findViewById(R.id.manage_products_rec);
        manageProductsRec.setLayoutManager(new LinearLayoutManager(this));
        productModels = new ArrayList<>();
        manageProductAdapter = new ManageProductAdapter(this, productModels);
        manageProductsRec.setAdapter(manageProductAdapter);


            db.collection("Products")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    ProductModel productModel = document.toObject(ProductModel.class);
                                    productModels.add(productModel);

                                }
                                manageProductAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                                scrollView.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(ManageProductsActivity.this, "Err" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        //Create Product
        btnCreateProduct = findViewById(R.id.manageProduct_btnCreate);
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageProductsActivity.this, CreateProductActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) { // Nút quay lại
            onBackPressed(); // Gọi onBackPressed() để thoát khỏi activity hiện tại
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}