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
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    CardView visitorList;
    ListView listView;
    String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
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
        listView = view.findViewById(R.id.lecturer_list);
        visitorList = view.findViewById(R.id.booking_card);
        visitorList.setEnabled(false);
        firebaseHandler.getTutorList().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> list = (ArrayList<String>) task.getResult().get("list");
                if (list.contains(currentUser)){
                    visitorList.setEnabled(true);
                    visitorList.setVisibility(View.VISIBLE);
                    visitorList.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            firebaseHandler.getTutorList().collection("data").document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    String visitor = task.getResult().getString("isBook");
                                    String date = task.getResult().getString("date");
                                    if (!visitor.equals("") && sdf.format(Calendar.getInstance().getTime()).equals(date)){
                                        final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                                        View view = getLayoutInflater().inflate(R.layout.visitor_list_dialog, null);
                                        TextView username  = view.findViewById(R.id.textView48);
                                        TextView detail = view.findViewById(R.id.textView47);
                                        // Setup all the components of the dialog
                                        dialog.setView(view);
                                        final AlertDialog alert = dialog.create();
                                        firebaseHandler.getAccount(visitor).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                String name = task.getResult().getString("name");
                                                task.getResult().getReference().collection("programCode").document("program").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @SuppressLint("SetTextI18n")
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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
                                    else Toast.makeText(getActivity(), "You have no visitor!" , Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });
        firebaseHandler.getTutorList().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<TutorItem> items = new ArrayList<>();
                ArrayList<String> list = (ArrayList<String>) task.getResult().get("list");
                final boolean[] isBooked = {false};
                for (String account: list) {
                    if (!account.equals(currentUser)) {
                        task.getResult().getReference().collection("data").document(account).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                String name = task.getResult().getString("name");
                                String major = task.getResult().getString("major");
                                String phone = task.getResult().getString("phone");
                                String mail = task.getResult().getId();
                                String date = task.getResult().getString("date");
                                String role = task.getResult().getString("role");
                                String isBook = task.getResult().getString("isBook");
                                ArrayList<String> time = (ArrayList<String>) task.getResult().get("availableTime");
                                ArrayList<String> day = (ArrayList<String>) task.getResult().get("availableDay");
                                if (isBook.equals(currentUser)){
                                    isBooked[0] = true;
                                }
                                else if (!sdf.format(Calendar.getInstance().getTime()).equals(date)){
                                    isBook = "Available";
                                }
                                items.add(get(name, mail, major, phone, role, isBook, day, time));
                                ArrayAdapter<TutorItem> arrayAdapter = new ArrayAdapterTutor(getActivity(), items, isBooked[0]);
                                listView.setAdapter(arrayAdapter);
                            }
                        });
                    }
                }
            }
        });
        return view;
    }

    private TutorItem get(String name, String mail, String major, String phone, String role, String isBook, ArrayList<String> day, ArrayList<String> time){
        return new TutorItem(name, mail, major, phone, role, isBook, day, time);
    }
}