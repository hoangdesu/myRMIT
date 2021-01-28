package com.example.myrmit.clubs;
import com.example.myrmit.*;
import com.example.myrmit.model.FirebaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClubsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    Clubs club;
    List<Clubs> clubList;
    List<String> clubNames;
    List<String> clubCategories;
    List<String> clubCreatedDates;
    RecyclerView clubsRecyclerView;
    ClubsRecyclerAdapter clubsRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubs);

        clubList = new ArrayList<>();
        clubNames = new ArrayList<>();
        clubCategories = new ArrayList<>();
        clubCreatedDates = new ArrayList<>();

        clubsRecyclerView = findViewById(R.id.clubsRecyclerView);
        clubsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        clubsRecyclerView.addItemDecoration(divider);



        firebaseHandler.getAllClubs().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String clubName = document.getString("name");
                        Double date = document.getDouble("createdDate");
                        String category = document.getString("category");
                        String createdDate = String.valueOf(date);
                        createdDate = createdDate.substring(0, 4);
                        club = new Clubs(clubName, category, createdDate);
                        clubList.add(club);
                        //Log.d("clubs", club.getCreatedDate());

                        clubNames.add(clubName);
                        clubCategories.add(category);
                        clubCreatedDates.add(createdDate);

                    }
                    clubsRecyclerAdapter = new ClubsRecyclerAdapter(clubNames, clubCategories, clubCreatedDates);
                    clubsRecyclerView.setAdapter(clubsRecyclerAdapter);

                    // Toast.makeText(ClubsActivity.this, "Done fetching data", Toast.LENGTH_SHORT).show();
                }
            }
        });








    }
}