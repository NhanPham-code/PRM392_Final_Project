package com.example.bakeryshop.Data.DTO;

public class CartDisplayItem {
    private CartItemDTO cartItem;
    private ReadProductDTO product;
    private boolean isSelected; // Đảm bảo trường này tồn tại

    public CartDisplayItem(CartItemDTO cartItem, ReadProductDTO product) {
        this.cartItem = cartItem;
        this.product = product;
        this.isSelected = false; // Mặc định là chưa chọn khi khởi tạo
    }

    public CartItemDTO getCartItem() {
        return cartItem;
    }

    public ReadProductDTO getProduct() {
        return product;
    }

    // Các phương thức getter và setter cho isSelected - ĐẢM BẢO CÓ CÁC PHƯƠNG THỨC NÀY
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}