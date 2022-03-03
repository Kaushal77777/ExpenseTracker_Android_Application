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

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

}
