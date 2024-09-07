package com.example.mywallet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DeleteTransactionActivity extends AppCompatActivity {

    Button deleteTransactionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_transaction);

        deleteTransactionButton = findViewById(R.id.delete_transaction_button);

        deleteTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete transaction logic here
                Toast.makeText(DeleteTransactionActivity.this, "Transaction deleted successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
