package com.example.caferestaurantsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caferestaurantsystem.MainActivity;
import com.example.caferestaurantsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class UpdateUserProfileActivity extends AppCompatActivity {

    Button btnUpdate;
    EditText fullName,address, phone;
    ProgressBar progressBar ;
    FirebaseAuth auth;
    FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_profile);

        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        fullName = findViewById(R.id.update_profile_fullName);
        address = findViewById(R.id.update_profile_address);
        phone = findViewById(R.id.update_profile_phone);
        btnUpdate =findViewById(R.id.update_profile_btnUpdate);


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateUser();
                progressBar.setVisibility(View.VISIBLE);
            }

        });
    }
    private void UpdateUser() {
        String updatedFullName  = fullName.getText().toString();
        String updatedPhone  = phone.getText().toString();
        String updatedAddress  = address.getText().toString();


        if(TextUtils.isEmpty(updatedFullName )){
            Toast.makeText(this, "FullName is Empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(updatedPhone )){
            Toast.makeText(this, "Phone is Empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(updatedAddress )){
            Toast.makeText(this, "Address is Empty!", Toast.LENGTH_SHORT).show();
            return;
        }


        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", currentUserEmail)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().update("fullName", updatedFullName);
                            document.getReference().update("phone", updatedPhone);
                            document.getReference().update("address", updatedAddress);
                            // Cập nhật TextViews để hiển thị dữ liệu mới
                            fullName.setText("FullName: " + updatedFullName);
                            phone.setText("Phone: " + updatedPhone);
                            address.setText("Address: " + updatedAddress);
                            Toast.makeText(getApplicationContext(), "Update Profile Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UpdateUserProfileActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Update Profile Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}