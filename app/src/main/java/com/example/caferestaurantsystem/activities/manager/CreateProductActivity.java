package com.example.caferestaurantsystem.activities.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.adapters.manager.CategoryBaseAdapter;
import com.example.caferestaurantsystem.models.CategoryModel;
import com.example.caferestaurantsystem.models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class CreateProductActivity extends AppCompatActivity {
    private ImageView cardImageView;
    private Uri imageUri;
    private Bitmap bitmap;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String photoURl;
    Context context;
    Spinner categorySpinner;
    CategoryBaseAdapter categoryBaseAdapter;


    EditText ProductName, ProductDescription, ProductPrice, ProductDiscount;

    Button btnCreate;
    MaterialCardView ProductImage;

    ArrayList<CategoryModel> categoryModels = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        Log.d("DEBUG", "Inside onCreate(): Value of imageUri: " + imageUri);
        //ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create new Product");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //data instance
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage =FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        //Load data to Category Spinner
        categorySpinner = findViewById(R.id.create_product_Category);



        firebaseFirestore.collection("Category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            categoryModels.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CategoryModel categoryModel = document.toObject(CategoryModel.class);
                                categoryModels.add(categoryModel);
                            }
                            // Create and set the adapter only after data is loaded
                            categoryBaseAdapter = new CategoryBaseAdapter(CreateProductActivity.this, categoryModels);
                            categorySpinner.setAdapter(categoryBaseAdapter);
                        }
                        else {
                            Toast.makeText(CreateProductActivity.this, "Err" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });






        ProductName = findViewById(R.id.create_product_name);
        ProductDescription = findViewById(R.id.create_product_description);
        ProductPrice = findViewById(R.id.create_product_price);
        ProductDiscount = findViewById(R.id.create_product_discount);
        ProductImage = findViewById(R.id.create_product_cardView);
        btnCreate = findViewById(R.id.create_product_btnCreate);
        cardImageView = findViewById(R.id.create_product_img);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage();
//                CreateProduct();
            }
        });
        ProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckStoragePermission();
//                PickImageFromGallery();
            }
        });
    }

    private void CheckStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }else {
                PickImageFromGallery();
            }
        }else {
            PickImageFromGallery();
        }
    }


    private void PickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        launcher.launch(intent);
    }
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent data  = result.getData();
                    if(data != null && data.getData() != null){
                        imageUri = data.getData();

                        //Convert image into bitmap
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),imageUri
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if(imageUri != null){
                        cardImageView.setImageBitmap(bitmap);

                    }
                }
            });
    private void UploadImage(){
        if(imageUri != null){
            final StorageReference myRef = storageReference.child("photo/products/"+imageUri.getLastPathSegment());
            myRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    myRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if(uri != null){
                                photoURl = uri.toString();
                                CreateProduct();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateProductActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    private  void CreateProduct(){
        String productNameStr = ProductName.getText().toString().trim();
        String productDescriptionStr = ProductDescription.getText().toString().trim();
        String productDiscountStr = ProductDiscount.getText().toString().trim();
        String productPriceStr = ProductPrice.getText().toString().trim();

        CategoryModel categoryModelSpinner = (CategoryModel) categorySpinner.getSelectedItem();
        int categoryId = categoryModelSpinner.getCategoryId();
        int productDiscount = 0;
        int productPrice = 0;
        // Data Validation
        if (imageUri == null) {
            Toast.makeText(CreateProductActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productNameStr) || TextUtils.isEmpty(productDescriptionStr) || TextUtils.isEmpty(productDiscountStr) || TextUtils.isEmpty(productPriceStr)) {
            Toast.makeText(CreateProductActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (productNameStr.length() > 50) {
            Toast.makeText(CreateProductActivity.this, "Product name is too long (max 50 characters)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (productDescriptionStr.length() > 250) {
            Toast.makeText(CreateProductActivity.this, "Product description is too long (max 250 characters)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (productDiscountStr.length() > 10 || productPriceStr.length() > 10) {
            Toast.makeText(CreateProductActivity.this, "Discount and price should not exceed 10 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            productDiscount = Integer.parseInt(productDiscountStr);
            productPrice = Integer.parseInt(productPriceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(CreateProductActivity.this, "Discount and price must be valid integers", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference documentReference =firebaseFirestore.collection("Products").document();
        ProductModel productModel = new ProductModel(productNameStr,productDescriptionStr,photoURl,productPrice,productDiscount,5,categoryId);
        documentReference.set(productModel, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    String DocId= documentReference.getId();
                    productModel.setDocumentId(DocId);
                    documentReference.set(productModel,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(CreateProductActivity.this,"Create Product successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateProductActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
