package com.dduproject.expensetracker.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.google.firebase.database.FirebaseDatabase;
import com.dduproject.expensetracker.databinding.ActivityEditCategoryBinding;
import com.dduproject.expensetracker.exceptions.EmptyStringException;
import com.dduproject.expensetracker.models.Category;

public class EditCategoryActivity extends BaseActivity {
    ActivityEditCategoryBinding binding;
    private String categoryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String categoryName = getIntent().getExtras().getString("category-name");
        categoryID = getIntent().getExtras().getString("category-id");
        binding.etCategoryName.setText(categoryName);

        binding.btnSave.setOnClickListener(v -> {
            try {
                editCustomCategory(binding.etCategoryName.getText().toString());
            } catch (EmptyStringException e) {
                binding.ilCategoryName.setError(e.getMessage());
            }

        });

        binding.btnRemove.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("users").child(getUid()).child("categories").child(categoryID).removeValue();
            finish();
        });
    }

    private void editCustomCategory(String categoryName) throws EmptyStringException {
        if(categoryName == null || categoryName.length() == 0) {
            throw new EmptyStringException("Entry name length should be > 0");
        }
        FirebaseDatabase.getInstance().getReference().child("users").child(getUid()).child("categories").child(categoryID).setValue(new Category(categoryName));
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
