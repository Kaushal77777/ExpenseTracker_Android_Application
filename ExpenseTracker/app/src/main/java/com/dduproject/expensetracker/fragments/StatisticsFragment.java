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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.dduproject.expensetracker.activities.CategoriesActivity;
import com.dduproject.expensetracker.activities.MembersActivity;
import com.dduproject.expensetracker.activities.SignInActivity;
import com.dduproject.expensetracker.adapter.StatisticsAdapter;
import com.dduproject.expensetracker.databinding.FragmentStatisticsBinding;
import com.dduproject.expensetracker.models.StatisticsModel;
import com.dduproject.expensetracker.utils.CalendarHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.dduproject.expensetracker.R;
import com.dduproject.expensetracker.models.WalletEntry;
import com.dduproject.expensetracker.utils.CurrencyHelper;


public class StatisticsFragment extends Fragment {
    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }
}
