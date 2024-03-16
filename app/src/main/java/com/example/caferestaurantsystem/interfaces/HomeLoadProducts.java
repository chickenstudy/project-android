package com.example.caferestaurantsystem.interfaces;

import com.example.caferestaurantsystem.models.ProductModel;

import java.util.ArrayList;

public interface HomeLoadProducts {
    public void CallBack(int categoryId, ArrayList<ProductModel> productModels);
}
