package com.example.katanamimenaclient;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.katanamimenaclient.adapter.RoomDetailsAdapter;
import com.example.katanamimenaclient.model.RoomDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CriteriaFragment extends Fragment {

    private int adultsCount = 1;
    private TextView textAdultsCount;
    private EditText editTextArrivalDate, editTextDepartureDate;
    private CheckBox checkBoxStar1, checkBoxStar2, checkBoxStar3, checkBoxStar4, checkBoxStar5;
    private RecyclerView recyclerView;
    private ScrollView criteriaContainer;
    private TextView resultsHeader;
    private Button searchButton;

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
        View view = inflater.inflate(R.layout.fragment_criteria, container, false);

        criteriaContainer = view.findViewById(R.id.criteriaContainer);
        textAdultsCount = view.findViewById(R.id.textAdultsCount);
        Button buttonIncrease = view.findViewById(R.id.buttonIncrease);
        Button buttonDecrease = view.findViewById(R.id.buttonDecrease);
        searchButton = view.findViewById(R.id.searchButton);
        resultsHeader = view.findViewById(R.id.resultsHeader);

        editTextArrivalDate = view.findViewById(R.id.arrivalInput);
        editTextDepartureDate = view.findViewById(R.id.departureInput);

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

        editTextArrivalDate.setOnClickListener(v -> showDatePickerDialog(true));
        editTextDepartureDate.setOnClickListener(v -> showDatePickerDialog(false));

        checkBoxStar1 = view.findViewById(R.id.checkBoxStar1);
        checkBoxStar2 = view.findViewById(R.id.checkBoxStar2);
        checkBoxStar3 = view.findViewById(R.id.checkBoxStar3);
        checkBoxStar4 = view.findViewById(R.id.checkBoxStar4);
        checkBoxStar5 = view.findViewById(R.id.checkBoxStar5);

        setupStarCheckBoxes();

        searchButton.setOnClickListener(v -> showResults(view));

        return view;
    }

    private void showDatePickerDialog(boolean isArrival) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    if (isArrival) {
                        editTextArrivalDate.setText(dateFormat.format(calendar.getTime()));
                    } else {
                        editTextDepartureDate.setText(dateFormat.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        if (!isArrival && editTextArrivalDate.getText().length() > 0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Calendar arrivalCalendar = Calendar.getInstance();
                arrivalCalendar.setTime(sdf.parse(editTextArrivalDate.getText().toString()));
                datePickerDialog.getDatePicker().setMinDate(arrivalCalendar.getTimeInMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        datePickerDialog.show();
    }

    private void setupStarCheckBoxes() {
        checkBoxStar1.setOnCheckedChangeListener(starCheckedChangeListener);
        checkBoxStar2.setOnCheckedChangeListener(starCheckedChangeListener);
        checkBoxStar3.setOnCheckedChangeListener(starCheckedChangeListener);
        checkBoxStar4.setOnCheckedChangeListener(starCheckedChangeListener);
        checkBoxStar5.setOnCheckedChangeListener(starCheckedChangeListener);
    }

    private final CompoundButton.OnCheckedChangeListener starCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                checkBoxStar1.setChecked(checkBoxStar1 == buttonView);
                checkBoxStar2.setChecked(checkBoxStar2 == buttonView);
                checkBoxStar3.setChecked(checkBoxStar3 == buttonView);
                checkBoxStar4.setChecked(checkBoxStar4 == buttonView);
                checkBoxStar5.setChecked(checkBoxStar5 == buttonView);
            }
        }
    };

    private void showResults(View view) {
        criteriaContainer.setVisibility(View.GONE);

        resultsHeader.setVisibility(View.VISIBLE);
        recyclerView = view.findViewById(R.id.resultsList);
        recyclerView.setVisibility(View.VISIBLE);

        List<RoomDetails> roomDetailsList = new ArrayList<>();
        roomDetailsList.add(new RoomDetails("Paris", "France", 200, 4.5f, 150, R.drawable.room_picture));
        roomDetailsList.add(new RoomDetails("London", "UK", 250, 4.7f, 200, R.drawable.room_picture));
        roomDetailsList.add(new RoomDetails("Paris", "France", 200, 4.5f, 150, R.drawable.room_picture));
        roomDetailsList.add(new RoomDetails("London", "UK", 250, 4.7f, 200, R.drawable.room_picture));
        roomDetailsList.add(new RoomDetails("Paris", "France", 200, 4.5f, 150, R.drawable.room_picture));
        roomDetailsList.add(new RoomDetails("London", "UK", 250, 4.7f, 200, R.drawable.room_picture));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RoomDetailsAdapter adapter = new RoomDetailsAdapter(getContext(), roomDetailsList);
        recyclerView.setAdapter(adapter);
    }
}
