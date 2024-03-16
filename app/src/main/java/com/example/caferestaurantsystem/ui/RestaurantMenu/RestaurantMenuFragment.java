package com.example.caferestaurantsystem.ui.RestaurantMenu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.adapters.HomeCategoryAdapter;
import com.example.caferestaurantsystem.adapters.NavRestaurantMenuAdapter;
import com.example.caferestaurantsystem.models.CategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class RestaurantMenuFragment extends Fragment {
    FirebaseFirestore db;
    RecyclerView navRestaurantMenuRecyclerView;
    ProgressBar progressBar;
    ScrollView scrollView;

    //Nav Restaurant Menu
    List<CategoryModel> categoryModelList;
    NavRestaurantMenuAdapter navRestaurantMenuAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View root = inflater.inflate(R.layout.fragment_restaurant_menu, container, false);

        db = FirebaseFirestore.getInstance();
        navRestaurantMenuRecyclerView = root.findViewById(R.id.nav_restaurant_menu_rec);
        progressBar = root.findViewById(R.id.nav_restaurant_menu_progressbar);
        scrollView = root.findViewById(R.id.nav_restaurant_menu_scroll_view);

        //Progressbar
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        // Nav restaurant menu

       navRestaurantMenuRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        categoryModelList = new ArrayList<>();
        navRestaurantMenuAdapter = new NavRestaurantMenuAdapter(getActivity(), categoryModelList);
        navRestaurantMenuRecyclerView.setAdapter(navRestaurantMenuAdapter);

        db.collection("Category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CategoryModel categoryModel = document.toObject(CategoryModel.class);
                                categoryModelList.add(categoryModel);
                                navRestaurantMenuAdapter.notifyDataSetChanged();
                                //Progressbar
                                scrollView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(getActivity(),"Err"+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return root;
    }
}