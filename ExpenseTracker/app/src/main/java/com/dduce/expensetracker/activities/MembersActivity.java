package com.dduce.expensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dduce.expensetracker.utils.DBHelper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.dduce.expensetracker.adapter.MembersAdapter;
import com.dduce.expensetracker.databinding.ActivityMembersBinding;
import com.dduce.expensetracker.models.Member;

public class MembersActivity extends AppCompatActivity {
    ActivityMembersBinding binding;
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

        FirebaseRecyclerOptions<Member> options = new FirebaseRecyclerOptions.Builder<Member>()
                .setQuery(DBHelper.getMembers(), snapshot -> new Member(snapshot.getKey(), snapshot.child("name").getValue().toString()))
                .build();

        membersAdapter = new MembersAdapter(getApplicationContext(), options);

        binding.rvMembers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMembers.setAdapter(membersAdapter);

        binding.fabAddMember.setOnClickListener(v ->
                startActivity(new Intent(MembersActivity.this, AddMemberActivity.class))
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
        membersAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        membersAdapter.stopListening();
    }
}
