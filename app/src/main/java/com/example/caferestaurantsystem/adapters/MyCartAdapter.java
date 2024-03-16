package com.example.caferestaurantsystem.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.models.CartModel;
import com.example.caferestaurantsystem.models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
    Context context;
    ArrayList<CartModel> cartModels;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth  = FirebaseAuth.getInstance();



    public MyCartAdapter(Context context, ArrayList<CartModel> cartModels) {
        this.context = context;
        this.cartModels = cartModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String productDocumentId = cartModels.get(position).getProductDocumentId();

        db.collection("Products")
                .whereEqualTo("documentId", productDocumentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        ProductModel product = queryDocumentSnapshots.getDocuments().get(0).toObject(ProductModel.class);

                        if (product != null) {
                            holder.productName.setText(product.getProductName());
                            holder.productPrice.setText(String.format("%,d", product.getProductPrice()));
                            holder.productDiscount.setText(String.valueOf(cartModels.get(position).getProductDiscount())+ "%");
                        }
                    } else {
                        // Không tìm thấy sản phẩm với productId tương ứng
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi khi truy vấn Firestore
                });

       holder.quantity.setText(String.valueOf(cartModels.get(position).getQuantity()));
       holder.totalPrice.setText(String.format("%,d", cartModels.get(position).getTotalPrice()));

        //Delete Item
        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xác nhận xóa");
                builder.setMessage("Bạn có chắc chắn muốn xóa mục này?");

                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý xóa mục
                        db.collection("AddToCart")
                                .document(cartModels.get(position).getDocumentId())
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            cartModels.remove(cartModels.get(position));
                                            int updatedTotalAmount = calculateTotalAmount();
                                            sendTotalAmountBroadcast(updatedTotalAmount);
                                            notifyDataSetChanged();
                                            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Err" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });

                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        //pass totalAmount to My Cart Fragment
        boolean check = true;
        if(check){
            int updatedTotalAmount = calculateTotalAmount();

            // Gửi updatedTotalAmount thông qua broadcast
            sendTotalAmountBroadcast(updatedTotalAmount);
            check = false;
        }
    }

    private void sendTotalAmountBroadcast(int updatedTotalAmount) {
        Intent intent = new Intent("MyTotalAmount");
        intent.putExtra("totalAmount", updatedTotalAmount);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private int calculateTotalAmount() {
        int totalAmount = 0;
        for (CartModel cartModel : cartModels) {
            totalAmount += cartModel.getTotalPrice();
        }
        return totalAmount;
    }

    @Override
    public int getItemCount() {

        return cartModels.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName,productPrice, productDiscount, quantity, totalPrice;
        ImageView deleteItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.my_cart_item_productName);
            productPrice = itemView.findViewById(R.id.my_cart_item_productPrice);
            quantity = itemView.findViewById(R.id.my_cart_item_quantity);
            totalPrice = itemView.findViewById(R.id.my_cart_item_totalPrice);
            deleteItem = itemView.findViewById(R.id.my_cart_item_delete);
            productDiscount = itemView.findViewById(R.id.my_cart_item_productDiscount);
        }
    }
}
