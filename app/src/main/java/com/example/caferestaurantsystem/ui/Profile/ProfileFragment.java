package com.example.caferestaurantsystem.ui.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.activities.UpdateUserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProfileFragment extends Fragment {
    private TextView fullNameTextView, emailTextView, phoneTextView, addressTextView;
    Button updateProfile;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize TextViews
        fullNameTextView = view.findViewById(R.id.user_profile_fullName);
        emailTextView = view.findViewById(R.id.user_profile_email);
        phoneTextView = view.findViewById(R.id.user_profile_phone);
        addressTextView = view.findViewById(R.id.user_profile_address);

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
                            fullNameTextView.setText("FullName: " + document.getString("fullName"));
                            emailTextView.setText("Email: " + currentUserEmail);
                            phoneTextView.setText("Phone: " + document.getString("phone"));
                            addressTextView.setText("Address: " + document.getString("address"));
                        }
                    }
                });

        updateProfile = view.findViewById(R.id.user_profile_btnUpdate);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(getActivity(), UpdateUserProfileActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}