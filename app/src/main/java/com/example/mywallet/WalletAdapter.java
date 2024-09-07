package com.example.mywallet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.WalletViewHolder> {
    private List<Wallet> walletList;
    private OnItemClickListener listener;

    public WalletAdapter(List<Wallet> walletList, OnItemClickListener listener) {
        this.walletList = walletList;
        this.listener = listener;
    }

    @Override
    public WalletViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet, parent, false);
        return new WalletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WalletViewHolder holder, int position) {
        Wallet wallet = walletList.get(position);
        holder.walletName.setText(wallet.getName());
        holder.walletBalance.setText(String.valueOf(wallet.getBalance()));

        holder.updateButton.setOnClickListener(v -> listener.onUpdateClick(wallet));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(wallet));
        holder.seeTransactionsButton.setOnClickListener(v -> listener.onSeeTransactionsClick(wallet));
    }

    @Override
    public int getItemCount() {
        return walletList.size();
    }

    public static class WalletViewHolder extends RecyclerView.ViewHolder {
        TextView walletName, walletBalance;
        Button updateButton, deleteButton, seeTransactionsButton;

        public WalletViewHolder(View itemView) {
            super(itemView);
            walletName = itemView.findViewById(R.id.wallet_name);
            walletBalance = itemView.findViewById(R.id.wallet_balance);
            updateButton = itemView.findViewById(R.id.button_update);
            deleteButton = itemView.findViewById(R.id.button_delete);
            seeTransactionsButton = itemView.findViewById(R.id.button_see_transactions);
        }
    }

    public interface OnItemClickListener {
        void onUpdateClick(Wallet wallet);
        void onDeleteClick(Wallet wallet);
        void onSeeTransactionsClick(Wallet wallet); // Add this method
    }
}



