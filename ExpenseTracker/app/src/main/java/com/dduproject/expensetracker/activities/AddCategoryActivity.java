package com.dduproject.expensetracker.activities;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.dduproject.expensetracker.databinding.ActivityAddCategoryBinding;
import com.dduproject.expensetracker.models.Category;
import com.dduproject.expensetracker.utils.DBHelper;

public class AddCategoryActivity extends AppCompatActivity {

    ActivityAddCategoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.btnAddCategory.setOnClickListener(v -> {
            String categoryName = binding.etCategoryName.getText().toString();
            if(categoryName.isEmpty()){
                binding.ilCategoryName.setError("Please Enter Category Name");
            } else {
                DBHelper.addCategory(new Category(categoryName));
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
