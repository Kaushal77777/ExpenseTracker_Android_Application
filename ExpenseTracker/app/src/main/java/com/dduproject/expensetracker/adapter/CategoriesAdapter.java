package com.dduproject.expensetracker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import com.dduproject.expensetracker.R;
import com.dduproject.expensetracker.activities.EditCategoryActivity;
import com.dduproject.expensetracker.models.Category;

public class CategoriesAdapter extends ArrayAdapter<Category> implements View.OnClickListener {

    private final Activity activity;
    Context context;

    public CategoriesAdapter(Activity activity, List<Category> data, Context context) {
        super(context, R.layout.row_categories, data);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.row_categories, parent, false);
        }
        Category category = getItem(position);
        TextView tvCategoryName = listItem.findViewById(R.id.tvCategoryName);
        tvCategoryName.setText(category.getCategoryName());
        listItem.setOnClickListener(v -> {
            Intent intent = new Intent(activity, EditCategoryActivity.class);
            intent.putExtra("category-id", category.getCategoryId());
            intent.putExtra("category-name", category.getCategoryName());
            activity.startActivity(intent);
        });
        return listItem;
    }
}
