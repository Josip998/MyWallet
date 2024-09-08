package com.mycompany.mywallet;

import com.google.firebase.Timestamp;

public class Transaction {

    private String transactionId;
    private double amount;
    private Timestamp date;
    private String description;
    private String type;
    private String walletId;

    // Default constructor required for Firestore
    public Transaction() {}

    // Parameterized constructor
    public Transaction(String transactionId, double amount, Timestamp date, String description, String type, String walletId) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.type = type;
        this.walletId = walletId;
    }

    // Getters and setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    // Method to set ID if needed
    public void setId(String transactionId) {
        this.transactionId = transactionId;
    }

    // Method to get ID
    public String getId() {
        return transactionId;
    }
}




