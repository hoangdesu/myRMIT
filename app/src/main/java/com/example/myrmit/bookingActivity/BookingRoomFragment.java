package com.example.myrmit.bookingActivity;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.myrmit.ClickableViewPager;
import com.example.myrmit.R;
import com.example.myrmit.model.FirebaseHandler;
import com.example.myrmit.model.arrayAdapter.RoomCardAdapter;
import com.example.myrmit.model.objects.Room;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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
    Button cancelButton;
    TextView roomId;
    LinearLayout roomLayout;

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
                String name, image, bookedBy, bookedAt;
                boolean available = false;
                double rating;
                int capacity;
                // Get data of each rooms
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    name = Objects.requireNonNull(documentSnapshot.getData().get("id")).toString();
                    image = Objects.requireNonNull(documentSnapshot.getData().get("image")).toString();
                    available = Objects.requireNonNull(documentSnapshot.getBoolean("available"));
                    rating = Objects.requireNonNull(documentSnapshot.getDouble("rating"));
                    capacity = Objects.requireNonNull(documentSnapshot.getDouble("capacity").intValue());
                    bookedBy = Objects.requireNonNull(documentSnapshot.getData().get("bookedBy")).toString();
                    bookedAt = Objects.requireNonNull(documentSnapshot.getData().get("bookedAt")).toString();
                    rooms.add(new Room(capacity,name,available,image,rating));
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        if (bookedBy.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                            ((LinearLayout) view.findViewById(R.id.book_room_layout)).setVisibility(View.VISIBLE);
                            ((TextView) view.findViewById(R.id.room_id_)).setText(name);
                            ((TextView) view.findViewById(R.id.time)).setText(bookedAt);
                        }
                    }
                }

                // Store to the card view
                roomId = view.findViewById(R.id.room_id_);
                cancelButton = view.findViewById(R.id.cancel_button);
                roomLayout = view.findViewById(R.id.book_room_layout);

                // Set on cancel click
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Update the room's status
                        firebaseHandler.updateRoomAvailability(true, roomId.getText().toString(), "", "");
                        List<Room>  tempList= roomCardAdapter.getRoomList();
                        for (int i = 0; i < tempList.size(); i++) {
                            if (roomId.getText().toString().equals(tempList.get(i).getName())) {
                                roomCardAdapter.updateAvailability(true, i);
                                System.out.println("asdf");
                            }
                        }
                        roomLayout.setVisibility(View.INVISIBLE);
                    }
                });
                setRoomCard(view);
            }
        });

        return view;
    }

    /**
     * Set room for card view
     * @param view View
     */
    private void setRoomCard(View view){
        roomCardAdapter = new RoomCardAdapter(rooms, getContext());
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(roomCardAdapter);

        // Set on room click, this is for booking this room
        viewPager.setOnItemClickListener(new ClickableViewPager.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.book_room_dialog, null);
                Button noBtn = dialogView.findViewById(R.id.no_button);
                Button yesBtn = dialogView.findViewById(R.id.yes_button);
                TextView roomID = dialogView.findViewById(R.id.room_id);
                Spinner spinner = dialogView.findViewById(R.id.spinner);

                // Get time and update
                String[] items = new String[]{"8:00","9:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
                spinner.setAdapter(adapter);

                roomID.setText(rooms.get(position).getName());
                setDialogForBooking(dialogView, noBtn, yesBtn, view, spinner, position);
            }
        });
    }

    /**
     * set Dialog for user to choose time and book the selected room
     * @param dialogView View
     * @param noBtn Button
     * @param yesBtn Button
     * @param view View
     * @param spinner Spinner
     * @param position int
     */
    private void setDialogForBooking(View dialogView, Button noBtn, Button yesBtn, View view, Spinner spinner, int position){
        // Setup the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    if (roomCardAdapter.getRoomList().get(position).isAvailable()) {
                        // Check if the room is available
                        if (roomLayout.getVisibility() != View.VISIBLE) {
                            String time = spinner.getSelectedItem().toString();
                            String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            // Then book the room if this is available one
                            firebaseHandler.getRoomData(roomCardAdapter.getRoomList().get(position).getName()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        boolean available = task.getResult().getBoolean("available");
                                        if (available) {
                                            firebaseHandler.updateRoomAvailability(false, roomCardAdapter.getRoomList().get(position).getName(),time,user);
                                            roomLayout.setVisibility(View.VISIBLE);
                                            ((TextView) view.findViewById(R.id.room_id_)).setText(roomCardAdapter.getRoomList().get(position).getName());
                                            ((TextView) view.findViewById(R.id.time)).setText(time);
                                        }
                                        else Toast.makeText(getContext(), "Room Not Available", Toast.LENGTH_SHORT).show();
                                        //refresh the card
                                        roomCardAdapter.updateAvailability(false, position);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "You Already Booked A Room", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Room Not Available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Room cannot be booked by guest!", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}