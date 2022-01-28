package com.dduproject.expensetracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.dduproject.expensetracker.R;
import com.dduproject.expensetracker.activities.EditEntryActivity;
import com.dduproject.expensetracker.models.WalletEntry;
import com.dduproject.expensetracker.utils.CurrencyHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends ArrayAdapter<WalletEntry> implements View.OnClickListener {

    Context context;

    public HistoryAdapter(Context context, List<WalletEntry> data) {
        super(context, R.layout.row_history, data);
        this.context = context;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.row_history, parent, false);
        }
        WalletEntry walletEntry = getItem(position);
        ImageView ivEntryIcon = listItem.findViewById(R.id.ivEntryIcon);
        TextView tvEntryName = listItem.findViewById(R.id.tvEntryName);
        TextView tvEntryCategory = listItem.findViewById(R.id.tvEntryCategory);
        TextView tvEntryMember = listItem.findViewById(R.id.tvEntryMember);
        TextView tvEntryDate = listItem.findViewById(R.id.tvEntryDate);
        TextView tvEntryAmount = listItem.findViewById(R.id.tvEntryAmount);

        tvEntryCategory.setText(walletEntry.category);
        tvEntryName.setText(walletEntry.name);
        tvEntryMember.setText(walletEntry.member);

        Date date = new Date(-walletEntry.timestamp);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        tvEntryDate.setText(dateFormat.format(date));
        tvEntryAmount.setText(CurrencyHelper.formatCurrency(walletEntry.balanceDifference));
        tvEntryAmount.setTextColor(ContextCompat.getColor(context, walletEntry.balanceDifference < 0 ? R.color.colorExpense : R.color.colorIncome));
        ivEntryIcon.setBackgroundColor(ContextCompat.getColor(context, walletEntry.balanceDifference < 0 ? R.color.colorExpense : R.color.colorIncome));
        listItem.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditEntryActivity.class);
            intent.putExtra("wallet-entry-id", walletEntry.id);
            intent.putExtra("wallet-entry-category", walletEntry.category);
            intent.putExtra("wallet-entry-name", walletEntry.name);
            intent.putExtra("wallet-entry-member", walletEntry.member);
            intent.putExtra("wallet-entry-timestamp", walletEntry.timestamp);
            intent.putExtra("wallet-entry-balance-difference", walletEntry.balanceDifference);
            context.startActivity(intent);
        });

        return listItem;
    }
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}