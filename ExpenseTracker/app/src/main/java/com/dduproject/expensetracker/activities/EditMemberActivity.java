package com.dduproject.expensetracker.activities;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.database.FirebaseDatabase;
import com.dduproject.expensetracker.databinding.ActivityEditMemberBinding;
import com.dduproject.expensetracker.exceptions.EmptyStringException;
import com.dduproject.expensetracker.models.Member;

public class EditMemberActivity extends BaseActivity {
    ActivityEditMemberBinding binding;
    private String memberID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String memberName = getIntent().getExtras().getString("member-name");
        memberID = getIntent().getExtras().getString("member-id");
        binding.etMemberName.setText(memberName);
        binding.btnSaveMember.setOnClickListener(v -> {
            try {
                editMember(binding.etMemberName.getText().toString());
            } catch (EmptyStringException e) {
                binding.ilMemberName.setError(e.getMessage());
            }
        });

        binding.btnRemoveMember.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("users").child(getUid()).child("members").child(memberID).removeValue();
            finish();
        });
    }

    private void editMember(String memberName) throws EmptyStringException {
        if(memberName == null || memberName.length() == 0) {
            throw new EmptyStringException("Entry name length should be > 0");
        }
        FirebaseDatabase.getInstance().getReference().child("users").child(getUid()).child("members").child(memberID).setValue(new Member(memberName));
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
