package com.dduce.expensetracker.activities;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.dduce.expensetracker.utils.DBHelper;
import com.dduce.expensetracker.databinding.ActivityEditMemberBinding;
import com.dduce.expensetracker.models.Member;

public class EditMemberActivity extends AppCompatActivity {
    ActivityEditMemberBinding binding;
    private String memberID;
    private String memberName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        memberID = getIntent().getExtras().getString("member-id");
        memberName = getIntent().getExtras().getString("member-name");

        binding.etMemberName.setText(memberName);
        binding.btnSaveMember.setOnClickListener(v -> {
            memberName = binding.etMemberName.getText().toString();
            if(memberName.isEmpty()){
                binding.ilMemberName.setError("Please Enter Member Name");
            } else {
                DBHelper.updateMember(memberID, new Member(memberName));
                finish();
            }
        });

        binding.btnRemoveMember.setOnClickListener(v -> {
            DBHelper.deleteMember(memberID);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
