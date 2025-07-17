package com.example.bakeryshop.Data.DTO;

import com.google.gson.annotations.SerializedName; // Likely used for API calls
import java.io.Serializable;

public class CartItemDTO implements Serializable {
    private static final long serialVersionUID = 1L; // Recommended for Serializable

    @SerializedName("cartID")
    private int cartID;
    @SerializedName("productID")
    private int productID;
    @SerializedName("quantity")
    private int quantity;
    // Add other fields from your actual CartItemDTO if they exist,
    // e.g., @SerializedName("userID") private int userID;

    public CartItemDTO() {
        // Empty constructor for Retrofit/Gson
    }

    public CartItemDTO(int cartID, int productID, int quantity) {
        this.cartID = cartID;
        this.productID = productID;
        this.quantity = quantity;
    }

    // Getters
    public int getCartID() {
        return cartID;
    }

    public int getProductID() {
        return productID;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters (if needed, especially for updating quantity locally)
    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}