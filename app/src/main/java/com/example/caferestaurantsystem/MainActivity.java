package com.example.caferestaurantsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.caferestaurantsystem.activities.LoginActivity;
import com.example.caferestaurantsystem.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // Get the current user's email
        // Get the current user's email
        //
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_profile ,R.id.nav_restaurant_menu, R.id.nav_my_order, R.id.nav_my_cart,R.id.nav_manager)
                .setOpenableLayout(drawer)
                .build();
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
                            try {
                                int roleId = Math.toIntExact(document.getLong("roleId"));
                                if(roleId == 2) {
//                                if (roleId == 1&&roleId==2&&roleId==3) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Menu menu = navigationView.getMenu();
                                            MenuItem adminMenuItem = menu.findItem(R.id.nav_manager);
                                            adminMenuItem.setVisible(true);
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Menu menu = navigationView.getMenu();
                                            MenuItem adminMenuItem = menu.findItem(R.id.nav_manager);
                                            adminMenuItem.setVisible(false);
                                        }
                                    });
                                }
                            } catch (NumberFormatException e) {
                                // Xử lý ngoại lệ
                            }
                        }
                    }
                });


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        TextView logOut = findViewById(R.id.btn_logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}