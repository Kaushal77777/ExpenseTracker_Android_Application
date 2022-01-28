package com.dduproject.expensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import java.util.ArrayList;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.dduproject.expensetracker.databinding.ActivityCategoriesBinding;
import com.dduproject.expensetracker.models.User;
import com.dduproject.expensetracker.adapter.CategoriesAdapter;
import com.dduproject.expensetracker.utils.CategoriesHelper;
import com.dduproject.expensetracker.models.Category;

public class CategoriesActivity extends BaseActivity {
    ActivityCategoriesBinding binding;
    private User user;
    private ArrayList<Category> customCategoriesList;
    private CategoriesAdapter categoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        customCategoriesList = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(this, customCategoriesList, getApplicationContext());
        binding.lvCategories.setAdapter(categoriesAdapter);

        binding.fabAddCategory.setOnClickListener(v ->
                startActivity(new Intent(CategoriesActivity.this, AddCategoryActivity.class))
        );
        getData();
    }
    private void getData(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(getUid()).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e( "Error getting data", String.valueOf(task.getException()));
            } else {
                user = task.getResult().getValue(User.class);
                dataUpdated();
            }
        });
    }
    private void dataUpdated() {
        if (user == null) {
            return;
        }
        customCategoriesList.clear();
        customCategoriesList.addAll(CategoriesHelper.getCustomCategories(user));
        categoriesAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        getData();
        super.onRestart();
    }
}
