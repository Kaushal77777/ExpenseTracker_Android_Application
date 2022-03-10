package com.dduce.expensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dduce.expensetracker.utils.DBHelper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.dduce.expensetracker.databinding.ActivityCategoriesBinding;
import com.dduce.expensetracker.adapter.CategoriesAdapter;
import com.dduce.expensetracker.models.Category;

public class CategoriesActivity extends AppCompatActivity {
    ActivityCategoriesBinding binding;
    CategoriesAdapter categoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(DBHelper.getCategories(), snapshot -> new Category(snapshot.getKey(), snapshot.child("name").getValue().toString()))
                .build();

        categoriesAdapter = new CategoriesAdapter(getApplicationContext(), options);

        binding.rvCategories.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCategories.setAdapter(categoriesAdapter);

        binding.fabAddCategory.setOnClickListener(v ->
                startActivity(new Intent(CategoriesActivity.this, AddCategoryActivity.class))
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        categoriesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        categoriesAdapter.stopListening();
    }
}
