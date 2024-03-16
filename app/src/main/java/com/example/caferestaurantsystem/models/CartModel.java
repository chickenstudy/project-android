package com.example.caferestaurantsystem.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class CartModel implements Serializable {

    private String UserEmail;
    private String ProductDocumentId;
    private int Quantity;
    private int TotalPrice;
     private String DocumentId;

     String OrderDate;
     private int ProductDiscount;

    public CartModel() {
    }

    public CartModel(String userEmail, String productDocumentId, int quantity, int totalPrice, int productDiscount) {
        this.UserEmail = userEmail;
        this.ProductDocumentId = productDocumentId;
        this.Quantity = quantity;
        this.TotalPrice = totalPrice;
        this.ProductDiscount = productDiscount;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.OrderDate = dateFormat.format(calendar.getTime());
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public int getProductDiscount() {
        return ProductDiscount;
    }

    public void setProductDiscount(int productDiscount) {
        ProductDiscount = productDiscount;
    }

    public String getDocumentId() {
        return DocumentId;
    }

    public void setDocumentId(String documentId) {
        DocumentId = documentId;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getProductDocumentId() {
        return ProductDocumentId;
    }

    public void setProductDocumentId(String productDocumentId) {
        ProductDocumentId = productDocumentId;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public int getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        TotalPrice = totalPrice;
    }



}
