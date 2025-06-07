package com.seu.airline.entity;

import java.time.LocalDateTime;

public class Order {
    private Integer orderId; // 注意：数据库中可能是自增主键
    private Integer customerId; // 对应GuestID
    private Integer buyerId; // 对应HostID
    private String flightId;
    private String seatType; // "Economy" 或 "Business"
    private String orderStatus; // "Established", "Paid", "Canceled"
    private LocalDateTime orderTime;

    public Order() {
    }

    public Order(Integer customerId, Integer buyerId, String flightId,
            String seatType, String orderStatus, LocalDateTime orderTime) {
        this.customerId = customerId;
        this.buyerId = buyerId;
        this.flightId = flightId;
        this.seatType = seatType;
        this.orderStatus = orderStatus;
        this.orderTime = orderTime;
    }

    // Getter和Setter方法
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
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
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", buyerId=" + buyerId +
                ", flightId='" + flightId + '\'' +
                ", seatType='" + seatType + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderTime=" + orderTime +
                '}';
    }
}