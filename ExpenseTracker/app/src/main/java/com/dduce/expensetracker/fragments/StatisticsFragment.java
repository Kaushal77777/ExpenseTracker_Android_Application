package com.dduce.expensetracker.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dduce.expensetracker.models.Category;
import com.dduce.expensetracker.utils.DBHelper;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.dduce.expensetracker.adapter.StatisticsAdapter;
import com.dduce.expensetracker.databinding.FragmentStatisticsBinding;
import com.dduce.expensetracker.models.StatisticsModel;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.dduce.expensetracker.R;
import com.dduce.expensetracker.models.Entry;
import com.dduce.expensetracker.utils.CurrencyHelper;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class StatisticsFragment extends Fragment {
    FragmentStatisticsBinding binding;
    private List<Entry> entryList;
    private List<Category> categoriesList;
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
        //getStatisticsData();
        setCategories();
    }

    private void setCategories() {
        Category[] category_default = new Category[]{
                new Category(":Others","Others"),
                new Category(":Clothing","Clothing"),
                new Category(":Food","Food"),
                new Category(":Fuel","Fuel"),
                new Category(":Gaming","Gaming"),
                new Category(":Gift","Gift"),
                new Category(":Holidays","Holidays"),
                new Category(":Home","Home"),
                new Category(":Kids","Kids"),
                new Category(":Pharmacy","Pharmacy"),
                new Category(":Repair","Repair"),
                new Category(":Shopping","Shopping"),
                new Category(":Sport","Sport"),
                new Category(":Transfer","Transfer"),
                new Category(":Transport","Transport"),
                new Category(":Work","Work")
        };
        categoriesList = new ArrayList<>(Arrays.asList(category_default));
        DBHelper.getCategoryReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    Category category = data.getValue(Category.class);
                    if(category != null) {
                        String id = data.getKey();
                        String name = category.name;
                        categoriesList.add(new Category(id,name));
                    }
                }
                getStatisticsData();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                getStatisticsData();
            }
        });
        binding.etCategoryStat.setOnClickListener(v -> pickCategory());
    }

    private void pickCategory() {
        String[] categories = new String[categoriesList.size()];
        for(int i = 0; i < categoriesList.size(); i++) {
            categories[i] = categoriesList.get(i).getCategoryName();
        }
        new MaterialAlertDialogBuilder(getActivity())//this.getContext()
                .setTitle("Select Category")
                .setSingleChoiceItems(categories, 0, (dialog, which) -> {
                    updateBarChart(categories[which]);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void getStatisticsData() {
        DBHelper.getEntryReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(entryList.size() > 0){
                    entryList.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Entry entry = dataSnapshot.getValue(Entry.class);
                    entryList.add(entry);
                }
                updateData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateData() {
        if (entryList != null) {
            updatePieChart();
            if(categoriesList.size() > 0){
                updateBarChart(categoriesList.get(6).getCategoryName());
            }
        } else {
            Toast.makeText(getActivity(),"No Data Found", Toast.LENGTH_LONG).show();
        }
    }
    public void updatePieChart(){
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

        ///
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueTextColor(Color.BLACK);
        ///

        PieData data = new PieData(pieDataSet);
        binding.pieChart.setData(data);
        binding.pieChart.setTouchEnabled(false);
        binding.pieChart.getLegend().setEnabled(false);
        binding.pieChart.getDescription().setEnabled(false);

        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleColor(ContextCompat.getColor(getContext(), R.color.white));

        ///change this radius
        binding.pieChart.setHoleRadius(30f);
        binding.pieChart.setTransparentCircleRadius(40f);
        ///

        binding.pieChart.setDrawCenterText(true);
        binding.pieChart.setRotationAngle(270);
        binding.pieChart.setRotationEnabled(false);
        binding.pieChart.setHighlightPerTapEnabled(true);

        ///
        binding.pieChart.animateY(2000);
        binding.pieChart.setEntryLabelColor(Color.BLACK);
        ///

        binding.pieChart.invalidate();

        Collections.sort(categoryModelsHome, (o1, o2) -> Long.compare(o1.getMoney(), o2.getMoney()));
        adapter.notifyDataSetChanged();

        binding.tvExpenseAmount.setText(CurrencyHelper.formatCurrency(expensesSumInDateRange));
        binding.tvIncomeAmount.setText(CurrencyHelper.formatCurrency(incomesSumInDateRange));
        float progress = 100 * incomesSumInDateRange / (float) (incomesSumInDateRange - expensesSumInDateRange);
        binding.pbIncome.setProgress((int) progress);
    }
    public void updateBarChart(String entryCategory){
        binding.etCategoryStat.setText(entryCategory);
        long expensesSumInMonth = 0;
        HashMap<String, Long> monthlyExpenseModels = new HashMap<>();
        monthlyExpenseModels.put("01", 0L);monthlyExpenseModels.put("02", 0L);
        monthlyExpenseModels.put("03", 0L);monthlyExpenseModels.put("04", 0L);
        monthlyExpenseModels.put("05", 0L);monthlyExpenseModels.put("06", 0L);
        monthlyExpenseModels.put("07", 0L);monthlyExpenseModels.put("08", 0L);
        monthlyExpenseModels.put("09", 0L);monthlyExpenseModels.put("10", 0L);
        monthlyExpenseModels.put("11", 0L);monthlyExpenseModels.put("12", 0L);

        for(Entry entry : entryList){
            if(entry.category.equals(entryCategory)){
                Date date = new Date(entry.timestamp);
                DateFormat dateFormat = new SimpleDateFormat("MM", Locale.getDefault());
                String month = dateFormat.format(date);
                expensesSumInMonth = monthlyExpenseModels.get(month);
                expensesSumInMonth += entry.amount;
                monthlyExpenseModels.put(month, expensesSumInMonth);
            }
        }

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1,-1*monthlyExpenseModels.get("01")/100));
        barEntries.add(new BarEntry(2,-1*monthlyExpenseModels.get("02")/100));
        barEntries.add(new BarEntry(3,-1*monthlyExpenseModels.get("03")/100));
        barEntries.add(new BarEntry(4,-1*monthlyExpenseModels.get("04")/100));
        barEntries.add(new BarEntry(5,-1*monthlyExpenseModels.get("05")/100));
        barEntries.add(new BarEntry(6,-1*monthlyExpenseModels.get("06")/100));
        barEntries.add(new BarEntry(7,-1*monthlyExpenseModels.get("07")/100));
        barEntries.add(new BarEntry(8,-1*monthlyExpenseModels.get("08")/100));
        barEntries.add(new BarEntry(9,-1*monthlyExpenseModels.get("09")/100));
        barEntries.add(new BarEntry(10,-1*monthlyExpenseModels.get("10")/100));
        barEntries.add(new BarEntry(11,-1*monthlyExpenseModels.get("11")/100));
        barEntries.add(new BarEntry(12,-1*monthlyExpenseModels.get("12")/100));

        BarDataSet barDataSet = new BarDataSet(barEntries,"Category");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        binding.barChart.setFitBars(true);
        binding.barChart.setData(barData);
        binding.barChart.getDescription().setText("Categories wise expense");
        binding.barChart.animateY(2000);
    }

    @Override
    public void onResume() {
        getStatisticsData();
        super.onResume();
    }
}