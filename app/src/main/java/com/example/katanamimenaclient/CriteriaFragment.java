package com.example.katanamimenaclient;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CriteriaFragment extends Fragment {

    private int adultsCount = 1;
    private TextView textAdultsCount;

    public CriteriaFragment() {
        // Required empty public constructor
    }

    public static CriteriaFragment newInstance() {
        return new CriteriaFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_criteria, container, false);

        textAdultsCount = view.findViewById(R.id.textAdultsCount);
        Button buttonIncrease = view.findViewById(R.id.buttonIncrease);
        Button buttonDecrease = view.findViewById(R.id.buttonDecrease);

        buttonIncrease.setOnClickListener(v -> {
            adultsCount++;
            textAdultsCount.setText(String.valueOf(adultsCount));
        });

        buttonDecrease.setOnClickListener(v -> {
            if (adultsCount > 1) {
                adultsCount--;
                textAdultsCount.setText(String.valueOf(adultsCount));
            }
        });

        return view;
    }
}
