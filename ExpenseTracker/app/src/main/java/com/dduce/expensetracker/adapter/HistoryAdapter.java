package com.dduce.expensetracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dduce.expensetracker.databinding.RowHistoryBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.dduce.expensetracker.R;
import com.dduce.expensetracker.activities.EditEntryActivity;
import com.dduce.expensetracker.models.Entry;
import com.dduce.expensetracker.utils.CurrencyHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryAdapter extends FirebaseRecyclerAdapter<Entry, HistoryAdapter.HistoryViewHolder> {

    Context mContext;

    public HistoryAdapter(Context mContext, @NonNull FirebaseRecyclerOptions<Entry> options) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder viewHolder, int position, @NonNull Entry entry) {
        Date date = new Date(entry.timestamp);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
        viewHolder.binding.tvEntryCategory.setText(entry.category);
        viewHolder.binding.tvEntryName.setText(entry.name);
        viewHolder.binding.tvEntryMember.setText(entry.member);
        viewHolder.binding.tvEntryDate.setText(dateFormat.format(date));
        viewHolder.binding.tvEntryAmount.setText(CurrencyHelper.formatCurrency(entry.amount));
        viewHolder.binding.tvEntryAmount.setTextColor(ContextCompat.getColor(mContext, entry.amount < 0 ? R.color.colorExpense : R.color.colorIncome));
        viewHolder.binding.ivEntryIcon.setBackgroundColor(ContextCompat.getColor(mContext, entry.amount < 0 ? R.color.colorExpense : R.color.colorIncome));
        viewHolder.binding.btnEditEntry.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, EditEntryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("entry-id", entry.id);
            intent.putExtra("entry-category", entry.category);
            intent.putExtra("entry-name", entry.name);
            intent.putExtra("entry-member", entry.member);
            intent.putExtra("entry-timestamp", entry.timestamp);
            intent.putExtra("entry-amount", entry.amount);
            mContext.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(RowHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder{
        RowHistoryBinding binding;
        public HistoryViewHolder(RowHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}