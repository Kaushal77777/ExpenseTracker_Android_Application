package com.dduce.expensetracker.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.dduce.expensetracker.R;
import com.dduce.expensetracker.utils.DBHelper;
import com.google.android.material.appbar.AppBarLayout;
import com.dduce.expensetracker.databinding.ActivityMainBinding;
import com.dduce.expensetracker.adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.fabAddEntry.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddEntryActivity.class)));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        binding.viewPager.setAdapter(viewPagerAdapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                ((AppBarLayout) binding.toolbar.getParent()).setExpanded(true, true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_manage_categories){
            startActivity(new Intent(this, CategoriesActivity.class));
            return true;
        } else if(id == R.id.menu_manage_members){
            startActivity(new Intent(this, MembersActivity.class));
            return true;
        } else if(id == R.id.menu_logout){
            DBHelper.getFirebaseAuth().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
