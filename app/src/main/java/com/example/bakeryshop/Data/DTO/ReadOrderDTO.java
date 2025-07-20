package com.example.bakeryshop.Data.DTO;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ReadOrderDTO implements Serializable {
    @SerializedName("OrderID")
    public int orderID;

    @SerializedName("UserID")
    public int userID;

    @SerializedName("OrderDate")
    public Date orderDate;

    @SerializedName("TotalAmount")
    public double totalAmount;

    @SerializedName("ShippingAddress")
    public String shippingAddress;

    @SerializedName("OrderStatus")
    public String orderStatus;

    @SerializedName("PaymentMethod")
    public String paymentMethod;

    @SerializedName("PaymentStatus")
    public String paymentStatus;

    @SerializedName("OrderDetails")
    public ArrayList<ReadOrderDetailDTO> orderDetails;

    public ReadOrderDTO() {
    }

    public ReadOrderDTO(int orderID,int userID, Date orderDate, double totalAmount, String shippingAddress, String orderStatus, String paymentMethod, String paymentStatus, ArrayList<ReadOrderDetailDTO> orderDetails) {
        this.orderID = orderID;
        this.userID = userID;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.orderStatus = orderStatus;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.orderDetails = orderDetails;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public ArrayList<ReadOrderDetailDTO> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(ArrayList<ReadOrderDetailDTO> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
