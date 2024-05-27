package com.example.katanamimenaclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import server.Message;
import server.MessageType;
import server.Room;

public class RatingFragment extends Fragment {

    private RatingBar ratingBar;
    private Context context;
    private float currentRating; // Variable to store the current rating

    private String roomName; // Variable to store the room name passed in as an argument

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rating, container, false);

        // Retrieve the room name from arguments
        if (getArguments() != null) {
            setRoomName(getArguments().getString("room_name"));
        }

        // Initialize the RatingBar
        ratingBar = view.findViewById(R.id.rating_stars);

        // Initialize the Button and set OnClickListener
        Button completeRatingButton = view.findViewById(R.id.complete_rating_button);
        completeRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentRating = ratingBar.getRating();
                int convertNumber = (int) currentRating;
                Thread networkThread = new Thread(() -> {
                    Socket dataSocket;
                    InputStream inputStream;
                    OutputStream os;
                    try {
                        dataSocket = new Socket("10.0.2.2", 5678);
                        inputStream = dataSocket.getInputStream();
                        os = dataSocket.getOutputStream();
                        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                        PrintWriter out = new PrintWriter(os, true);
                        Room room = new Room();
                        room.setName(roomName);
                        room.setStars(convertNumber);
                        Message message = new Message();
                        message.setType(MessageType.RATE_ROOM.getValue());
                        Gson gson = new Gson();
                        message.setRoom(room);
                        out.println(gson.toJson(message));
                        String reply = in.readLine();
                        Message messageReply = gson.fromJson(reply, Message.class);
                        System.out.println("message!");

                        // Update UI on the main thread
                        getActivity().runOnUiThread(() -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Completed rate");
                            builder.setMessage(messageReply.getContent());
                            builder.setPositiveButton("OK", (dialog, which) -> {
                                dialog.dismiss();
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        });

                    } catch (IOException e) {
                        e.printStackTrace(); // Handle exceptions appropriately
                    }
                });
                networkThread.start();
            }
        });
        return view;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
