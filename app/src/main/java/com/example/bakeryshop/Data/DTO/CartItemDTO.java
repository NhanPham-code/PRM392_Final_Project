package com.example.bakeryshop.Data.DTO;

public class CartItemDTO {
    private int cartID;
    private int userID;
    private int productID;
    private int quantity;
    private String latestUpdate;

    public int getCartID() { return cartID; }
    public void setCartID(int cartID) { this.cartID = cartID; }
    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }
    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }


}
