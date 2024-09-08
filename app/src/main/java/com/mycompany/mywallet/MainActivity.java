package com.mycompany.mywallet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button buttonLogout, buttonViewWallets, buttonUpdateWallet, buttonDeleteWallet;
    Button buttonAddTransaction, buttonViewTransactions, buttonUpdateTransaction, buttonDeleteTransaction;
    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.user_details);

        // Initialize buttons
        buttonLogout = findViewById(R.id.logout);

        buttonViewWallets = findViewById(R.id.buttonViewWallets);


        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        // Set up button listeners
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        TextView privacyPolicyLink = findViewById(R.id.privacy_policy_link);
        privacyPolicyLink.setOnClickListener(v -> {
            String url = "https://www.freeprivacypolicy.com/live/5494f6f2-dec2-46ff-abfc-b7f243d8ada2";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });



        buttonViewWallets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewWalletsActivity.class);
                startActivity(intent);
            }
        });
    }
}

