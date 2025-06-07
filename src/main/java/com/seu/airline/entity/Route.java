package com.seu.airline.entity;

public class Route {
    private String routeId;
    private String departureAirportId;
    private String arrivalAirportId;

    public Route() {
    }

    public Route(String routeId, String departureAirportId, String arrivalAirportId) {
        this.routeId = routeId;
        this.departureAirportId = departureAirportId;
        this.arrivalAirportId = arrivalAirportId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getDepartureAirportId() {
        return departureAirportId;
    }

    public void setDepartureAirportId(String departureAirportId) {
        this.departureAirportId = departureAirportId;
    }

    public String getArrivalAirportId() {
        return arrivalAirportId;
    }

    public void setArrivalAirportId(String arrivalAirportId) {
        this.arrivalAirportId = arrivalAirportId;
    }

    @Override
    public String toString() {
        return "Route{" +
                "routeId='" + routeId + '\'' +
                ", departureAirportId='" + departureAirportId + '\'' +
                ", arrivalAirportId='" + arrivalAirportId + '\'' +
                '}';
    }
}