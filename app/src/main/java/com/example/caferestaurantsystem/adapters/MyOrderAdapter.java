package com.example.caferestaurantsystem.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caferestaurantsystem.R;
import com.example.caferestaurantsystem.models.OrderModel;

import java.util.ArrayList;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    Context context;
    ArrayList<OrderModel> orderModels;


    public MyOrderAdapter(Context context, ArrayList<OrderModel> orderModels) {
        this.context = context;
        this.orderModels = orderModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         return new MyOrderAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.orderId.setText(orderModels.get(position).getOrderId());
        holder.email.setText(orderModels.get(position).getEmail());
        holder.phone.setText(orderModels.get(position).getPhone());
        holder.address.setText(orderModels.get(position).getAddress());
        holder.totalPrice.setText(String.format("%,d",orderModels.get(position).getTotalPrice())+" VNĐ");
        holder.orderDate.setText(orderModels.get(position).getOrderDate());

    }

    @Override
    public int getItemCount() {

        return orderModels.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, email,phone, address, orderDate, totalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.my_order_item_OrderId);
            email = itemView.findViewById(R.id.my_order_item_email);
            phone = itemView.findViewById(R.id.my_order_item_phone);
            address = itemView.findViewById(R.id.my_order_item_address);
            totalPrice = itemView.findViewById(R.id.my_order_item_totalPrice);
            orderDate = itemView.findViewById(R.id.my_order_item_orderDate);

        }
    }
}
