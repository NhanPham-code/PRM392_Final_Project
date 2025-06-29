package com.example.bakeryshop.Data.DTO;

import com.google.gson.annotations.SerializedName;

public class ReadProductDTO {
    @SerializedName("productID")
    private int productId;
    @SerializedName("productName")
    private String productName;
    @SerializedName("description")
    private String description;
    @SerializedName("price")
    private double price;
    @SerializedName("stockQuantity")
    private int quantity;
    @SerializedName("imageURL")
    private String imageUrl;
    @SerializedName("categoryID")
    private int categoryId;
    @SerializedName("isAvailable")
    private boolean isAvailable;

    public ReadProductDTO() {
    }

    public ReadProductDTO(int productId, String productName, String description, double price, int quantity, String imageUrl, int categoryId, boolean isAvailable) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.isAvailable = isAvailable;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
