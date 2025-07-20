// src/main/java/com/example/bakeryshop/Data/DTO/CreateOrderDTO.java
package com.example.bakeryshop.Data.DTO;

import com.google.gson.annotations.SerializedName;

public class CreateOrderDTO {
    @SerializedName("paymentMethod")
    private String paymentMethod;
    @SerializedName("shippingAddress")
    private String shippingAddress;
    @SerializedName("totalAmount")
    private double totalAmount;

    // Chú ý: Không có UserID ở đây, vì Ocelot sẽ tự gán từ token
    // Và cũng không có OrderDetails ở đây, vì chúng ta sẽ gửi riêng

    public CreateOrderDTO(String paymentMethod, String shippingAddress, double totalAmount) {
        this.paymentMethod = paymentMethod;
        this.shippingAddress = shippingAddress;
        this.totalAmount = totalAmount;
    }

    // Getters và Setters (nếu cần, nhưng thường chỉ cần constructor và getter cho Retrofit)
    public String getPaymentMethod() { return paymentMethod; }
    public String getShippingAddress() { return shippingAddress; }
    public double getTotalAmount() { return totalAmount; }
}