package com.seu.airline.entity;

public class Customer {
    private String customerId;
    private String name;
    private String password;
    private Integer accountBalance;
    private String phone;
    private String email;
    private String identity;
    private Integer rank;

    // 构造函数
    public Customer() {
    }

    public Customer(String name, String password, Integer accountBalance,
            String phone, String email, String identity, Integer rank) {
        this.name = name;
        this.password = password;
        this.accountBalance = accountBalance != null ? accountBalance : 0; // ✅ Integer
        this.phone = phone;
        this.email = email;
        this.identity = identity;
        this.rank = rank != null ? rank : 0;
    }

    // Getter和Setter方法
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAccountBalance() {
        return accountBalance;
    } // ✅ Integer

    public void setAccountBalance(Integer accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", accountBalance=" + accountBalance +
                ", rank=" + rank +
                '}';
    }
}