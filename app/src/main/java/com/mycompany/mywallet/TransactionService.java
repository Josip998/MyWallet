package com.mycompany.mywallet;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class TransactionService {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference transactionsRef = db.collection("transactions");

    // Create a Transaction
    public Task<Void> createTransaction(String transactionId, double amount, String type, String walletId, String description) {
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("amount", amount);
        transaction.put("type", type);
        transaction.put("date", com.google.firebase.Timestamp.now());
        transaction.put("walletId", walletId);
        transaction.put("description", description);

        return transactionsRef.document(transactionId).set(transaction);
    }

    // Read a Transaction
    public Task<DocumentSnapshot> getTransaction(String transactionId) {
        return transactionsRef.document(transactionId).get();
    }

    // Delete a Transaction
    public Task<Void> deleteTransaction(String transactionId) {
        return transactionsRef.document(transactionId).delete();
    }

    // Read All Transactions for a Wallet
    public Task<QuerySnapshot> getAllTransactionsForWallet(String walletId) {
        return transactionsRef.whereEqualTo("walletId", walletId).get();
    }
}
