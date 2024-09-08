package com.mycompany.mywallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ViewTransactionsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_TRANSACTION = 1;
    private RecyclerView recyclerViewTransactions;
    private TransactionAdapter transactionsAdapter;
    private List<Transaction> transactionList = new ArrayList<>();
    private String walletId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transactions);

        // Initialize walletId from Intent extras
        walletId = getIntent().getStringExtra("wallet_id");
        if (walletId == null) {
            Log.e("ViewTransactions", "walletId is null. Please ensure it's passed from previous activity.");
            finish(); // Close activity if walletId is not provided
            return;
        }

        recyclerViewTransactions = findViewById(R.id.transactions_recyclerview);
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));

        // Initialize TransactionAdapter with walletId
        transactionsAdapter = new TransactionAdapter(transactionList, new TransactionAdapter.OnItemClickListener() {
            @Override
            public void onUpdateClick(Transaction transaction, String walletId) {
                // Handle update click
                Intent intent = new Intent(ViewTransactionsActivity.this, UpdateTransactionActivity.class);
                intent.putExtra("transaction_id", transaction.getId());
                intent.putExtra("wallet_id", walletId);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Transaction transaction, String walletId) {
                // Handle delete click
                if (transaction.getId() == null || transaction.getId().isEmpty()) {
                    Log.e("ViewTransactions", "Transaction ID is null or empty");
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("wallets").document(walletId).collection("transactions").document(transaction.getId()).delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d("ViewTransactions", "Transaction deleted successfully");
                        })
                        .addOnFailureListener(e -> {
                            Log.w("ViewTransactions", "Error deleting transaction", e);
                        });
            }
        }, walletId);
        recyclerViewTransactions.setAdapter(transactionsAdapter);

        Button addTransactionButton = findViewById(R.id.add_transaction_button);
        addTransactionButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewTransactionsActivity.this, AddTransactionActivity.class);
            intent.putExtra("wallet_id", walletId);
            startActivityForResult(intent, REQUEST_CODE_ADD_TRANSACTION);
        });

        // Add this block to handle button click to navigate to MainActivity
        Button toMainButton = findViewById(R.id.button_to_main);
        toMainButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewTransactionsActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Load transactions with real-time updates
        loadTransactions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ViewTransactions", "onActivityResult called with requestCode: " + requestCode + " resultCode: " + resultCode);
        if (requestCode == REQUEST_CODE_ADD_TRANSACTION && resultCode == RESULT_OK) {
            // No need to manually reload transactions; real-time listener will handle updates
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadTransactions() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("wallets").document(walletId)
                .collection("transactions")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("FirestoreError", "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        List<Transaction> transactions = new ArrayList<>();
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            Transaction transaction = document.toObject(Transaction.class);
                            if (transaction != null) {
                                transactions.add(transaction);
                            }
                        }
                        transactionList.clear();
                        transactionList.addAll(transactions);
                        transactionsAdapter.notifyDataSetChanged(); // Notify adapter of data changes
                        Log.d("ViewTransactions", "Transactions updated successfully");
                    }
                });
    }
}






