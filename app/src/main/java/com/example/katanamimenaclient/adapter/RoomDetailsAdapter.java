package com.example.katanamimenaclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.katanamimenaclient.R;
import com.example.katanamimenaclient.model.RoomDetails;

import java.util.List;

public class RoomDetailsAdapter extends RecyclerView.Adapter<RoomDetailsAdapter.ViewHolder> {

    private List<RoomDetails> roomDetailsList;
    private Context context;

    public RoomDetailsAdapter(Context context, List<RoomDetails> roomDetailsList) {
        this.context = context;
        this.roomDetailsList = roomDetailsList;
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
            Toast.makeText(context, "Book Now for " + roomDetails.getLocationName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return roomDetailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView placeImage;
        TextView locationName, countryName, price, rating, ratingCount;
        Button bookNowButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            placeImage = itemView.findViewById(R.id.image_location);
            locationName = itemView.findViewById(R.id.text_location_name);
            countryName = itemView.findViewById(R.id.text_country_name);
            price = itemView.findViewById(R.id.text_price);
            rating = itemView.findViewById(R.id.text_rating);
            ratingCount = itemView.findViewById(R.id.text_rating_count);
            bookNowButton = itemView.findViewById(R.id.button_book_now);
        }
    }
}