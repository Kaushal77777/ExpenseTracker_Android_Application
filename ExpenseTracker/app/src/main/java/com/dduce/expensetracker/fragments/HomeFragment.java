package com.dduce.expensetracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dduce.expensetracker.utils.DBHelper;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.dduce.expensetracker.databinding.FragmentHomeBinding;
import com.dduce.expensetracker.adapter.HomeAdapter;
import com.dduce.expensetracker.models.HomeModel;
import com.dduce.expensetracker.utils.CurrencyHelper;
import com.dduce.expensetracker.R;
import com.dduce.expensetracker.models.Entry;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    private List<Entry> entryList;
    private HomeAdapter adapter;
    private ArrayList<HomeModel> categoryModelsHome;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        categoryModelsHome = new ArrayList<>();
        entryList = new ArrayList<>();
        adapter = new HomeAdapter(getActivity(), categoryModelsHome);
        binding.lvTopExpense.setAdapter(adapter);
        getHomeData();
    }

    private void getHomeData() {
        DBHelper.getEntryReference().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(entryList.size() > 0){
                    entryList.clear();
                }
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()){
                    Entry entry = dataSnapshot.getValue(Entry.class);
                    entryList.add(entry);
                }
                updateData();
            }
        });
    }
    private void updateData() {
        if (entryList != null) {
            long expensesSum = 0;
            long incomesSum = 0;

            HashMap<String, Long> categoryModels = new HashMap<>();
            for (Entry entry : entryList) {
                if (entry.amount > 0) {
                    incomesSum += entry.amount;
                    continue;
                }
                expensesSum += entry.amount;
                categoryModels.put(entry.category, entry.amount);
            }
            categoryModelsHome.clear();

            long incomePieValue = (long) (100 * incomesSum / (float) (incomesSum - expensesSum));
            long expensePieValue = 100 - incomePieValue;
            String incomePieLabel = CurrencyHelper.formatCurrency(incomesSum);
            String expensePieLabel = CurrencyHelper.formatCurrency(expensesSum);
            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            pieEntries.add(new PieEntry(incomePieValue, incomePieLabel));
            pieEntries.add(new PieEntry(expensePieValue, expensePieLabel));

            ArrayList<Integer> pieColors = new ArrayList<>();
            pieColors.add(ContextCompat.getColor(getContext(), R.color.colorIncome));
            pieColors.add(ContextCompat.getColor(getContext(), R.color.colorExpense));

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
            pieDataSet.setDrawValues(false);
            pieDataSet.setColors(pieColors);
            pieDataSet.setSliceSpace(2f);

            PieData data = new PieData(pieDataSet);
            binding.pieChart.setData(data);
            binding.pieChart.setTouchEnabled(false);
            binding.pieChart.getLegend().setEnabled(false);
            binding.pieChart.getDescription().setEnabled(false);

            binding.pieChart.setDrawHoleEnabled(true);
            binding.pieChart.setHoleColor(ContextCompat.getColor(getContext(), R.color.white));
            binding.pieChart.setHoleRadius(55f);
            binding.pieChart.setTransparentCircleRadius(55f);
            binding.pieChart.setDrawCenterText(true);
            binding.pieChart.setRotationAngle(270);
            binding.pieChart.setRotationEnabled(false);
            binding.pieChart.setHighlightPerTapEnabled(true);

            binding.pieChart.invalidate();

            for (Map.Entry<String, Long> categoryModel : categoryModels.entrySet()) {
                categoryModelsHome.add(new HomeModel(categoryModel.getKey(), categoryModel.getValue()));
            }

            Collections.sort(categoryModelsHome, (o1, o2) -> Long.compare(o1.getMoney(), o2.getMoney()));


            adapter.notifyDataSetChanged();
            long totalAmount = incomesSum + expensesSum;
            binding.tvTotalAmount.setText(CurrencyHelper.formatCurrency(totalAmount));
            binding.tvIncomeAmount.setText(CurrencyHelper.formatCurrency(incomesSum));
            binding.tvExpenseAmount.setText(CurrencyHelper.formatCurrency(expensesSum));
        } else {
            Toast.makeText(getActivity(),"No Data Found", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onResume() {
        getHomeData();
        super.onResume();
    }
}

