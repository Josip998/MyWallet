package com.example.mywallet;

public class Wallet {
    private String id;
    private String name;
    private double balance;

    private String userId;

    // Default constructor required for calls to DataSnapshot.getValue(Wallet.class)
    public Wallet() {
    }

    // Constructor with parameters
    public Wallet(String name, double balance, String userId) {
        this.name = name;
        this.balance = balance;
        this.userId = userId;
    }

    // Getter and Setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for balance
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}




