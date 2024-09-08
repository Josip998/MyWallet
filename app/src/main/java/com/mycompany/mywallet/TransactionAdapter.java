package com.mycompany.mywallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> transactions;
    private OnItemClickListener listener;
    private String walletId;

    public TransactionAdapter(List<Transaction> transactions, OnItemClickListener listener, String walletId) {
        this.transactions = transactions;
        this.listener = listener;
        this.walletId = walletId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.bind(transaction, listener, walletId);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void updateTransactions(List<Transaction> newTransactions) {
        this.transactions = new ArrayList<>(newTransactions);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onUpdateClick(Transaction transaction, String walletId);
        void onDeleteClick(Transaction transaction, String walletId);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView description;
        private TextView amount;
        private TextView type;
        private Button updateButton;
        private Button deleteButton;
        private View itemView; // Reference to the root view for background color change

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            description = itemView.findViewById(R.id.transaction_description);
            amount = itemView.findViewById(R.id.transaction_amount);
            type = itemView.findViewById(R.id.type);
            updateButton = itemView.findViewById(R.id.update_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        public void bind(final Transaction transaction, final OnItemClickListener listener, final String walletId) {
            description.setText(transaction.getDescription());
            amount.setText(String.valueOf(transaction.getAmount()));
            type.setText(transaction.getType());

            // Apply background color based on transaction type
            Context context = itemView.getContext();
            if ("Withdrawal".equals(transaction.getType())) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            } else if ("Deposit".equals(transaction.getType())) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.default_color)); // Optional: default color
            }

            updateButton.setOnClickListener(v -> listener.onUpdateClick(transaction, walletId));
            deleteButton.setOnClickListener(v -> listener.onDeleteClick(transaction, walletId));
        }
    }
}






