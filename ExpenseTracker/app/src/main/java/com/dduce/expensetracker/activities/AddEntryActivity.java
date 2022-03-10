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
import com.dduce.expensetracker.databinding.ActivityAddEntryBinding;
import com.dduce.expensetracker.models.Member;
import com.dduce.expensetracker.models.Category;
import com.dduce.expensetracker.utils.CurrencyHelper;
import com.dduce.expensetracker.models.Entry;
import com.google.firebase.database.ValueEventListener;

public class AddEntryActivity extends AppCompatActivity {
    ActivityAddEntryBinding binding;
    private Calendar chosenDate;
    int entryType = 0;
    List<Category> categoriesList;
    List<Member> membersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        chosenDate = Calendar.getInstance();
        binding.rgEntryType.setOnCheckedChangeListener((group, checkedId) -> entryType = group.indexOfChild(findViewById(checkedId)));
        setCategories();
        setMembers();
        setDateTime();
        binding.etDate.setOnClickListener(v -> pickDate());
        binding.etTime.setOnClickListener(v -> pickTime());
        CurrencyHelper.setupAmountEditText(binding.etAmount);
        binding.btnAddEntry.setOnClickListener(v -> {
            Date entryDate = chosenDate.getTime();
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
                DBHelper.addEntry(new Entry(entryCategory, entryMember, entryName, entryDate.getTime(), amount));
                finish();
            }
        });
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
        binding.etCategory.setText(categoriesList.get(0).getCategoryName());
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
        binding.etMember.setText(membersList.get(0).getMemberName());
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
        binding.etDate.setText(dataFormatter.format(chosenDate.getTime()));
        SimpleDateFormat dataFormatter2 = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        binding.etTime.setText(dataFormatter2.format(chosenDate.getTime()));
    }

    private void pickCategory() {
        String[] categories = new String[categoriesList.size()];
        for(int i = 0; i < categoriesList.size(); i++) {
            categories[i] = categoriesList.get(i).getCategoryName();
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Category")
                .setSingleChoiceItems(categories, 0, (dialog, which) -> {
                    binding.etCategory.setText(categories[which]);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void pickMember() {
        String[] members = new String[membersList.size()];
        for(int i = 0; i < membersList.size(); i++) {
            members[i] = membersList.get(i).getMemberName();
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Member")
                .setSingleChoiceItems(members, 0, (dialog, which) -> {
                    binding.etMember.setText(members[which]);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void pickTime() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            chosenDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            chosenDate.set(Calendar.MINUTE, minute);
            setDateTime();
        }, chosenDate.get(Calendar.HOUR_OF_DAY), chosenDate.get(Calendar.MINUTE), true).show();
    }

    private void pickDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            chosenDate.set(year1, monthOfYear, dayOfMonth);
            setDateTime();
        }, year, month, day).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }


}
