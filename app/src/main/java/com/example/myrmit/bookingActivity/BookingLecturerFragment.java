package com.example.myrmit.bookingActivity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.myrmit.R;
import com.example.myrmit.model.FirebaseHandler;
import com.example.myrmit.model.arrayAdapter.ArrayAdapterTutor;
import com.example.myrmit.model.objects.TutorItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookingLecturerFragment extends Fragment {
    private final FirebaseHandler firebaseHandler = new FirebaseHandler();
    private CardView visitorList;
    private ListView listView;
    private final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
    public BookingLecturerFragment() {
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
        View view = inflater.inflate(R.layout.booking_lecturer_fragment, container, false);
        // Initial setting
        listView = view.findViewById(R.id.lecturer_list);
        visitorList = view.findViewById(R.id.booking_card);
        visitorList.setEnabled(false);

        // Set up for lecturer/tutor to view the today's visitor
        setViewVisitors();

        // Set list for booking
        setList();

        return view;
    }

    /**
     * Set View visitor if you are a tutor or lecturer
     */
    private void setViewVisitors(){
        firebaseHandler.getTutorList().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> list = (ArrayList<String>) task.getResult().get("list");
                if (list.contains(currentUser)){
                    // Enable the button which is used to view visitor
                    visitorList.setEnabled(true);
                    visitorList.setVisibility(View.VISIBLE);

                    // Set on click for that button
                    visitorList.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get visitor's information
                            firebaseHandler.getTutorList().collection("data").document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    // Store data
                                    String visitor = task.getResult().getString("isBook");
                                    String date = task.getResult().getString("date");
                                    // Check if there is a visitor
                                    if (!visitor.equals("") && sdf.format(Calendar.getInstance().getTime()).equals(date)){
                                        showVisitor(visitor, v);           // Show visitor if yes
                                    }
                                    else Toast.makeText(getActivity(), "You have no visitor!" , Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    /**
     * Show visitor through the dialog
     * @param visitor String
     */
    private void showVisitor(String visitor, View v){
        // Set up the dialog
        final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
        View view = getLayoutInflater().inflate(R.layout.visitor_list_dialog, null);
        TextView username  = view.findViewById(R.id.textView48);
        TextView detail = view.findViewById(R.id.textView47);
        // Setup all the components of the dialog
        dialog.setView(view);
        final AlertDialog alert = dialog.create();
        // Get specific information of the visitor from firebase
        firebaseHandler.getAccount(visitor).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Store data
                String name = task.getResult().getString("name");
                task.getResult().getReference().collection("programCode").document("program").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        // Show data
                        String program = task.getResult().getString("code");
                        username.setText(visitor);
                        detail.setText("Name: " + name +" --- Program ID: " + program );
                    }
                });
            }
        });
        // Show the alert dialog
        alert.show();
    }

    /**
     * Set list for booking
     * The list will show all the tutor/lecture
     */
    private void setList(){
        firebaseHandler.getTutorList().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Get the list from firebase
                ArrayList<TutorItem> items = new ArrayList<>();
                ArrayList<String> list = (ArrayList<String>) task.getResult().get("list");
                final boolean[] isBooked = {false};
                for (String account: list) {
                    if (!account.equals(currentUser)) {             // To avoid booking yourself -> not show your self in here
                        task.getResult().getReference().collection("data").document(account).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                // Get the data of each tutor/lecture
                                String name = task.getResult().getString("name");
                                String major = task.getResult().getString("major");
                                String phone = task.getResult().getString("phone");
                                String mail = task.getResult().getId();
                                String date = task.getResult().getString("date");
                                String role = task.getResult().getString("role");
                                String isBook = task.getResult().getString("isBook");
                                ArrayList<String> time = (ArrayList<String>) task.getResult().get("availableTime");
                                ArrayList<String> day = (ArrayList<String>) task.getResult().get("availableDay");
                                // Check if it is booked or not
                                if (isBook.equals(currentUser)){
                                    isBooked[0] = true;
                                }
                                else if (!sdf.format(Calendar.getInstance().getTime()).equals(date)){
                                    isBook = "Available";
                                }
                                // Store item
                                items.add(get(name, mail, major, phone, role, isBook, day, time));
                                // show to the list
                                ArrayAdapter<TutorItem> arrayAdapter = new ArrayAdapterTutor(getActivity(), items, isBooked[0]);
                                listView.setAdapter(arrayAdapter);
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Get Tutor's information based on given data
     * @param name String
     * @param mail String
     * @param major String
     * @param phone String
     * @param role String
     * @param isBook String
     * @param day ArrayList<String>
     * @param time ArrayList<String>
     * @return TutorItem
     */
    private TutorItem get(String name, String mail, String major, String phone, String role, String isBook, ArrayList<String> day, ArrayList<String> time){
        return new TutorItem(name, mail, major, phone, role, isBook, day, time);
    }
}