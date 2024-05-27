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
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import server.Message;
import server.MessageType;
import server.Room;

public class CriteriaFragment extends Fragment implements RoomDetailsAdapter.CriteriaFragmentVisibilityHandler {

    private int adultsCount = 1;
    private TextView textAdultsCount;
    private EditText editTextArrivalDate, editTextDepartureDate, editTextPrice, editTextLocation;
    private CheckBox checkBoxStar1, checkBoxStar2, checkBoxStar3, checkBoxStar4, checkBoxStar5;
    private RecyclerView recyclerView;
    private ScrollView criteriaContainer;
    private TextView resultsHeader;
    private Button searchButton;

    private Button homePage;

    private Set<String> requestedDates;

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
        recyclerView = view.findViewById(R.id.resultsList);
        homePage = view.findViewById(R.id.homePage);
        editTextArrivalDate = view.findViewById(R.id.arrivalInput);
        editTextDepartureDate = view.findViewById(R.id.departureInput);
        editTextLocation = view.findViewById(R.id.locationInput);
        editTextPrice = view.findViewById(R.id.priceInput);

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

        homePage.setOnClickListener(v -> {
            criteriaContainer.setVisibility(View.VISIBLE);
            homePage.setVisibility(View.GONE);
            resultsHeader.setVisibility(View.GONE);
        });

        searchButton.setOnClickListener(v -> {
            Thread networkThread = new Thread(() -> {
                System.out.println("Connecting to server...");
                Socket dataSocket;
                InputStream inputStream;
                OutputStream os;
                try {
                    dataSocket = new Socket("10.0.2.2", 5678);
                    inputStream = dataSocket.getInputStream();
                    os = dataSocket.getOutputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter out = new PrintWriter(os, true);
                System.out.println("Connection to server established.");

                Message message = new Message();
                message.setType(MessageType.SEARCH.getValue());
                Room room = new Room();
                room.setPrice(Double.valueOf(editTextPrice.getText().toString()));
                room.setArea(editTextLocation.getText().toString());
                room.setNoOfPeople(adultsCount);
                room.setUserRating(0);
                if (checkBoxStar1.isChecked()) {
                    room.setUserRating(1);
                }
                if (checkBoxStar2.isChecked()) {
                    room.setUserRating(2);
                }
                if (checkBoxStar3.isChecked()) {
                    room.setUserRating(3);
                }
                if (checkBoxStar4.isChecked()) {
                    room.setUserRating(4);
                }
                if (checkBoxStar5.isChecked()) {
                    room.setUserRating(5);
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date startDate = null;
                Date endDate = null;
                try {
                    startDate = sdf.parse(editTextArrivalDate.getText().toString());
                    endDate = sdf.parse(editTextDepartureDate.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                if (startDate != null && endDate != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDate);
                    while (!calendar.getTime().after(endDate)) {
                        String formattedDate = sdf.format(calendar.getTime());
                        room.addRequestedDate(formattedDate);
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                }

                Gson gson = new Gson();
                message.setRoom(room);
                out.println(gson.toJson(message));
                String reply;

                try {
                    reply = in.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Message messageReply = gson.fromJson(reply, Message.class);
                System.out.println("Received from server: " + reply);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        ArrayList<Room> rooms = (ArrayList<Room>) messageReply.getResults();
                        if(rooms.isEmpty()){
                            homePage.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            homePage.setVisibility(View.GONE);
                        }
                        criteriaContainer.setVisibility(View.GONE);
                        resultsHeader.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);

                        List<RoomDetails> roomDetailsList = new ArrayList<>();
                        for (Room roomDetail : rooms) {
                            RoomDetails details = new RoomDetails(
                                    roomDetail.getArea(),
                                    roomDetail.getArea(),
                                    roomDetail.getPrice(),
                                    roomDetail.getStars(),
                                    roomDetail.getNoOfReviews(),
                                    R.drawable.room_picture,
                                    room.getRequestedDates(),
                                    roomDetail.getName()

                            );
                            roomDetailsList.add(details);
                        }
                        RoomDetailsAdapter adapter = new RoomDetailsAdapter(getContext(), roomDetailsList, this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adapter);
                    });
                }
            });
            networkThread.start();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Make the criteriaContainer visible and hide the resultsHeader and resultsList when returning to this fragment
        criteriaContainer.setVisibility(View.VISIBLE);
        resultsHeader.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showCriteriaFragment() {
        showCriteriaAndHideResults();
    }

    public void showCriteriaAndHideResults() {
        criteriaContainer.setVisibility(View.VISIBLE);
        resultsHeader.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
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
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            if (!isArrival && editTextArrivalDate.getText().length() > 0) {
                Calendar arrivalCalendar = Calendar.getInstance();
                arrivalCalendar.setTime(sdf.parse(editTextArrivalDate.getText().toString()));
                datePickerDialog.getDatePicker().setMinDate(arrivalCalendar.getTimeInMillis());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        datePickerDialog.show();
    }

    private void setupStarCheckBoxes() {
        CompoundButton.OnCheckedChangeListener starCheckedChangeListener = (buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxStar1.setChecked(checkBoxStar1 == buttonView);
                checkBoxStar2.setChecked(checkBoxStar2 == buttonView);
                checkBoxStar3.setChecked(checkBoxStar3 == buttonView);
                checkBoxStar4.setChecked(checkBoxStar4 == buttonView);
                checkBoxStar5.setChecked(checkBoxStar5 == buttonView);
            }
        };
        checkBoxStar1.setOnCheckedChangeListener(starCheckedChangeListener);
        checkBoxStar2.setOnCheckedChangeListener(starCheckedChangeListener);
        checkBoxStar3.setOnCheckedChangeListener(starCheckedChangeListener);
        checkBoxStar4.setOnCheckedChangeListener(starCheckedChangeListener);
        checkBoxStar5.setOnCheckedChangeListener(starCheckedChangeListener);
    }
}
