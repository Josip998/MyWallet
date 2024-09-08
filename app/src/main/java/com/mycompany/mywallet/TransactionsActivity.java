package com.mycompany.mywallet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity implements TransactionAdapter.OnItemClickListener {

    private static final int REQUEST_CODE_UPDATE_TRANSACTION = 2;
    private static final int ADD_TRANSACTION_REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String walletId;
    private TextView walletBalanceText; // TextView to display wallet balance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        recyclerView = findViewById(R.id.transaction_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        walletId = getIntent().getStringExtra("wallet_id");

        walletBalanceText = findViewById(R.id.wallet_balance_text); // Initialize TextView

        transactionAdapter = new TransactionAdapter(transactionList, this, walletId);
        recyclerView.setAdapter(transactionAdapter);

        loadTransactions();
        loadWalletBalance(); // Load the wallet balance

        Button addButton = findViewById(R.id.add_transaction_button);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTransactionActivity.class);
            intent.putExtra("wallet_id", walletId);
            startActivityForResult(intent, ADD_TRANSACTION_REQUEST_CODE); // Ensure request code is used here
        });

        Button homeButton = findViewById(R.id.button_to_main);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(TransactionsActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onUpdateClick(Transaction transaction, String walletId) {
        Intent intent = new Intent(this, UpdateTransactionActivity.class);
        intent.putExtra("transaction_id", transaction.getId());
        intent.putExtra("wallet_id", walletId);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_TRANSACTION);
    }

    @Override
    public void onDeleteClick(Transaction transaction, String walletId) {
        String transactionId = transaction.getId();

        if (transactionId == null || transactionId.isEmpty()) {
            Toast.makeText(this, "Transaction ID is null or empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Delete the transaction
        db.collection("wallets").document(walletId).collection("transactions").document(transactionId).delete()
                .addOnSuccessListener(aVoid -> {
                    // Update the wallet balance after deletion
                    updateWalletBalanceAfterDeletion(transaction);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error deleting transaction", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadTransactions() {
        db.collection("wallets").document(walletId).collection("transactions").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                transactionList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    Transaction transaction = document.toObject(Transaction.class);
                    if (transaction != null) {
                        transaction.setId(document.getId());
                        transactionList.add(transaction);
                    }
                }
                transactionAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Error loading transactions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWalletBalance() {
        db.collection("wallets").document(walletId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    double balance = document.getDouble("balance");
                    walletBalanceText.setText(String.format("Balance: $%.2f", balance));
                } else {
                    walletBalanceText.setText("Balance: $0.00");
                }
            } else {
                Toast.makeText(this, "Error loading wallet balance", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWalletBalanceAfterDeletion(Transaction transaction) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieve the current wallet balance
        db.collection("wallets").document(transaction.getWalletId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        double currentBalance = documentSnapshot.getDouble("balance");
                        double newBalance = currentBalance;

                        // Adjust balance based on transaction type
                        if ("Deposit".equals(transaction.getType())) {
                            newBalance -= transaction.getAmount(); // Subtract if it was a deposit
                        } else if ("Withdrawal".equals(transaction.getType())) {
                            newBalance += transaction.getAmount(); // Add if it was a withdrawal
                        }

                        // Update the wallet with the new balance
                        documentSnapshot.getReference().update("balance", newBalance)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Transaction deleted and wallet balance updated", Toast.LENGTH_SHORT).show();
                                    loadTransactions(); // Refresh the transaction list
                                    loadWalletBalance(); // Refresh the wallet balance
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error updating wallet balance", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Wallet not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error getting wallet document", Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TRANSACTION_REQUEST_CODE && resultCode == RESULT_OK) {
            loadTransactions(); // Refresh the transaction list if result is OK
            loadWalletBalance(); // Refresh the wallet balance
        }
        if (requestCode == REQUEST_CODE_UPDATE_TRANSACTION && resultCode == RESULT_OK) {
            loadTransactions(); // Refresh the transaction list if result is OK
            loadWalletBalance(); // Refresh the wallet balance
        }
    }
}






