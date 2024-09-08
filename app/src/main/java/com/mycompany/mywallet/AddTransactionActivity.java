package com.mycompany.mywallet;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.UUID;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText amountField;
    private EditText descriptionField;
    private RadioGroup typeGroup;
    private Button saveButton;

    private String walletId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        amountField = findViewById(R.id.amount_field);
        descriptionField = findViewById(R.id.description_field);
        typeGroup = findViewById(R.id.type_group); // Updated to RadioGroup
        saveButton = findViewById(R.id.save_button);

        walletId = getIntent().getStringExtra("wallet_id");

        saveButton.setOnClickListener(v -> {
            String amountString = amountField.getText().toString();
            String description = descriptionField.getText().toString();
            int selectedId = typeGroup.getCheckedRadioButtonId(); // Get selected RadioButton ID

            if (amountString.isEmpty() || description.isEmpty() || selectedId == -1) {
                Toast.makeText(AddTransactionActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve the selected RadioButton text
            RadioButton selectedTypeButton = findViewById(selectedId);
            String type = selectedTypeButton.getText().toString();

            double amount = Double.parseDouble(amountString);
            Timestamp now = new Timestamp(new Date()); // Current timestamp
            String transactionId = UUID.randomUUID().toString(); // Generate a unique ID

            Transaction transaction = new Transaction(transactionId, amount, now, description, type, walletId);

            // Update the wallet balance
            updateWalletBalance(transaction);
        });
    }

    private void updateWalletBalance(Transaction transaction) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("wallets").document(transaction.getWalletId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        double currentBalance = documentSnapshot.getDouble("balance");
                        double newBalance = currentBalance;

                        // Adjust balance based on transaction type
                        if ("Deposit".equals(transaction.getType())) {
                            newBalance += transaction.getAmount();
                        } else if ("Withdrawal".equals(transaction.getType())) {
                            newBalance -= transaction.getAmount();
                        }

                        // Update the wallet with the new balance
                        documentSnapshot.getReference().update("balance", newBalance)
                                .addOnSuccessListener(aVoid -> {
                                    // Add the transaction after updating the balance
                                    addTransaction(transaction);
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("Firestore", "Error updating wallet balance", e);
                                    Toast.makeText(AddTransactionActivity.this, "Error updating wallet balance", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(AddTransactionActivity.this, "Wallet not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error getting wallet document", e);
                    Toast.makeText(AddTransactionActivity.this, "Error getting wallet details", Toast.LENGTH_SHORT).show();
                });
    }

    private void addTransaction(Transaction transaction) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Define the path to the transactions collection
        db.collection("wallets").document(transaction.getWalletId())
                .collection("transactions")
                .document(transaction.getTransactionId()) // Ensure this ID is unique for each transaction
                .set(transaction) // Add the transaction data
                .addOnSuccessListener(aVoid -> {
                    // Log success and notify user
                    Log.d("Firestore", "Transaction successfully added!");
                    Toast.makeText(AddTransactionActivity.this, "Transaction added successfully", Toast.LENGTH_SHORT).show();

                    // Return result to the previous activity
                    setResult(RESULT_OK); // Set the result code
                    finish(); // Close this activity
                })
                .addOnFailureListener(e -> {
                    // Log failure and notify user
                    Log.w("Firestore", "Error adding transaction", e);
                    Toast.makeText(AddTransactionActivity.this, "Error adding transaction", Toast.LENGTH_SHORT).show();
                });
    }


}








