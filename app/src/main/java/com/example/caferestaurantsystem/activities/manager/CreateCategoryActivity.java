package com.example.caferestaurantsystem.activities.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.models.CategoryModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class CreateCategoryActivity extends AppCompatActivity {
    private ImageView cardImageView;
    private Uri imageUri;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String photoUrl;

    EditText categoryName;
    Button btnCreate;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        categoryName = findViewById(R.id.create_category_name);
        cardImageView = findViewById(R.id.create_category_img);
        btnCreate = findViewById(R.id.create_category_btnCreate);
        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Create Category");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnCreate.setOnClickListener(view -> {
            // Gọi uploadImage trước
            uploadImage();
        });

        cardImageView.setOnClickListener(view -> checkStoragePermission());
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                pickImageFromGallery();
            }
        } else {
            pickImageFromGallery();
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        launcher.launch(intent);
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        imageUri = data.getData();
                        cardImageView.setImageURI(imageUri);
                    }
                }
            });

    private void uploadImage() {
        if (imageUri != null) {
            final StorageReference myRef = storageReference.child("photo/categories/" + Objects.requireNonNull(imageUri.getLastPathSegment()));
            myRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> myRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        if (uri != null) {
                            photoUrl = uri.toString();
                            // Sau khi photoUrl được gán giá trị, gọi createCategory ở đây
                            createCategory();
                        }
                    }))
                    .addOnFailureListener(e -> Toast.makeText(CreateCategoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void createCategory() {
        String categoryNameStr = categoryName.getText().toString().trim();

        if (TextUtils.isEmpty(categoryNameStr)) {
            Toast.makeText(CreateCategoryActivity.this, "Please enter category name", Toast.LENGTH_SHORT).show();
            return;
        }

        int categoryId = generateUniqueId();
        CategoryModel categoryModel = new CategoryModel(categoryId, categoryNameStr, photoUrl);

        firebaseFirestore.collection("Category")
                .document(String.valueOf(categoryId))
                .set(categoryModel, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateCategoryActivity.this, "Category created successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(CreateCategoryActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private int generateUniqueId() {
        // Lấy thời gian hiện tại dưới dạng số long
        long currentTime = System.currentTimeMillis();

        // Chuyển thời gian hiện tại thành một chuỗi
        String currentTimeString = String.valueOf(currentTime);

        // Lấy 6 ký tự cuối cùng của chuỗi thời gian để tạo ID
        String uniqueIdString = currentTimeString.substring(currentTimeString.length() - 6);

        // Chuyển chuỗi thành số int và trả về
        return Integer.parseInt(uniqueIdString);
    }
}
