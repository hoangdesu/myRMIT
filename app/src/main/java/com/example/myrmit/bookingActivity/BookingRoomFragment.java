package com.example.myrmit.bookingActivity;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.myrmit.ClickableViewPager;
import com.example.myrmit.Facility;
import com.example.myrmit.MapsActivity;
import com.example.myrmit.R;
import com.example.myrmit.model.FirebaseHandler;
import com.example.myrmit.model.arrayAdapter.RoomCardAdapter;
import com.example.myrmit.model.arrayAdapter.SwipeCardAdapter;
import com.example.myrmit.news.News;
import com.example.myrmit.news.NewsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookingRoomFragment extends Fragment {
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    ClickableViewPager viewPager;
    RoomCardAdapter roomCardAdapter;
    List<Room> rooms = new ArrayList<Room>();

    public BookingRoomFragment() {
    }

    /**
     * On create function
     *
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * On Create View
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.booking_room_fragment, container, false);

        firebaseHandler.getRooms().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String name, image;
                boolean available = false;
                double rating;
                int capacity;
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    name = Objects.requireNonNull(documentSnapshot.getData().get("id")).toString();
                    image = Objects.requireNonNull(documentSnapshot.getData().get("image")).toString();
                    available = Objects.requireNonNull(documentSnapshot.getBoolean("available"));
                    rating = Objects.requireNonNull(documentSnapshot.getDouble("rating"));
                    capacity = Objects.requireNonNull(documentSnapshot.getDouble("capacity").intValue());
                    rooms.add(new Room(capacity,name,available,image,rating));
                }

                roomCardAdapter = new RoomCardAdapter(rooms, getContext());
                viewPager = view.findViewById(R.id.viewPager);
                viewPager.setAdapter(roomCardAdapter);
                viewPager.setOnItemClickListener(new ClickableViewPager.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        
                    }
                });
            }
        });

        return view;
    }
}