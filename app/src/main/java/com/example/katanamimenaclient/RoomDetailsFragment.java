package com.example.katanamimenaclient;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class RoomDetailsFragment extends Fragment {

    public RoomDetailsFragment() {
        // Required empty public constructor
    }

    public static RoomDetailsFragment newInstance() {
        return new RoomDetailsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_details, container, false);

        ImageView backButton = view.findViewById(R.id.back_button);

        return view;
    }


}
