package com.example.katanamimenaclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.example.katanamimenaclient.R;
import com.example.katanamimenaclient.RatingFragment;
import com.example.katanamimenaclient.model.RoomDetails;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import server.Message;
import server.MessageType;
import server.Room;

public class RoomDetailsAdapter extends RecyclerView.Adapter<RoomDetailsAdapter.ViewHolder> {

    private List<RoomDetails> roomDetailsList;
    private Context context;
    private CriteriaFragmentVisibilityHandler visibilityHandler;

    private Set<String> finalDates = new HashSet<String>();

    Message messageReply;

    public RoomDetailsAdapter(Context context, List<RoomDetails> roomDetailsList, CriteriaFragmentVisibilityHandler visibilityHandler) {
        this.context = context;
        this.roomDetailsList = roomDetailsList;
        this.visibilityHandler = visibilityHandler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_room_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomDetails roomDetails = roomDetailsList.get(position);
        holder.locationName.setText(roomDetails.getLocationName());
        holder.countryName.setText(roomDetails.getCountryName());
        holder.price.setText(String.valueOf(roomDetails.getPrice()));
        holder.rating.setText(String.valueOf(roomDetails.getRating()));
        holder.ratingCount.setText(String.format("(%d)", roomDetails.getRatingCount()));
        holder.placeImage.setImageResource(roomDetails.getImageResourceId());

        holder.bookNowButton.setOnClickListener(v -> {
            showRoomDetailsAlert(roomDetails);
        });

        holder.backButton.setOnClickListener(v -> {
            if (visibilityHandler != null) {
                visibilityHandler.showCriteriaFragment();
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomDetailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView placeImage;
        TextView locationName, countryName, price, rating, ratingCount, placeName;
        Button bookNowButton;
        ImageView backButton;
        Date startDate, endDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            placeImage = itemView.findViewById(R.id.image_location);
            locationName = itemView.findViewById(R.id.text_location_name);
            countryName = itemView.findViewById(R.id.text_country_name);
            price = itemView.findViewById(R.id.text_price);
            rating = itemView.findViewById(R.id.text_rating);
            ratingCount = itemView.findViewById(R.id.text_rating_count);
            bookNowButton = itemView.findViewById(R.id.button_book_now);
            backButton = itemView.findViewById(R.id.back_button);
        }
    }

    private void showRoomDetailsAlert(RoomDetails roomDetails) {
        Room room = new Room();
        room.setName(roomDetails.getName());
        finalDates = roomDetails.getRequestdDates();
        for (String s : finalDates) {
            room.addRequestedDate(s);
        }

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

                Message message = new Message();
                message.setType(MessageType.BOOK_ROOM.getValue());
                Gson gson = new Gson();
                message.setRoom(room);
                out.println(gson.toJson(message));
                String reply = in.readLine();
                messageReply = gson.fromJson(reply, Message.class);

                // Update UI on the main thread
                if (context instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) context;
                    activity.runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Room Details");
                        builder.setMessage(messageReply.getContent());
                        builder.setPositiveButton("OK", (dialog, which) -> {
                            navigateToRatingFragment(activity, roomDetails.getName());
                        });
                        builder.show();
                    });
                } else {
                    Log.e("ContextError", "Context used is not an instance of AppCompatActivity");
                }

            } catch (IOException e) {
                e.printStackTrace(); // Handle exceptions appropriately
            }
        });
        networkThread.start();
    }


    private void navigateToRatingFragment(Context context, String roomName) {
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            RatingFragment ratingFragment = new RatingFragment();
            Bundle args = new Bundle();
            args.putString("room_name", roomName);
            ratingFragment.setArguments(args);

            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, ratingFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            // Handle error or throw an exception if the context is not what you expect
            Log.e("NavigationError", "The provided context is not an AppCompatActivity.");
        }
    }


    public interface CriteriaFragmentVisibilityHandler {
        void showCriteriaFragment();
    }
}
