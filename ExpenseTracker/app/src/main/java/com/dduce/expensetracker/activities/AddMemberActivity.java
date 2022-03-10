package com.dduce.expensetracker.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.dduce.expensetracker.utils.DBHelper;
import com.dduce.expensetracker.databinding.ActivityAddMemberBinding;
import com.dduce.expensetracker.models.Member;

public class AddMemberActivity extends AppCompatActivity {
    ActivityAddMemberBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.btnAddMember.setOnClickListener(v -> {
            String memberName = binding.etMemberName.getText().toString();
            if(memberName.isEmpty()){
                binding.ilMemberName.setError("Please Enter Member Name");
            } else {
                DBHelper.addMember(new Member(memberName));
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
