package com.example.bakeryshop.Data.DTO;

import java.io.Serializable;

public class CartDisplayItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private CartItemDTO cartItem;
    private ReadProductDTO product;
    private boolean isSelected; // Trường để theo dõi trạng thái chọn

    public CartDisplayItem(CartItemDTO cartItem, ReadProductDTO product) {
        this.cartItem = cartItem;
        this.product = product;
        this.isSelected = false; // Mặc định là chưa chọn khi khởi tạo
    }

    public CartDisplayItem(CartItemDTO cartItem, ReadProductDTO product, boolean isSelected) {
        this.cartItem = cartItem;
        this.product = product;
        this.isSelected = isSelected;
    }

    public CartItemDTO getCartItem() { return cartItem; }
    public void setCartItem(CartItemDTO cartItem) { this.cartItem = cartItem; }

    public ReadProductDTO getProduct() { return product; }
    public void setProduct(ReadProductDTO product) { this.product = product; }

    public boolean isSelected() { return isSelected; } // Getter
    public void setSelected(boolean selected) { isSelected = selected; } // Setter

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartDisplayItem that = (CartDisplayItem) o;
        // So sánh dựa trên cartItem ID để xác định cùng một mục giỏ hàng
        return cartItem != null ? cartItem.getCartID() == that.getCartItem().getCartID() : that.getCartItem() == null;
    }

    @Override
    public int hashCode() {
        return cartItem != null ? Integer.valueOf(cartItem.getCartID()).hashCode() : 0;
    }
}