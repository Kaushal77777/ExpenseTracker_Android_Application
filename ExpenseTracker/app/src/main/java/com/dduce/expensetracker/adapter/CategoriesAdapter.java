package com.dduce.expensetracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dduce.expensetracker.databinding.RowCategoriesBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.dduce.expensetracker.activities.EditCategoryActivity;
import com.dduce.expensetracker.models.Category;

public class CategoriesAdapter extends FirebaseRecyclerAdapter<Category, CategoriesAdapter.CategoryViewHolder> {

    Context mContext;

    public CategoriesAdapter(Context mContext, @NonNull FirebaseRecyclerOptions<Category> options) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder viewHolder, int position, @NonNull Category category) {
        viewHolder.binding.tvCategoryName.setText(category.name);
        viewHolder.binding.btnEditCategory.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, EditCategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("category-id", category.getCategoryId());
            intent.putExtra("category-name", category.name);
            mContext.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(RowCategoriesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder{
        RowCategoriesBinding binding;
        public CategoryViewHolder(RowCategoriesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
