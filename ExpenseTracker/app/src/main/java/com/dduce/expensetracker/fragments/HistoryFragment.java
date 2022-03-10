package com.dduce.expensetracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dduce.expensetracker.utils.DBHelper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.dduce.expensetracker.adapter.HistoryAdapter;
import com.dduce.expensetracker.databinding.FragmentHistoryBinding;
import com.dduce.expensetracker.models.Entry;

public class HistoryFragment extends Fragment {
    FragmentHistoryBinding binding;
    HistoryAdapter historyAdapter;
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
        FirebaseRecyclerOptions<Entry> options = new FirebaseRecyclerOptions.Builder<Entry>()
                .setQuery(DBHelper.getEntries(), snapshot -> {
                    Entry entry = snapshot.getValue(Entry.class);
                    String entryId = snapshot.getKey();
                    String entryCategory = entry.category;
                    String entryMember = entry.member;
                    String entryName = entry.name;
                    long entryTimeStamp = entry.timestamp;
                    long entryAmount = entry.amount;
                    return new Entry(entryId,entryCategory,entryMember,entryName,entryTimeStamp,entryAmount);
                })
                .build();
        historyAdapter = new HistoryAdapter(getActivity(), options);
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvHistory.setAdapter(historyAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        historyAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        historyAdapter.stopListening();
    }
}
