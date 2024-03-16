package com.example.caferestaurantsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.caferestaurantsystem.MainActivity;
import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.models.CartModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PlacedOrderActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    TextView totalBill;
    EditText phone,address;
    Button order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placed_order);
        totalBill = findViewById(R.id.order_total_bill);
        phone = findViewById(R.id.order_phone);
        address = findViewById(R.id.order_input_address);
        order = findViewById(R.id.btnOrder);

        int bill = getIntent().getIntExtra("totalBill",0);
        //Toast.makeText(getApplicationContext(), "Total Bill: " + bill + " VNĐ", Toast.LENGTH_SHORT).show();
        totalBill.setText("Total Bill: " + String.format("%,d",bill) + "VNĐ");

        ArrayList<CartModel> cartModels = (ArrayList<CartModel>) getIntent().getSerializableExtra("listCarts");

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_address = address.getText().toString().trim();
                String str_phone = phone.getText().toString().trim();

                if (TextUtils.isEmpty(str_address) ) {
                    Toast.makeText(getApplicationContext(), "Please enter your address", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(str_phone))
                {
                    Toast.makeText(getApplicationContext(), "Please enter your phone number", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Lấy email đang đăng nhập
                    String userEmail = auth.getCurrentUser().getEmail();


                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String OrderDate = dateFormat.format(calendar.getTime());

                    // Tạo một Map để lưu thông tin đơn hàng
                    Map<String, Object> orderData = new HashMap<>();
                    orderData.put("Email", userEmail);
                    orderData.put("Phone", str_phone);
                    orderData.put("Address", str_address);
                    orderData.put("OrderDate", OrderDate);
                    orderData.put("TotalPrice", bill);
                    // Thêm thông tin đơn hàng vào Firestore
                    db.collection("Orders")
                            .add(orderData)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    address.setText("");
                                    phone.setText("");

                                    for (CartModel cartModel : cartModels) {
                                        String documentId = cartModel.getDocumentId();
                                        if (documentId != null) {
                                            db.collection("AddToCart").document(documentId).delete();
                                        }
                                    }
                                    cartModels.clear();

                                    Intent intent =  new Intent(PlacedOrderActivity.this, MainActivity.class);
                                    Toast.makeText(getApplicationContext(), "Order placed successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Failed to place order", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });


    }
}