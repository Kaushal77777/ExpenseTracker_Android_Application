package com.dduce.expensetracker.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class CurrencyHelper {
    public static String formatCurrency(long money) {
        long absMoney = Math.abs(money);
        return "₹ " + (money < 0 ? "-" : "") + (absMoney / 100) + "." + (absMoney % 100 < 10 ? "0" : "") + (absMoney % 100);
    }
    public static void setupAmountEditText(EditText editText) {
        editText.setText(CurrencyHelper.formatCurrency(0), TextView.BufferType.EDITABLE);
        editText.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }


            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!charSequence.toString().equals(current)) {
                    editText.removeTextChangedListener(this);
                    current = CurrencyHelper.formatCurrency(convertAmountStringToLong(charSequence));
                    editText.setText(current);
                    editText.setSelection(current.length());
                    editText.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public static long convertAmountStringToLong(CharSequence s) {
        String cleanString = s.toString().replaceAll("[^0-9]", "");
        return Long.parseLong(cleanString);
    }
}
