package com.example.caferestaurantsystem.models;

public class OrderModel {
    private String OrderId;
    private String Email;
    private String Address;
    private String OrderDate;
    private String Phone;
    private int TotalPrice;

    public OrderModel() {
    }

    public OrderModel(String orderId, String email, String address, String orderDate, String phone, int totalPrice) {
        OrderId = orderId;
        Email = email;
        Address = address;
        OrderDate = orderDate;
        Phone = phone;
        TotalPrice = totalPrice;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public int getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        TotalPrice = totalPrice;
    }
}
