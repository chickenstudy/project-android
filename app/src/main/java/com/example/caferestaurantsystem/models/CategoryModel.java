package com.example.caferestaurantsystem.models;

public class CategoryModel {
    private int CategoryId;
    private String CategoryName;
    private String CategoryImage;

    public CategoryModel() {
    }

    public CategoryModel(int categoryId, String categoryName, String categoryImage) {
        CategoryId = categoryId;
        CategoryName = categoryName;
        CategoryImage = categoryImage;
    }



    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getCategoryImage() {
        return CategoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        CategoryImage = categoryImage;
    }
}
