package com.example.caferestaurantsystem.ui.home;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.adapters.HomeCategoryAdapter;
import com.example.caferestaurantsystem.adapters.HomeCategoryProductsAdapter;
import com.example.caferestaurantsystem.adapters.HomeHighestRatingAdapter;
import com.example.caferestaurantsystem.interfaces.HomeLoadProducts;
import com.example.caferestaurantsystem.models.CategoryModel;
import com.example.caferestaurantsystem.models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HomeFragment extends Fragment implements HomeLoadProducts {

    ScrollView scrollView;
    ProgressBar progressBar;
    RecyclerView homeHighestRatingRecyclerView, homeCategoryRecyclerView,homeMenuRecyclerView;
    FirebaseFirestore db;



    ///////Search View
    EditText searchBox;
    private ArrayList<ProductModel> searchProductsList;
    RecyclerView searchRecycler;



    // Home Highest Rating
    ArrayList<ProductModel> homeHighestRatingList;
    HomeHighestRatingAdapter homeHighestRatingAdapter;

    //Home Category
    ArrayList<CategoryModel> categoryModelList;
    HomeCategoryAdapter homeCategoryAdapter;

    //Home Menu
    ArrayList<ProductModel> homeProducts;
    HomeCategoryProductsAdapter homeCategoryProductsAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home,container,false);

        db = FirebaseFirestore.getInstance();
        homeHighestRatingRecyclerView = root.findViewById(R.id.rec_home_highest_rating);
        homeCategoryRecyclerView = root.findViewById(R.id.rec_home_category);
        homeMenuRecyclerView = root.findViewById(R.id.rec_home_menu);
        scrollView = root.findViewById(R.id.home_scroll_view);
        progressBar = root.findViewById(R.id.home_progressbar);

        //Progressbar
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        // Highest Rating
        homeHighestRatingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        homeHighestRatingList = new ArrayList<>();
        homeHighestRatingAdapter = new HomeHighestRatingAdapter(getActivity(), homeHighestRatingList);
        homeHighestRatingRecyclerView.setAdapter(homeHighestRatingAdapter);

        db.collection("Products")
                .orderBy("productRating", Query.Direction.DESCENDING) // Sắp xếp theo rating giảm dần
                .limit(10) // Giới hạn kết quả trả về 10 sản phẩm
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            homeHighestRatingList.clear(); // Xóa danh sách cũ
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductModel productModel = document.toObject(ProductModel.class);
                                homeHighestRatingList.add(productModel);
                            }
                            homeHighestRatingAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                            progressBar.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getActivity(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        // Home category
        homeCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        categoryModelList = new ArrayList<>();
        homeCategoryAdapter = new HomeCategoryAdapter(getContext(),categoryModelList,getActivity(),this);
        homeCategoryRecyclerView.setAdapter(homeCategoryAdapter);

        db.collection("Category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CategoryModel categoryModel = document.toObject(CategoryModel.class);
                                categoryModelList.add(categoryModel);
                                homeCategoryAdapter.notifyDataSetChanged();
                            }
                            categoryModelList.sort(Comparator.comparing(CategoryModel::getCategoryId));
                            homeCategoryAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(),"Err"+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

       ////Search Product
        searchRecycler = root.findViewById(R.id.home_search_rec);
        searchBox = root.findViewById(R.id.home_search_box);
        searchProductsList = new ArrayList<>();
        homeHighestRatingAdapter= new HomeHighestRatingAdapter(getContext(),searchProductsList);
        searchRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        searchRecycler.setAdapter(homeHighestRatingAdapter);
        searchRecycler.setHasFixedSize(true);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {

                    searchProductsList.clear();
                    homeHighestRatingAdapter.notifyDataSetChanged();
                }else {
                    searchProduct(editable.toString());

                }
            }
        });
        return root;
    }

    private void searchProduct(String searchQuery) {
        if (!searchQuery.isEmpty()) {
            // Loại bỏ dấu tiếng Việt và chuyển thành chữ thường
            String searchQueryNormalized = removeVietnameseAccents(searchQuery);

            // Sắp xếp kết quả tìm kiếm theo tên sản phẩm đã được chuẩn hóa
            Query query = db.collection("Products")
                    .orderBy("productName")
                    .startAt(searchQueryNormalized)
                    .endAt(searchQueryNormalized + "\uf8ff");

            // Thực hiện tìm kiếm
            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                searchProductsList.clear();
                                homeHighestRatingAdapter.notifyDataSetChanged();
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    ProductModel productModel = document.toObject(ProductModel.class);
                                    searchProductsList.add(productModel);
                                }
                                //Log.d("TAG", "Error searching for products111111: "+ searchQueryNormalized);
                                //Log.d("TAG", "Error searching for products222: "+ searchProductsList.size());
                            } else {
                                Log.d("TAG", "Error searching for products: ", task.getException());
                            }
                        }
                    });
        }
    }





    @Override
    public void CallBack(int position, ArrayList<ProductModel> productModels) {
        homeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        homeCategoryProductsAdapter =  new HomeCategoryProductsAdapter(getContext(),productModels);
        homeCategoryProductsAdapter.notifyDataSetChanged();
        homeMenuRecyclerView.setAdapter(homeCategoryProductsAdapter);
    }

    public  String removeVietnameseAccents(String str) {
        Pattern pattern = Pattern.compile("[\\u0300-\\u036F]");
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("");
    }
}
