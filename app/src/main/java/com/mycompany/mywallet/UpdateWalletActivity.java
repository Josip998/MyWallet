package com.mycompany.mywallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateWalletActivity extends AppCompatActivity {

    private EditText walletName, walletBalance;
    private Button updateWalletButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String walletId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_wallet);

        walletName = findViewById(R.id.wallet_name);
        walletBalance = findViewById(R.id.wallet_balance);
        updateWalletButton = findViewById(R.id.update_wallet_button);

        walletId = getIntent().getStringExtra("wallet_id"); // Get wallet ID from intent
        loadWalletDetails();

        updateWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWallet();
            }
        });
    }

    private void loadWalletDetails() {
        db.collection("wallets").document(walletId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Wallet wallet = task.getResult().toObject(Wallet.class);
                if (wallet != null) {
                    walletName.setText(wallet.getName());
                    walletBalance.setText(String.valueOf(wallet.getBalance()));
                }
            } else {
                Toast.makeText(UpdateWalletActivity.this, "Failed to load wallet details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWallet() {
        String name = walletName.getText().toString().trim();
        String balanceInput = walletBalance.getText().toString().trim();

        // Check if the wallet name is empty
        if (name.isEmpty()) {
            Toast.makeText(this, "Wallet name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the balance input is empty
        if (balanceInput.isEmpty()) {
            Toast.makeText(this, "Please enter a valid balance", Toast.LENGTH_SHORT).show();
            return;
        }

        // Regular expression to ensure the input is a valid number (integer or decimal)
        if (!balanceInput.matches("^[0-9]*\\.?[0-9]+$")) {
            Toast.makeText(this, "Invalid balance. Please enter a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        double balance;
        try {
            balance = Double.parseDouble(balanceInput);

            // Check if the parsed balance is a valid number and greater than or equal to 0
            if (balance < 0) {
                Toast.makeText(this, "Balance cannot be negative", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            // Handle invalid number formats
            Toast.makeText(this, "Invalid balance. Please enter a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the userId of the currently logged-in user
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create an updated Wallet object with the existing id and userId
        Wallet updatedWallet = new Wallet(name, balance, userId);

        db.collection("wallets").document(walletId).set(updatedWallet).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UpdateWalletActivity.this, "Wallet updated successfully", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("update_success", true);
                setResult(RESULT_OK, resultIntent);
                finish(); // Close activity
            } else {
                Toast.makeText(UpdateWalletActivity.this, "Failed to update wallet", Toast.LENGTH_SHORT).show();
            }
        });
    }






}
