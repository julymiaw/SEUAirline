package com.seu.airline.entity;

public class Passenger {
    private Integer hostId; // 对应Flask中的HostID
    private Integer guestId; // 对应Flask中的GuestID

    public Passenger() {
    }

    public Passenger(Integer hostId, Integer guestId) {
        this.hostId = hostId;
        this.guestId = guestId;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    public Integer getGuestId() {
        return guestId;
    }

    public void setGuestId(Integer guestId) {
        this.guestId = guestId;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "hostId=" + hostId +
                ", guestId=" + guestId +
                '}';
    }
}