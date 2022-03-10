package com.dduce.expensetracker.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.dduce.expensetracker.utils.DBHelper;
import com.dduce.expensetracker.databinding.ActivityEditCategoryBinding;
import com.dduce.expensetracker.models.Category;

public class EditCategoryActivity extends AppCompatActivity {
    ActivityEditCategoryBinding binding;
    private String categoryID;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        categoryID = getIntent().getExtras().getString("category-id");
        categoryName = getIntent().getExtras().getString("category-name");

        binding.etCategoryName.setText(categoryName);

        binding.btnSave.setOnClickListener(v -> {
            categoryName = binding.etCategoryName.getText().toString();
            if(categoryName.isEmpty()){
                binding.ilCategoryName.setError("Please Enter Category Name");
            } else {
                DBHelper.updateCategory(categoryID, new Category(categoryName));
                finish();
            }
        });

        binding.btnRemove.setOnClickListener(v -> {
            DBHelper.deleteCategory(categoryID);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
