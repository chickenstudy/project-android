package com.example.caferestaurantsystem.ui.Order;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.adapters.MyOrderAdapter;
import com.example.caferestaurantsystem.models.OrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class OrderFragment extends Fragment {
    Context context ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    RecyclerView recyclerView;
    MyOrderAdapter myOrderAdapter;
    ArrayList<OrderModel> orderModels;

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_order,container,false);
        recyclerView = root.findViewById(R.id.order_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        orderModels =  new ArrayList<>();
        myOrderAdapter =  new MyOrderAdapter(getActivity(), orderModels);
        recyclerView.setAdapter(myOrderAdapter);

        String userEmail = auth.getCurrentUser().getEmail();

        db.collection("Orders")
                .whereEqualTo("Email", userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {


                                String documentId = document.getId();

                                OrderModel orderModel = document.toObject(OrderModel.class);

                                orderModel.setOrderId(documentId);

                                orderModels.add(orderModel);

                            }
                            myOrderAdapter.notifyDataSetChanged();

                        }

                        else {

                        }
                    }
                });


        return root;
    }
}