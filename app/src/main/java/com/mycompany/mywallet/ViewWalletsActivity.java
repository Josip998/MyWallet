package com.mycompany.mywallet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ViewWalletsActivity extends AppCompatActivity implements WalletAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private WalletAdapter walletAdapter;
    private Button buttonAddWallet;
    private List<Wallet> walletList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final int REQUEST_CODE_UPDATE = 1;
    private static final int ADD_WALLET_REQUEST_CODE = 2; // Define request code for adding wallet

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wallets);

        recyclerView = findViewById(R.id.wallet_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        buttonAddWallet = findViewById(R.id.buttonAddWallet);
        walletAdapter = new WalletAdapter(walletList, this);
        recyclerView.setAdapter(walletAdapter);

        loadWallets();

        buttonAddWallet.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddWalletActivity.class);
            startActivityForResult(intent, ADD_WALLET_REQUEST_CODE); // Start AddWalletActivity with request code
        });

        Button homeButton = findViewById(R.id.button_to_main);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewWalletsActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Only load wallets that belong to the current logged-in user
    private void loadWallets() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();  // Get the current user
        if (currentUser == null) {
            Toast.makeText(ViewWalletsActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();  // Get the logged-in user's UID

        // Query Firestore for wallets where userId matches the current user's UID
        db.collection("wallets")
                .whereEqualTo("userId", userId)  // Only retrieve wallets of the current user
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        walletList.clear();  // Clear the old list
                        for (DocumentSnapshot document : task.getResult()) {
                            Wallet wallet = document.toObject(Wallet.class);
                            if (wallet != null) {
                                wallet.setId(document.getId()); // Set the ID for the wallet
                                walletList.add(wallet);  // Add the wallet to the list
                            }
                        }
                        walletAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the list
                    } else {
                        Toast.makeText(this, "Error loading wallets", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onUpdateClick(Wallet wallet) {
        Intent intent = new Intent(this, UpdateWalletActivity.class);
        intent.putExtra("wallet_id", wallet.getId()); // Pass the wallet ID to the update activity
        startActivityForResult(intent, REQUEST_CODE_UPDATE);
    }

    @Override
    public void onDeleteClick(Wallet wallet) {
        String walletId = wallet.getId(); // Make sure wallet.getId() returns a valid ID

        if (walletId == null || walletId.isEmpty()) {
            Toast.makeText(this, "Wallet ID is null or empty", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("wallets").document(walletId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Wallet deleted successfully", Toast.LENGTH_SHORT).show();
                    loadWallets(); // Refresh the wallet list after deletion
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error deleting wallet", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onSeeTransactionsClick(Wallet wallet) {
        Intent intent = new Intent(this, TransactionsActivity.class);
        intent.putExtra("wallet_id", wallet.getId()); // Pass the wallet ID to the TransactionsActivity
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from AddWalletActivity
        if (requestCode == ADD_WALLET_REQUEST_CODE && resultCode == RESULT_OK) {
            // Reload the wallet list after adding a new wallet
            loadWallets();
        }

        // Check if the result is from UpdateWalletActivity
        if (requestCode == REQUEST_CODE_UPDATE && resultCode == RESULT_OK) {
            boolean updateSuccess = data.getBooleanExtra("update_success", false);
            if (updateSuccess) {
                // Reload the wallet list after update
                loadWallets();
            }
        }
    }
}





