package com.example.caferestaurantsystem.ui.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.activities.manager.CreateCategoryActivity;
import com.example.caferestaurantsystem.activities.manager.ManageProductsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ManagerFragment extends Fragment {
    TextView txtFullName, txtManageProducts, txtManageCategories, txtManageOrder;
    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view =  inflater.inflate(R.layout.fragment_nav_manager,container,false);

        txtFullName = view.findViewById(R.id.nav_manager_fullName);
        // Get the current user's email
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Reference the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference the 'users' collection in Firestore and find the user document by email
        db.collection("users")
                .whereEqualTo("email", currentUserEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Populate the TextViews with user data
                            txtFullName.setText("( " + document.getString("fullName") + " )");

                        }
                    }
                });


        txtManageProducts =  view.findViewById(R.id.nav_manager_manageProduct);
        txtManageCategories =  view.findViewById(R.id.nav_manager_manageCategories);
        txtManageOrder =  view.findViewById(R.id.nav_manager_manageOrder);

        // Set click listeners for the TextViews
        txtManageProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the ManageProductsActivity
                Intent intent = new Intent(getContext(), ManageProductsActivity.class);
                startActivity(intent);
            }
        });
        txtManageCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the ManageCategoriesActivity
                Intent intent = new Intent(getContext(), CreateCategoryActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }
}
