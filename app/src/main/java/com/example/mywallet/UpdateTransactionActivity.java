package com.example.mywallet;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateTransactionActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText editTextDescription;
    private EditText editTextAmount;
    private String transactionId;
    private String walletId;
    private double oldAmount; // Store the old amount for balance calculation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_transaction);

        editTextDescription = findViewById(R.id.editTextDescription);
        editTextAmount = findViewById(R.id.editTextAmount);
        transactionId = getIntent().getStringExtra("transaction_id");
        walletId = getIntent().getStringExtra("wallet_id");

        Button updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(v -> updateTransaction());

        loadTransactionDetails();
    }

    private void loadTransactionDetails() {
        db.collection("wallets").document(walletId).collection("transactions").document(transactionId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Transaction transaction = documentSnapshot.toObject(Transaction.class);
                        if (transaction != null) {
                            editTextDescription.setText(transaction.getDescription());
                            oldAmount = transaction.getAmount(); // Store the old amount
                            editTextAmount.setText(String.valueOf(oldAmount));
                        }
                    } else {
                        Toast.makeText(this, "Transaction not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading transaction details", Toast.LENGTH_SHORT).show());
    }

    private void updateTransaction() {
        String description = editTextDescription.getText().toString();
        double newAmount = Double.parseDouble(editTextAmount.getText().toString());

        // Create a map to store updated transaction fields
        Map<String, Object> updates = new HashMap<>();
        updates.put("description", description);
        updates.put("amount", newAmount);

        // Update the transaction document
        db.collection("wallets").document(walletId).collection("transactions").document(transactionId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Adjust wallet balance after transaction update
                    adjustWalletBalance(oldAmount, newAmount);
                    Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Set result code before finishing activity
                    finish(); // Close the activity and return to the previous activity
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error updating transaction", Toast.LENGTH_SHORT).show());
    }

    private void adjustWalletBalance(double oldAmount, double newAmount) {
        double difference = newAmount - oldAmount;

        // Fetch the current wallet balance
        db.collection("wallets").document(walletId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Wallet wallet = documentSnapshot.toObject(Wallet.class);
                        if (wallet != null) {
                            double currentBalance = wallet.getBalance();

                            // Update the wallet balance
                            double updatedBalance = currentBalance + difference;
                            Map<String, Object> balanceUpdate = new HashMap<>();
                            balanceUpdate.put("balance", updatedBalance);

                            db.collection("wallets").document(walletId).update(balanceUpdate)
                                    .addOnSuccessListener(aVoid -> Log.d("UpdateTransaction", "Wallet balance updated successfully"))
                                    .addOnFailureListener(e -> Log.d("UpdateTransaction", "Error updating wallet balance", e));
                        }
                    } else {
                        Log.d("UpdateTransaction", "Wallet not found");
                    }
                })
                .addOnFailureListener(e -> Log.d("UpdateTransaction", "Error fetching wallet balance", e));
    }
}



