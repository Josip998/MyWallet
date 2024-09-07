package com.example.mywallet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DeleteWalletActivity extends AppCompatActivity {

    Button deleteWalletButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_wallet);

        deleteWalletButton = findViewById(R.id.delete_wallet_button);

        deleteWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete wallet logic here
                Toast.makeText(DeleteWalletActivity.this, "Wallet deleted successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}