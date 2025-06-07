package com.seu.airline.entity;

public class Aircraft {
    private String aircraftId;
    private String aircraftType;
    private Integer economySeats;
    private Integer businessSeats;

    public Aircraft() {
    }

    public Aircraft(String aircraftId, String aircraftType,
            Integer economySeats, Integer businessSeats) {
        this.aircraftId = aircraftId;
        this.aircraftType = aircraftType;
        this.economySeats = economySeats;
        this.businessSeats = businessSeats;
    }

    public String getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(String aircraftId) {
        this.aircraftId = aircraftId;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public Integer getEconomySeats() {
        return economySeats;
    }

    public void setEconomySeats(Integer economySeats) {
        this.economySeats = economySeats;
    }

    public Integer getBusinessSeats() {
        return businessSeats;
    }

    public void setBusinessSeats(Integer businessSeats) {
        this.businessSeats = businessSeats;
    }

    @Override
    public String toString() {
        return "Aircraft{" +
                "aircraftId='" + aircraftId + '\'' +
                ", aircraftType='" + aircraftType + '\'' +
                ", economySeats=" + economySeats +
                ", businessSeats=" + businessSeats +
                '}';
    }
}