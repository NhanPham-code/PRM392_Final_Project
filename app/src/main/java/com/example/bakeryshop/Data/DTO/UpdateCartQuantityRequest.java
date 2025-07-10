package com.example.bakeryshop.Data.DTO;

public class UpdateCartQuantityRequest {
    private int cartId;
    private int quantity; // Đặt tên là quantity để khớp với Backend nếu có

    public UpdateCartQuantityRequest(int cartId, int quantity) {
        this.cartId = cartId;
        this.quantity = quantity;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}