package com.example.myrmit.clubs;
import com.bumptech.glide.Glide;
import com.example.myrmit.*;
import com.example.myrmit.model.FirebaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SearchView;
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
    List<String> clubLogos;
    RecyclerView clubsRecyclerView;
    ClubsRecyclerAdapter clubsRecyclerAdapter;
    SearchView svClubs;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubs);

        // Initiate list for array adapter
        clubList = new ArrayList<>();
        clubNames = new ArrayList<>();
        clubCategories = new ArrayList<>();
        clubCreatedDates = new ArrayList<>();
        clubLogos = new ArrayList<>();

        svClubs = (SearchView) findViewById(R.id.svClubs);

        // set up recycler view
        clubsRecyclerView = findViewById(R.id.clubsRecyclerView);
        clubsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        clubsRecyclerView.addItemDecoration(divider);

        // Retrieve data from Firebase
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

//                        String logo = clubName.substring(0, clubName.indexOf(" "));
//                        clubLogos.add(logo);
//                        Log.i("LOGO", logo);

                    }

                    // Adding club logos
                    clubLogos.add("https://firebasestorage.googleapis.com/v0/b/myrmit-c2020.appspot.com/o/Accounting.jpg?alt=media&token=ce9b197b-82b2-44cf-9ec9-966c10959054");
                    clubLogos.add("https://firebasestorage.googleapis.com/v0/b/myrmit-c2020.appspot.com/o/BIS.jpg?alt=media&token=76e5c960-96af-4fc5-bc23-e0c1464154df");
                    clubLogos.add("https://firebasestorage.googleapis.com/v0/b/myrmit-c2020.appspot.com/o/Badminton.jpg?alt=media&token=4d420a72-aad3-419d-a6bc-cd59263271b7");
                    clubLogos.add("https://firebasestorage.googleapis.com/v0/b/myrmit-c2020.appspot.com/o/Basketball.jpg?alt=media&token=2d0d39dc-8f9b-4c14-aba4-11e97c561ecf");
                    clubLogos.add("https://firebasestorage.googleapis.com/v0/b/myrmit-c2020.appspot.com/o/Business.jpg?alt=media&token=b62bd8f6-337a-4a88-a2bb-8223a3da1567");
                    clubLogos.add("https://firebasestorage.googleapis.com/v0/b/myrmit-c2020.appspot.com/o/Cheerleading.jpg?alt=media&token=7717a9be-c360-462a-8913-cb13acafec16");
                    clubLogos.add("https://firebasestorage.googleapis.com/v0/b/myrmit-c2020.appspot.com/o/Developer.jpg?alt=media&token=f24c6d2c-ce7d-4a78-a93f-457a5ac18654");
                    clubLogos.add("https://firebasestorage.googleapis.com/v0/b/myrmit-c2020.appspot.com/o/FinTech.jpg?alt=media&token=d678d1b4-30a7-4390-9c55-e2497afcbdb8");
                    clubLogos.add("https://firebasestorage.googleapis.com/v0/b/myrmit-c2020.appspot.com/o/Japanese.jpg?alt=media&token=114d0252-146f-4926-88c0-557d9258ed4b");
                    clubLogos.add("https://firebasestorage.googleapis.com/v0/b/myrmit-c2020.appspot.com/o/Mass.jpg?alt=media&token=33aa3f88-664d-41f2-bd3a-02958d199dc1");
                    clubLogos.add("https://firebasestorage.googleapis.com/v0/b/myrmit-c2020.appspot.com/o/Music.jpg?alt=media&token=41f947c9-38a9-4cb5-b257-c962438c5d34");
                    clubLogos.add("https://firebasestorage.googleapis.com/v0/b/myrmit-c2020.appspot.com/o/Neo.jpg?alt=media&token=81fb7603-ae0d-4a70-9d0a-fa7bcf44a97f");
                    clubLogos.add("https://firebasestorage.googleapis.com/v0/b/myrmit-c2020.appspot.com/o/Ultimate.jpg?alt=media&token=823fa5bb-255d-4feb-81c1-5342a4d3ea6d");
                    clubsRecyclerAdapter = new ClubsRecyclerAdapter(clubLogos, clubNames, clubCategories, clubCreatedDates);
                    clubsRecyclerView.setAdapter(clubsRecyclerAdapter);

                    // Toast.makeText(ClubsActivity.this, "Done fetching data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Filter club using search view
        svClubs.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                clubsRecyclerAdapter.getFilter().filter(s);
                return false;
            }
        });
    }
}