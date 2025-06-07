package com.seu.airline.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Flight {
    private String flightId;
    private String routeId;
    private String aircraftId;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal economyPrice;
    private BigDecimal businessPrice;

    // 构造函数
    public Flight() {
    }

    public Flight(String flightId, String routeId, String aircraftId,
            LocalDateTime departureTime, LocalDateTime arrivalTime,
            BigDecimal economyPrice, BigDecimal businessPrice) {
        this.flightId = flightId;
        this.routeId = routeId;
        this.aircraftId = aircraftId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.economyPrice = economyPrice;
        this.businessPrice = businessPrice;
    }

    // Getter和Setter方法
    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(String aircraftId) {
        this.aircraftId = aircraftId;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public BigDecimal getEconomyPrice() {
        return economyPrice;
    }

    public void setEconomyPrice(BigDecimal economyPrice) {
        this.economyPrice = economyPrice;
    }

    public BigDecimal getBusinessPrice() {
        return businessPrice;
    }

    public void setBusinessPrice(BigDecimal businessPrice) {
        this.businessPrice = businessPrice;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightId='" + flightId + '\'' +
                ", routeId='" + routeId + '\'' +
                ", aircraftId='" + aircraftId + '\'' +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                '}';
    }
}