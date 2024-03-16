package com.example.caferestaurantsystem.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.List;

public class NavMenuItemAdapter extends RecyclerView.Adapter<NavMenuItemAdapter.ViewHolder> {
    private BottomSheetDialog bottomSheetDialog;
    Context context;
    List<ProductModel> productModels;

    public NavMenuItemAdapter(Context context, List<ProductModel> productModels) {
        this.context = context;
        this.productModels = productModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_menu_products_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final String fProductName = productModels.get(position).getProductName();
        final String fProductDescription = productModels.get(position).getProductDescription();
        final String fProductImage = productModels.get(position).getProductImage();
        final float fProductRating = productModels.get(position).getProductRating();
        final int fProductPrice = productModels.get(position).getProductPrice();
        final int fProductDiscount = productModels.get(position).getProductDiscount();

        Glide.with(context).load(productModels.get(position).getProductImage()).into(holder.productImage);
        holder.productName.setText(productModels.get(position).getProductName());
        holder.productDescription.setText(productModels.get(position).getProductDescription());
        holder.productRating.setText(String.format("%.1f", productModels.get(position).getProductRating()));
        holder.productPrice.setText("Price "+ String.format("%,d", productModels.get(position).getProductPrice()));
        holder.productDiscount.setText("Discount "+ productModels.get(position).getProductDiscount()+ "%");


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
                        //add to cart
                        AddedToCart(userEmail, productModels.get(position).getDocumentId(), totalQuantity, (int) totalPrice, productModels.get(position).getProductDiscount());
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
        return productModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productDescription, productPrice, productRating, productDiscount;
        Button addToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.nav_menu_item_product_name);
            productImage = itemView.findViewById(R.id.nav_menu_item_product_img);
            productDescription = itemView.findViewById(R.id.nav_menu_item_product_description);
            productPrice = itemView.findViewById(R.id.nav_menu_item_product_price);
            productRating = itemView.findViewById(R.id.nav_menu_item_product_rating);
            productDiscount = itemView.findViewById(R.id.nav_menu_item_product_discount);
        }
    }
}
