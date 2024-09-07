package com.example.mywallet;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class WalletService {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Add a new wallet
    public void addWallet(String walletId, String name, double balance, String userId, OnCompleteListener<Void> callback) {
        Map<String, Object> wallet = new HashMap<>();
        wallet.put("name", name);
        wallet.put("balance", balance);
        wallet.put("userId", userId);

        db.collection("wallets").document(walletId).set(wallet)
                .addOnCompleteListener(callback);
    }

    // Update an existing wallet
    public void updateWallet(String walletId, String name, double balance, OnCompleteListener<Void> callback) {
        DocumentReference walletRef = db.collection("wallets").document(walletId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("balance", balance);

        walletRef.update(updates)
                .addOnCompleteListener(callback);
    }

    // Delete a wallet
    public void deleteWallet(String walletId, OnCompleteListener<Void> callback) {
        db.collection("wallets").document(walletId).delete()
                .addOnCompleteListener(callback);
    }

    // Add a transaction to a wallet
    public void addTransaction(String walletId, String transactionId, String description, double amount, OnCompleteListener<Void> callback) {
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("description", description);
        transaction.put("amount", amount);

        db.collection("wallets").document(walletId).collection("transactions").document(transactionId).set(transaction)
                .addOnCompleteListener(callback);
    }

    // Update a transaction
    public void updateTransaction(String walletId, String transactionId, String description, double amount, OnCompleteListener<Void> callback) {
        DocumentReference transactionRef = db.collection("wallets").document(walletId).collection("transactions").document(transactionId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("description", description);
        updates.put("amount", amount);

        transactionRef.update(updates)
                .addOnCompleteListener(callback);
    }

    // Delete a transaction
    public void deleteTransaction(String walletId, String transactionId, OnCompleteListener<Void> callback) {
        db.collection("wallets").document(walletId).collection("transactions").document(transactionId).delete()
                .addOnCompleteListener(callback);
    }
}
