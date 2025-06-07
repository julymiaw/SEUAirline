package com.seu.airline.entity;

import java.time.LocalDateTime;

public class Order {
    private String orderId;
    private String customerId;
    private String buyerId;
    private String flightId;
    private String seatType;
    private String orderStatus;
    private LocalDateTime orderTime;

    // 构造函数
    public Order() {
    }

    public Order(String customerId, String buyerId, String flightId,
            String seatType, String orderStatus, LocalDateTime orderTime) {
        this.customerId = customerId;
        this.buyerId = buyerId;
        this.flightId = flightId;
        this.seatType = seatType;
        this.orderStatus = orderStatus;
        this.orderTime = orderTime;
    }

    // Getter和Setter方法
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    } // ✅ 修改为String

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getBuyerId() {
        return buyerId;
    } // ✅ 修改为String

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customerId='" + customerId + '\'' + // ✅ 字符串格式
                ", buyerId='" + buyerId + '\'' + // ✅ 字符串格式
                ", flightId='" + flightId + '\'' +
                ", seatType='" + seatType + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderTime=" + orderTime +
                '}';
    }
}