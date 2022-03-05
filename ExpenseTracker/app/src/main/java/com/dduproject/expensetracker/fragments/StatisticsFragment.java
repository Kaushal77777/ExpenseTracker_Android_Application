package com.dduproject.expensetracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dduproject.expensetracker.utils.DBHelper;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.dduproject.expensetracker.adapter.StatisticsAdapter;
import com.dduproject.expensetracker.databinding.FragmentStatisticsBinding;
import com.dduproject.expensetracker.models.StatisticsModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dduproject.expensetracker.R;
import com.dduproject.expensetracker.models.Entry;
import com.dduproject.expensetracker.utils.CurrencyHelper;


public class StatisticsFragment extends Fragment {
    FragmentStatisticsBinding binding;
    private List<Entry> entryList;
    private ArrayList<StatisticsModel> categoryModelsHome;
    private StatisticsAdapter adapter;

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        categoryModelsHome = new ArrayList<>();
        entryList = new ArrayList<>();
        adapter = new StatisticsAdapter( getActivity(),categoryModelsHome);
        binding.lvTopExpense.setAdapter(adapter);
        getStatisticsData();
    }

    private void getStatisticsData() {
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
            long expensesSumInDateRange = 0;
            long incomesSumInDateRange = 0;
            HashMap<String, Long> categoryModels = new HashMap<>();
            for (Entry entry :entryList) {
                if (entry.amount > 0) {
                    incomesSumInDateRange += entry.amount;
                    continue;
                }
                expensesSumInDateRange += entry.amount;
                categoryModels.put(entry.category, entry.amount);
            }
            categoryModelsHome.clear();
            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            for (Map.Entry<String, Long> categoryModel : categoryModels.entrySet()) {
                long pieValue = -categoryModel.getValue();
                String pieCategory = categoryModel.getKey();
                float piePercentage = categoryModel.getValue() / (float) expensesSumInDateRange;
                categoryModelsHome.add(new StatisticsModel(pieCategory, categoryModel.getValue(), piePercentage));
                if (piePercentage > 0.1f) {
                    pieEntries.add(new PieEntry(pieValue, pieCategory));
                } else {
                    pieEntries.add(new PieEntry(pieValue));
                }
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
            pieDataSet.setDrawValues(false);
            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
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

            Collections.sort(categoryModelsHome, (o1, o2) -> Long.compare(o1.getMoney(), o2.getMoney()));
            adapter.notifyDataSetChanged();

            binding.tvExpenseAmount.setText(CurrencyHelper.formatCurrency(expensesSumInDateRange));
            binding.tvIncomeAmount.setText(CurrencyHelper.formatCurrency(incomesSumInDateRange));
            float progress = 100 * incomesSumInDateRange / (float) (incomesSumInDateRange - expensesSumInDateRange);
            binding.pbIncome.setProgress((int) progress);
        } else {
            Toast.makeText(getActivity(),"No Data Found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        getStatisticsData();
        super.onResume();
    }
}