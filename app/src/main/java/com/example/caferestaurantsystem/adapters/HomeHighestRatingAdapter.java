package com.example.caferestaurantsystem.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.models.CartModel;
import com.example.caferestaurantsystem.models.ProductModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeHighestRatingAdapter extends RecyclerView.Adapter<HomeHighestRatingAdapter.ViewHolder> {
    private BottomSheetDialog bottomSheetDialog;

    private Context context;
    private ArrayList<ProductModel> highestRatingList;

    public HomeHighestRatingAdapter(Context context, ArrayList<ProductModel> highestRatingList) {
        this.context = context;
        this.highestRatingList = highestRatingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_hot_combo_items, parent, false)) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final String fProductName = highestRatingList.get(position).getProductName();
        final String fProductDescription = highestRatingList.get(position).getProductDescription();
        final String fProductImage = highestRatingList.get(position).getProductImage();
        final float fProductRating = highestRatingList.get(position).getProductRating();
        final int fProductPrice = highestRatingList.get(position).getProductPrice();
        final int fProductDiscount = highestRatingList.get(position).getProductDiscount();

        holder.highestRatingName.setText(highestRatingList.get(position).getProductName());
        Glide.with(context).load(highestRatingList.get(position).getProductImage()).into(holder.highestRatingImage);
        holder.highestRatingRating.setText(String.format("%.1f", highestRatingList.get(position).getProductRating()));
        holder.highestRatingPrice.setText("Price "+ String.format("%,d", highestRatingList.get(position).getProductPrice()));
        holder.highestRatingDiscount.setText("Discount "+ highestRatingList.get(position).getProductDiscount()+ "%");


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            boolean check = true;
            int totalQuantity = 1;
            float totalPrice = fProductPrice;

            @Override
            public void onClick(View view) {


                bottomSheetDialog = new BottomSheetDialog(context,R.style.BottomSheetTheme);

                View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout,null);

                Button addToCartBtn = sheetView.findViewById(R.id.bottom_sheet_addToCart_btn);
                ImageView bottomProductImg = sheetView.findViewById(R.id.bottom_sheet_img);
                TextView bottomProductName = sheetView.findViewById(R.id.bottom_sheet_product_name);
                TextView bottomProductDescription = sheetView.findViewById(R.id.bottom_sheet_product_description);
                TextView bottomProductRating = sheetView.findViewById(R.id.bottom_sheet_product_rating);
                TextView bottomProductPrice = sheetView.findViewById(R.id.bottom_sheet_product_price);
                TextView bottomProductDiscount = sheetView.findViewById(R.id.bottom_sheet_discount);
                ImageView addItemImage = sheetView.findViewById(R.id.bottom_sheet_addItem);
                ImageView removeItemImage = sheetView.findViewById(R.id.bottom_sheet_removeItem);
                TextView quantity = sheetView.findViewById(R.id.bottom_sheet_quantity);



                addItemImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        check = false;
                        totalQuantity++;
                        quantity.setText(String.valueOf(totalQuantity));
                        totalPrice = (fProductPrice - (fProductPrice * fProductDiscount / 100)) * totalQuantity; // Tính giá tổng sau giảm giá
                    }
                });
                removeItemImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(totalQuantity > 1 ) {
                            check = false;
                            totalQuantity--;
                            quantity.setText(String.valueOf(totalQuantity));
                            totalPrice = (fProductPrice - (fProductPrice * fProductDiscount / 100)) * totalQuantity; // Tính giá tổng sau giảm giá
                        }
                    }
                });
                addToCartBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(check){
                            quantity.setText(String.valueOf(totalQuantity));
                            totalPrice = (fProductPrice - (fProductPrice * fProductDiscount / 100)) * totalQuantity; // Tính giá tổng sau giảm giá
                        }
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String userEmail = auth.getCurrentUser().getEmail();
                        AddedToCart(userEmail, highestRatingList.get(position).getDocumentId(), totalQuantity, (int) totalPrice, highestRatingList.get(position).getProductDiscount());
                        Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();

                    }
                });

                //Load to Bottom Dialog
                Glide.with(context).load(fProductImage).into(bottomProductImg);
                bottomProductName.setText(fProductName);
                bottomProductDescription.setText(fProductDescription);
                bottomProductRating.setText(String.format("%.1f", fProductRating));
                bottomProductPrice.setText("Price "+ String.format("%,d", fProductPrice));
                bottomProductDiscount.setText("Discount "+ fProductDiscount  +"%");
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
            }
        });
    }

    private String getUserEmailFromSharedPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("userEmail", ""); // Trả về email lưu trong SharedPreferences
    }



    private void AddedToCart(String userEmail, String documentId, int quantity, int totalPrice, int productDiscount) {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference orderDetailRef = db.collection("AddToCart").document(); // Tạo một tài liệu mới

        // Tạo một đối tượng OrderDetail
        CartModel cartModel = new CartModel(userEmail, documentId, quantity, totalPrice, productDiscount);

        // Thêm đối tượng vào Firestore
        orderDetailRef.set(cartModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Đã thêm đơn hàng thành công
                        Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xảy ra lỗi khi thêm đơn hàng
                        Toast.makeText(context, "Failed to add to Cart", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public int getItemCount() {
        return highestRatingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView highestRatingImage;
        TextView highestRatingName, highestRatingDescription, highestRatingRating, highestRatingPrice, highestRatingDiscount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            highestRatingName = itemView.findViewById(R.id.home_hot_combo_item_name);
            highestRatingImage = itemView.findViewById(R.id.home_hot_combo_item_img);

            highestRatingDescription = itemView.findViewById(R.id.home_hot_combo_item_description);
            highestRatingRating = itemView.findViewById(R.id.home_hot_combo_item_rating);
            highestRatingPrice = itemView.findViewById(R.id.home_hot_combo_item_price);
            highestRatingDiscount = itemView.findViewById(R.id.home_hot_combo_item_discount);

        }
    }
}
