package com.dduproject.expensetracker.activities;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.firebase.database.FirebaseDatabase;
import com.dduproject.expensetracker.databinding.ActivityAddCategoryBinding;
import com.dduproject.expensetracker.exceptions.EmptyStringException;
import com.dduproject.expensetracker.models.Category;

public class AddCategoryActivity extends BaseActivity {
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
            try {
                addCustomCategory(binding.etCategoryName.getText().toString());
            } catch (EmptyStringException e) {
                binding.ilCategoryName.setError(e.getMessage());
            }
        });
    }

    private void addCustomCategory(String categoryName) throws EmptyStringException {
        if(categoryName == null || categoryName.length() == 0) {
            throw new EmptyStringException("Entry name length should be > 0");
        }
        FirebaseDatabase.getInstance().getReference().child("users").child(getUid()).child("categories").push().setValue(new Category(categoryName));
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
