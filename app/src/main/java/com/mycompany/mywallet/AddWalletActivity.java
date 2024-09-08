package com.mycompany.mywallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddWalletActivity extends AppCompatActivity {

    private EditText editTextWalletName, editTextWalletBalance;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);

        editTextWalletName = findViewById(R.id.editTextWalletName);
        editTextWalletBalance = findViewById(R.id.editTextWalletBalance);
        Button buttonSaveWallet = findViewById(R.id.buttonSaveWallet);

        db = FirebaseFirestore.getInstance();

        buttonSaveWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextWalletName.getText().toString().trim();
                String balanceStr = editTextWalletBalance.getText().toString().trim();

                // Validate that both fields are filled in
                if (name.isEmpty() || balanceStr.isEmpty()) {
                    Toast.makeText(AddWalletActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Regular expression to ensure the input is a valid number
                if (!balanceStr.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                    Toast.makeText(AddWalletActivity.this, "Please enter a valid balance", Toast.LENGTH_SHORT).show();
                    return;
                }

                double balance;
                try {
                    balance = Double.parseDouble(balanceStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(AddWalletActivity.this, "Invalid balance format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the user is logged in
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(AddWalletActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create the Wallet object
                String userId = currentUser.getUid();
                Wallet newWallet = new Wallet(name, balance, userId);

                // Add the wallet to Firestore
                db.collection("wallets").add(newWallet)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(AddWalletActivity.this, "Wallet added successfully", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("wallet_added", true);
                            setResult(RESULT_OK, resultIntent);
                            finish(); // Close activity
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddWalletActivity.this, "Error adding wallet", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}




