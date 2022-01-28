package com.dduproject.expensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import java.util.ArrayList;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.dduproject.expensetracker.adapter.MembersAdapter;
import com.dduproject.expensetracker.databinding.ActivityMembersBinding;
import com.dduproject.expensetracker.models.User;
import com.dduproject.expensetracker.models.Member;
import com.dduproject.expensetracker.utils.MembersHelper;

public class MembersActivity extends BaseActivity {
    ActivityMembersBinding binding;
    private User user;
    private ArrayList<Member> membersList;
    private MembersAdapter membersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMembersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getData();

        membersList = new ArrayList<>();
        membersAdapter = new MembersAdapter(this, membersList, getApplicationContext());
        binding.lvMembers.setAdapter(membersAdapter);

        binding.fabAddMember.setOnClickListener(v ->
                startActivity(new Intent(MembersActivity.this, AddMemberActivity.class))
        );

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
        membersList.clear();
        membersList.addAll(MembersHelper.getCustomMembers(user));
        membersAdapter.notifyDataSetChanged();
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
