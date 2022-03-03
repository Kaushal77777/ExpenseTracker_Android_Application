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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dduproject.expensetracker.utils.DBHelper;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.dduproject.expensetracker.databinding.FragmentHomeBinding;
import com.dduproject.expensetracker.adapter.HomeAdapter;
import com.dduproject.expensetracker.models.HomeModel;
import com.dduproject.expensetracker.utils.CurrencyHelper;
import com.dduproject.expensetracker.R;
import com.dduproject.expensetracker.models.Entry;

public class HomeFragment extends Fragment {

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

}
