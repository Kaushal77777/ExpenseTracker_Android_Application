package com.dduproject.expensetracker.activities;

import android.os.Bundle;
import android.view.MenuItem;
import com.google.firebase.database.FirebaseDatabase;
import com.dduproject.expensetracker.databinding.ActivityAddMemberBinding;
import com.dduproject.expensetracker.exceptions.EmptyStringException;
import com.dduproject.expensetracker.models.Member;

public class AddMemberActivity extends BaseActivity {
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
            try {
                addMember(binding.etMemberName.getText().toString());
            } catch (EmptyStringException e) {
                binding.ilMemberName.setError(e.getMessage());
            }
        });
    }

    private void addMember(String memberName) throws EmptyStringException {
        if(memberName == null || memberName.length() == 0) {
            throw new EmptyStringException("Entry name length should be > 0");
        }
        FirebaseDatabase.getInstance().getReference().child("users").child(getUid()).child("members").push().setValue(new Member(memberName));
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
