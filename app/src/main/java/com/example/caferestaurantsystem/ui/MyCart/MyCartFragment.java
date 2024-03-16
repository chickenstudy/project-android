package com.example.caferestaurantsystem.ui.MyCart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.activities.PlacedOrderActivity;
import com.example.caferestaurantsystem.adapters.MyCartAdapter;
import com.example.caferestaurantsystem.models.CartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;


public class MyCartFragment extends Fragment {
    private int totalBill;
     Context context ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    RecyclerView recyclerView;
    MyCartAdapter myCartAdapter;
    ArrayList<CartModel> cartModels;
    TextView totalAmount;
    Button buyNow;

    public MyCartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_cart,container,false);
        recyclerView = root.findViewById(R.id.myCart_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        cartModels =  new ArrayList<>();
        myCartAdapter =  new MyCartAdapter(getActivity(), cartModels);
        recyclerView.setAdapter(myCartAdapter);

        String userEmail = auth.getCurrentUser().getEmail();
        //Log.d("MyCartAdapter", "ItemCount: " + userEmail);
        db.collection("AddToCart")
                .whereEqualTo("userEmail",userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String documentId = document.getId();

                                CartModel cartModel = document.toObject(CartModel.class);

                                cartModel.setDocumentId(documentId);

                                cartModels.add(cartModel);

                            }

                            if (cartModels == null || cartModels.isEmpty()) {

                                ConstraintLayout constraintLayout2 = root.findViewById(R.id.ConstraintLayout2);
                                constraintLayout2.setVisibility(View.GONE);

                                ConstraintLayout constraintLayout1 = root.findViewById(R.id.ConstraintLayout1);
                                constraintLayout1.setVisibility(View.VISIBLE);
                            } else {

                                ConstraintLayout constraintLayout2 = root.findViewById(R.id.ConstraintLayout2);
                                constraintLayout2.setVisibility(View.VISIBLE);

                                ConstraintLayout constraintLayout1 = root.findViewById(R.id.ConstraintLayout1);
                                constraintLayout1.setVisibility(View.GONE);


                                myCartAdapter.notifyDataSetChanged();
                            }

                        }

                        else {

                        }
                    }
                });

        //Nhan totalAmount tu MyCartAdapter
         totalAmount = root.findViewById(R.id.my_cart_totalAmount);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,new IntentFilter("MyTotalAmount"));

        //Buy Now
        buyNow = root.findViewById(R.id.my_cart_btnBuyNow);
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PlacedOrderActivity.class);
                intent.putExtra("totalBill", totalBill);
                intent.putExtra("listCarts",  (Serializable) cartModels);


                startActivity(intent);
            }
        });

        return root;
    }



    public BroadcastReceiver mMessageReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             totalBill = intent.getIntExtra("totalAmount",0);
            totalAmount.setText("Total Bill: "+String.format("%,d", totalBill)+" VNĐ");
        }
    };
}