package com.seu.airline.entity;

public class Admin {
    private String adminId;
    private String username;
    private String password;
    private String name;

    public Admin() {
    }

    public Admin(String adminId, String username, String password, String name) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
        this.name = name;
    }

    // Getter和Setter
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}