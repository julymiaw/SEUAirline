package com.seu.airline.entity;

public class Passenger {
    private String hostId;
    private String guestId;

    public Passenger() {
    }

    public Passenger(String hostId, String guestId) {
        this.hostId = hostId;
        this.guestId = guestId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "hostId='" + hostId + '\'' +
                ", guestId='" + guestId + '\'' +
                '}';
    }
}