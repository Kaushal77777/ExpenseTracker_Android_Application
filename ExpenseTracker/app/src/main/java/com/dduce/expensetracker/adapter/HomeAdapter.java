package com.dduce.expensetracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dduce.expensetracker.R;
import com.dduce.expensetracker.models.HomeModel;
import com.dduce.expensetracker.utils.CurrencyHelper;

import java.util.ArrayList;

public class HomeAdapter extends ArrayAdapter<HomeModel> implements View.OnClickListener {

    private ArrayList<HomeModel> items;
    Context context;


    public HomeAdapter(Context context, ArrayList<HomeModel> items) {
        super(context, R.layout.row_home, items);
        this.items = items;
        this.context = context;
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.row_home, parent, false);
        }
        HomeModel dataModel = getItem(position);
        ImageView ivHomeIcon = view.findViewById(R.id.ivHomeIcon);
        TextView tvHomeName = view.findViewById(R.id.tvHomeName);
        TextView tvHomeSum = view.findViewById(R.id.tvHomeSum);

        tvHomeName.setText(dataModel.getCategoryName());
        tvHomeSum.setText(CurrencyHelper.formatCurrency(dataModel.getMoney()));

        tvHomeSum.setTextColor(ContextCompat.getColor(context, dataModel.getMoney() < 0 ? R.color.colorExpense : R.color.colorIncome));
        ivHomeIcon.setBackgroundColor(ContextCompat.getColor(context, dataModel.getMoney() < 0 ? R.color.colorExpense : R.color.colorIncome));

        view.setClickable(false);
        view.setOnClickListener(null);
        return view;
    }
}
