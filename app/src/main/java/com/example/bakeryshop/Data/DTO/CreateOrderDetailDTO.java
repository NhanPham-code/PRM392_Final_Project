// src/main/java/com/example/bakeryshop/Data/DTO/CreateOrderDetailDTO.java
package com.example.bakeryshop.Data.DTO;

import com.google.gson.annotations.SerializedName;

public class CreateOrderDetailDTO {
    @SerializedName("orderID") // <--- QUAN TRỌNG: Gửi OrderID trong body
    private int orderID;
    @SerializedName("productID")
    private int productID;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("price")
    private double price;
    @SerializedName("subtotal")
    private double subtotal;

    public CreateOrderDetailDTO(int orderID, int productID, int quantity, double price, double subtotal) {
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }

    // Getters và Setters (nếu cần)
    public int getOrderID() { return orderID; }
    public int getProductID() { return productID; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getSubtotal() { return subtotal; }
}