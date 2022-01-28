package com.dduproject.expensetracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.dduproject.expensetracker.activities.CategoriesActivity;
import com.dduproject.expensetracker.activities.MembersActivity;
import com.dduproject.expensetracker.activities.SignInActivity;
import com.dduproject.expensetracker.adapter.HistoryAdapter;
import com.dduproject.expensetracker.databinding.FragmentHistoryBinding;
import com.dduproject.expensetracker.models.WalletEntry;
import java.util.ArrayList;

import com.dduproject.expensetracker.R;

public class HistoryFragment extends Fragment {
    FragmentHistoryBinding binding;
    private ArrayList<WalletEntry> walletEntryList;
    private HistoryAdapter historyAdapter;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        walletEntryList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(getActivity(), walletEntryList);
        binding.lvHistory.setAdapter(historyAdapter);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("wallet-entries").child(getUid()).child("default").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(walletEntryList.size() > 0){
                    walletEntryList.clear();
                }
                for (DataSnapshot postSnapShot:dataSnapshot.getChildren()){
                    WalletEntry walletEntry = postSnapShot.getValue(WalletEntry.class);
                    if(walletEntry != null) {
                        String walletEntryId = postSnapShot.getKey();
                        String walletEntryCategory = walletEntry.category;
                        String walletEntryMember = walletEntry.member;
                        String walletEntryName = walletEntry.name;
                        long walletEntryTimeStamp = walletEntry.timestamp;
                        long walletEntryBalanceDifference = walletEntry.balanceDifference;
                        walletEntryList.add(new WalletEntry(walletEntryId,walletEntryCategory,walletEntryMember,walletEntryName,walletEntryTimeStamp,walletEntryBalanceDifference));
                    }
                }
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_manage_categories){
            startActivity(new Intent(getActivity(), CategoriesActivity.class));
            return true;
        } else if(id == R.id.menu_manage_members){
            startActivity(new Intent(getActivity(), MembersActivity.class));
            return true;
        } else if(id == R.id.menu_logout){
            FirebaseAuth.getInstance().signOut();
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("com.dduproject.expensetracker.ACTION_LOGOUT");
            if(getActivity() != null){
                getActivity().sendBroadcast(broadcastIntent);
                getActivity().startActivity(new Intent(getActivity(), SignInActivity.class));
                getActivity().finish();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
