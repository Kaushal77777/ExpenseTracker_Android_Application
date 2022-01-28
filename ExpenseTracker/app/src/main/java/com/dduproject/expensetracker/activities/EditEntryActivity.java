package com.dduproject.expensetracker.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.dduproject.expensetracker.databinding.ActivityEditEntryBinding;
import com.dduproject.expensetracker.exceptions.EmptyStringException;
import com.dduproject.expensetracker.exceptions.ZeroBalanceDifferenceException;
import com.dduproject.expensetracker.models.User;
import com.dduproject.expensetracker.models.Member;
import com.dduproject.expensetracker.utils.CategoriesHelper;
import com.dduproject.expensetracker.models.Category;
import com.dduproject.expensetracker.utils.CurrencyHelper;
import com.dduproject.expensetracker.models.WalletEntry;
import com.dduproject.expensetracker.utils.MembersHelper;

public class EditEntryActivity extends BaseActivity {
    ActivityEditEntryBinding binding;
    private Calendar selectedDate;
    private User user;
    private String walletEntryId;
    private String walletEntryCategory;
    private String walletEntryName;
    private String walletEntryMember;
    private long walletEntryTimestamp;
    private long walletEntryBalanceDiff;
    int entryType = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        walletEntryId = getIntent().getExtras().getString("wallet-entry-id");
        walletEntryCategory = getIntent().getExtras().getString("wallet-entry-category");
        walletEntryName = getIntent().getExtras().getString("wallet-entry-name");
        walletEntryMember = getIntent().getExtras().getString("wallet-entry-member");
        walletEntryTimestamp = getIntent().getExtras().getLong("wallet-entry-timestamp");
        walletEntryBalanceDiff = getIntent().getExtras().getLong("wallet-entry-balance-difference");
        selectedDate = Calendar.getInstance();
        binding.rgEntryType.setOnCheckedChangeListener((group, checkedId) -> entryType = group.indexOfChild(findViewById(checkedId)));
        updateDate();
        binding.etDate.setOnClickListener(v -> pickDate());
        binding.etTime.setOnClickListener(v -> pickTime());
        binding.etCategory.setOnClickListener(v -> pickCategory());
        binding.etMember.setOnClickListener(v -> pickMember());
        binding.btnSaveEntry.setOnClickListener(v -> {
            try {
                long balanceDifference = ((entryType * 2) - 1) * CurrencyHelper.convertAmountStringToLong(binding.etAmount.getText().toString());
                Date entryDate = selectedDate.getTime();
                String entryCategory = binding.etCategory.getText().toString();
                String entryMember = binding.etMember.getText().toString();
                String entryName =  binding.etName.getText().toString();
                editWalletEntry(balanceDifference,entryDate,entryCategory,entryMember,entryName);
            }  catch (EmptyStringException e) {
                binding.ilName.setError(e.getMessage());
            } catch (ZeroBalanceDifferenceException e) {
                binding.ilAmount.setError(e.getMessage());
            }
        });

        binding.btnRemoveEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemoveWalletEntryDialog();
            }

            public void showRemoveWalletEntryDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditEntryActivity.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", (dialog, which) -> removeWalletEntry()).setNegativeButton("No", null).show();
            }
        });
        getData();
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
    public void updateData() {
        binding.etCategory.setText(walletEntryCategory);
        binding.etMember.setText(walletEntryMember);
        binding.etName.setText(walletEntryName);

        CurrencyHelper.setupAmountEditText(binding.etAmount);
        selectedDate.setTimeInMillis(-walletEntryTimestamp);
        updateDate();

        long amount = Math.abs(walletEntryBalanceDiff);
        String current = CurrencyHelper.formatCurrency(amount);
        binding.etAmount.setText(current);
        binding.etAmount.setSelection(current.length());
    }


    private void updateDate() {
        SimpleDateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        binding.etDate.setText(dataFormatter.format(selectedDate.getTime()));
        SimpleDateFormat dataFormatter2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        binding.etTime.setText(dataFormatter2.format(selectedDate.getTime()));
    }

    public void editWalletEntry(long balanceDifference, Date entryDate, String entryCategory, String entryMember, String entryName) throws EmptyStringException, ZeroBalanceDifferenceException {
        if (balanceDifference == 0) {
            throw new ZeroBalanceDifferenceException("Balance difference should not be 0");
        }

        if (entryName == null || entryName.length() == 0) {
            throw new EmptyStringException("Entry name length should be > 0");
        }

        long finalBalanceDifference = balanceDifference - walletEntryBalanceDiff;
        user.wallet += finalBalanceDifference;
        FirebaseDatabase.getInstance().getReference().child("wallet-entries").child(getUid()).child("default").child(walletEntryId).setValue(new WalletEntry(entryCategory, entryMember, entryName, entryDate.getTime(), balanceDifference));
        FirebaseDatabase.getInstance().getReference().child("users").child(getUid()).child("wallet").setValue(user.wallet);
        finish();
    }

    public void removeWalletEntry() {
        user.wallet -= walletEntryBalanceDiff;
        FirebaseDatabase.getInstance().getReference().child("users").child(getUid()).child("wallet").setValue(user.wallet);
        FirebaseDatabase.getInstance().getReference().child("wallet-entries").child(getUid()).child("default").child(walletEntryId).removeValue();
        finish();
    }

    private void pickCategory() {
        final List<Category> categoriesList = CategoriesHelper.getCategories(user);
        String[] categories = new String[categoriesList.size()];
        int selectedItem = 0;
        for(int i = 0; i < categoriesList.size(); i++) {
            categories[i] = categoriesList.get(i).getCategoryName();
            if(categoriesList.get(i).getCategoryName().equals(walletEntryCategory)){
                selectedItem = i;
            }
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Category")
                .setSingleChoiceItems(categories, selectedItem, (dialog, which) -> {
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
        int selectedItem = 0;
        for(int i = 0; i < membersList.size(); i++) {
            members[i] = membersList.get(i).getMemberName();
            if(membersList.get(i).getMemberName().equals(walletEntryMember)){
                selectedItem = i;
            }
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Member")
                .setSingleChoiceItems(members, selectedItem, (dialog, which) -> {
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
            selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDate.set(Calendar.MINUTE, minute);
            updateDate();
        }, selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE), true).show();
    }

    private void pickDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            selectedDate.set(year1, monthOfYear, dayOfMonth);
            updateDate();
        }, year, month, day).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
