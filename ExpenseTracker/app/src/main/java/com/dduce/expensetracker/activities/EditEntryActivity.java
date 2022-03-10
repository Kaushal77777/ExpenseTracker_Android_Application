package com.dduce.expensetracker.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dduce.expensetracker.utils.DBHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.dduce.expensetracker.databinding.ActivityEditEntryBinding;
import com.dduce.expensetracker.models.Member;
import com.dduce.expensetracker.models.Category;
import com.dduce.expensetracker.utils.CurrencyHelper;
import com.dduce.expensetracker.models.Entry;
import com.google.firebase.database.ValueEventListener;

public class EditEntryActivity extends AppCompatActivity {
    ActivityEditEntryBinding binding;
    private Calendar selectedDate;
    private String entryId;
    private String entryCategory;
    private String entryName;
    private String entryMember;
    private long entryTimestamp;
    private long entryAmount;
    int entryType = 0;
    List<Category> categoriesList;
    List<Member> membersList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        entryId = getIntent().getExtras().getString("entry-id");
        entryCategory = getIntent().getExtras().getString("entry-category");
        entryName = getIntent().getExtras().getString("entry-name");
        entryMember = getIntent().getExtras().getString("entry-member");
        entryTimestamp = getIntent().getExtras().getLong("entry-timestamp");
        entryAmount = getIntent().getExtras().getLong("entry-amount");

        selectedDate = Calendar.getInstance();
        binding.rgEntryType.setOnCheckedChangeListener((group, checkedId) -> entryType = group.indexOfChild(findViewById(checkedId)));
        setCategories();
        setMembers();
        setDateTime();
        binding.etDate.setOnClickListener(v -> pickDate());
        binding.etTime.setOnClickListener(v -> pickTime());
        CurrencyHelper.setupAmountEditText(binding.etAmount);
        binding.btnSaveEntry.setOnClickListener(v -> {
            Date entryDate = selectedDate.getTime();
            String entryAmount = binding.etAmount.getText().toString();
            String entryCategory = binding.etCategory.getText().toString();
            String entryMember = binding.etMember.getText().toString();
            String entryName =  binding.etName.getText().toString();
            int type = (entryType * 2) - 1;
            long amount = type * CurrencyHelper.convertAmountStringToLong(entryAmount);
            if(entryName.isEmpty()){
                binding.ilName.setError("Please Enter Name");
            } else if(amount == 0){
                binding.ilAmount.setError("Please Enter Amount");
            } else {
                DBHelper.updateEntry(entryId, new Entry(entryCategory, entryMember, entryName, entryDate.getTime(), amount));
                finish();
            }
        });

        binding.btnRemoveEntry.setOnClickListener(v -> new MaterialAlertDialogBuilder(EditEntryActivity.this)
                .setTitle("Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    DBHelper.deleteEntry(entryId);
                    finish();
                })
                .setNegativeButton("No", null)
                .show());
        updateData();
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        binding.etCategory.setOnClickListener(v -> pickCategory());
    }
    private void setMembers() {
        Member[] member_default = new Member[]{
                new Member(":Admin","Admin")
        };
        membersList = new ArrayList<>(Arrays.asList(member_default));
        DBHelper.getMemberReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    Member member = data.getValue(Member.class);
                    if(member != null) {
                        String id = data.getKey();
                        String name = member.name;
                        membersList.add(new Member(id,name));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        binding.etMember.setOnClickListener(v -> pickMember());
    }
    private void setDateTime() {
        SimpleDateFormat dataFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        binding.etDate.setText(dataFormatter.format(selectedDate.getTime()));
        SimpleDateFormat dataFormatter2 = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        binding.etTime.setText(dataFormatter2.format(selectedDate.getTime()));
    }
    public void updateData() {
        if (entryAmount < 0) {
            binding.rbExpense.setChecked(true);
            entryType = 0;
        } else {
            binding.rbIncome.setChecked(true);
            entryType = 1;
        }
        binding.etCategory.setText(entryCategory);
        binding.etMember.setText(entryMember);
        binding.etName.setText(entryName);
        selectedDate.setTimeInMillis(entryTimestamp);
        setDateTime();
        long amount = Math.abs(entryAmount);
        String current = CurrencyHelper.formatCurrency(amount);
        binding.etAmount.setText(current);
        binding.etAmount.setSelection(current.length());
    }

    private void pickCategory() {
        String[] categories = new String[categoriesList.size()];
        int selectedItem = 0;
        for(int i = 0; i < categoriesList.size(); i++) {
            categories[i] = categoriesList.get(i).getCategoryName();
            if(categoriesList.get(i).getCategoryName().equals(entryCategory)){
                selectedItem = i;
            }
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Category")
                .setSingleChoiceItems(categories, selectedItem, (dialog, which) -> {
                    binding.etCategory.setText(categories[which]);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void pickMember() {
        String[] members = new String[membersList.size()];
        int selectedItem = 0;
        for(int i = 0; i < membersList.size(); i++) {
            members[i] = membersList.get(i).getMemberName();
            if(membersList.get(i).getMemberName().equals(entryMember)){
                selectedItem = i;
            }
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Member")
                .setSingleChoiceItems(members, selectedItem, (dialog, which) -> {
                    binding.etMember.setText(members[which]);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void pickTime() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDate.set(Calendar.MINUTE, minute);
            setDateTime();
        }, selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE), true).show();
    }

    private void pickDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            selectedDate.set(year1, monthOfYear, dayOfMonth);
            setDateTime();
        }, year, month, day).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
