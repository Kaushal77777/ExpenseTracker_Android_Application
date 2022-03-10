package com.dduce.expensetracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import com.dduce.expensetracker.R;
import com.dduce.expensetracker.models.StatisticsModel;
import com.dduce.expensetracker.utils.CurrencyHelper;

public class StatisticsAdapter extends ArrayAdapter<StatisticsModel> implements View.OnClickListener {

    private ArrayList<StatisticsModel> items;
    Context context;


    public StatisticsAdapter(Context context, ArrayList<StatisticsModel> items) {
        super(context, R.layout.row_statistics, items);
        this.items = items;
        this.context = context;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.row_statistics, parent, false);
        }
        StatisticsModel dataModel = getItem(position);
        ImageView ivStatisticIcon = listItem.findViewById(R.id.ivStatisticIcon);
        TextView tvStatisticCategory = listItem.findViewById(R.id.tvStatisticCategory);
        TextView tvStatisticSum = listItem.findViewById(R.id.tvStatisticSum);
        TextView tvStatisticPercentage = listItem.findViewById(R.id.tvStatisticPercentage);

        tvStatisticCategory.setText(dataModel.getCategoryName());
        tvStatisticSum.setText(CurrencyHelper.formatCurrency(dataModel.getMoney()));
        tvStatisticPercentage.setText((int)(dataModel.getPercentage() * 100) +"." + (int)(dataModel.getPercentage() * 10000) % 100  + "%");
        tvStatisticSum.setTextColor(ContextCompat.getColor(context, dataModel.getMoney() < 0 ? R.color.colorExpense : R.color.colorIncome));
        ivStatisticIcon.setBackgroundColor(ContextCompat.getColor(context, dataModel.getMoney() < 0 ? R.color.colorExpense : R.color.colorIncome));

        listItem.setClickable(false);
        listItem.setOnClickListener(null);
        return listItem;
    }
}
