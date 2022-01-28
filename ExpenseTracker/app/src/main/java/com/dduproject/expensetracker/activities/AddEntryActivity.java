package com.dduproject.expensetracker.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.dduproject.expensetracker.databinding.ActivityAddEntryBinding;
import com.dduproject.expensetracker.exceptions.EmptyStringException;
import com.dduproject.expensetracker.exceptions.ZeroBalanceDifferenceException;
import com.dduproject.expensetracker.models.User;
import com.dduproject.expensetracker.models.Member;
import com.dduproject.expensetracker.utils.CategoriesHelper;
import com.dduproject.expensetracker.models.Category;
import com.dduproject.expensetracker.utils.CurrencyHelper;
import com.dduproject.expensetracker.models.WalletEntry;
import com.dduproject.expensetracker.utils.MembersHelper;

public class AddEntryActivity extends BaseActivity {
    ActivityAddEntryBinding binding;
    private Calendar chosenDate;
    private User user;
    int entryType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getData();
        chosenDate = Calendar.getInstance();
        binding.rgEntryType.setOnCheckedChangeListener((group, checkedId) -> entryType = group.indexOfChild(findViewById(checkedId)));
        updateDate();
        binding.etDate.setOnClickListener(v -> pickDate());
        binding.etTime.setOnClickListener(v -> pickTime());
        binding.etCategory.setOnClickListener(v -> pickCategory());
        binding.etMember.setOnClickListener(v -> pickMember());
        binding.btnAddEntry.setOnClickListener(v -> {
            try {
                Date entryDate = chosenDate.getTime();
                String entryAmount = binding.etAmount.getText().toString();
                String entryCategory = binding.etCategory.getText().toString();
                String entryMember = binding.etMember.getText().toString();
                String entryName =  binding.etName.getText().toString();
                int balanceType = (entryType * 2) - 1;
                long balanceDifference = balanceType * CurrencyHelper.convertAmountStringToLong(entryAmount);
                addToWallet(balanceDifference,entryDate,entryCategory,entryMember,entryName);
            } catch (EmptyStringException e) {
                binding.ilName.setError(e.getMessage());
            } catch (ZeroBalanceDifferenceException e) {
                binding.ilAmount.setError(e.getMessage());
            }
        });
    }
    private void getData(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(getUid()).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e( "Error getting data", String.valueOf(task.getException()));
            } else {
                user = task.getResult().getValue(User.class);
                updateData();
            }
        });
    }
    private void updateData() {
        if (user == null) {
            return;
        }

        final List<Category> categoriesList = CategoriesHelper.getCategories(user);
        binding.etCategory.setText(categoriesList.get(0).getCategoryName());

        final List<Member> membersList = MembersHelper.getMembers(user);
        binding.etMember.setText(membersList.get(0).getMemberName());

        CurrencyHelper.setupAmountEditText(binding.etAmount);
    }


    private void updateDate() {
        SimpleDateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        binding.etDate.setText(dataFormatter.format(chosenDate.getTime()));
        SimpleDateFormat dataFormatter2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        binding.etTime.setText(dataFormatter2.format(chosenDate.getTime()));
    }

    public void addToWallet(long balanceDifference, Date entryDate, String entryCategory, String entryMember, String entryName) throws ZeroBalanceDifferenceException, EmptyStringException {
        if (balanceDifference == 0) {
            throw new ZeroBalanceDifferenceException("Balance difference should not be 0");
        }

        if (entryName == null || entryName.length() == 0) {
            throw new EmptyStringException("Entry name length should be > 0");
        }

        FirebaseDatabase.getInstance().getReference().child("wallet-entries").child(getUid()).child("default").push().setValue(new WalletEntry(entryCategory, entryMember, entryName, entryDate.getTime(), balanceDifference));
        user.wallet += balanceDifference;
        FirebaseDatabase.getInstance().getReference().child("users").child(getUid()).child("wallet").setValue(user.wallet);
        finish();
    }
    private void pickCategory() {
        final List<Category> categoriesList = CategoriesHelper.getCategories(user);
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
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
    private void pickMember() {
        final List<Member> membersList = MembersHelper.getMembers(user);
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
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
    private void pickTime() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            chosenDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            chosenDate.set(Calendar.MINUTE, minute);
            updateDate();
        }, chosenDate.get(Calendar.HOUR_OF_DAY), chosenDate.get(Calendar.MINUTE), true).show();
    }


    private void pickDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            chosenDate.set(year1, monthOfYear, dayOfMonth);
            updateDate();
        }, year, month, day).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }


}
