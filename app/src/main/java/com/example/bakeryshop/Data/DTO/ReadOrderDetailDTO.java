package com.example.bakeryshop.Data.DTO;

import com.google.gson.annotations.SerializedName;


public class ReadOrderDetailDTO {
    @SerializedName("orderDetailID")
    public int orderDetailID;
    @SerializedName("orderID")
    public int orderID;
    @SerializedName("productID")
    public int productID;
    @SerializedName("productName")
    public String productName;
    @SerializedName("productImg")
    public String productImg;
    @SerializedName("quantity")
    public int quantity;
    @SerializedName("unitPrice")
    public double unitPrice;
    @SerializedName("lineTotal")
    public double totalPrice;


    public ReadOrderDetailDTO() {
    }

    public ReadOrderDetailDTO(int orderDetailID, int orderID, int productID, String productName, String productImg, int quantity, double unitPrice, double totalPrice) {
        this.orderDetailID = orderDetailID;
        this.orderID = orderID;
        this.productID = productID;
        this.productName = productName;
        this.productImg = productImg;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    public int getOrderDetailID() {
        return orderDetailID;
    }

    public void setOrderDetailID(int orderDetailID) {
        this.orderDetailID = orderDetailID;
    }

    public int getOrderID() {
        return orderID;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
