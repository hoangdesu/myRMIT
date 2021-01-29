package com.example.myrmit.clubs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.myrmit.model.FirebaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myrmit.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ClubInfoActivity extends AppCompatActivity {

    private static final String TAG = "INFO";
    ImageView ivClubLogo;
    TextView tvClubName, tvClubDescription, tvClubLocation, tvClubInfoCategory, tvClubInfoCreatedDate;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String description, location, email;
    Button btnJoinClub;
    boolean joinedClub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_info);

        String logo = getIntent().getStringExtra("logo");
        String name = getIntent().getStringExtra("name");
        String category = getIntent().getStringExtra("category");
        String createdDate = getIntent().getStringExtra("createdDate");

        ivClubLogo = findViewById(R.id.ivClubLogo);
        tvClubName = findViewById(R.id.tvClubName);
        tvClubDescription = findViewById(R.id.tvClubDescription);
        tvClubLocation = findViewById(R.id.tvClubLocation);
        btnJoinClub = findViewById(R.id.btnJoinClub);
        tvClubInfoCategory = findViewById(R.id.tvClubInfoCategory);
        tvClubInfoCreatedDate = findViewById(R.id.tvClubInfoCreatedDate);

        //joinedClubs = new ArrayList<>();

        DocumentReference clubsRef = db.collection("clubs").document(name);

        clubsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        description = document.getString("description");
                        location = document.getString("location");
                        email = document.getString("email");
                        joinedClub = document.getBoolean("joined");
//
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

                if (joinedClub) {
                    btnJoinClub.setBackgroundColor(Color.RED);
                    btnJoinClub.setText("Joined!");
                } else {
                    btnJoinClub.setBackgroundColor(Color.BLUE);
                    btnJoinClub.setText("Join now");
                }

                tvClubDescription.setText(description);
                tvClubLocation.setText(location);
                tvClubInfoCategory.setText(category);
                tvClubInfoCreatedDate.setText(createdDate);

            }
        });

        //Log.i("INFO", description);


        Glide.with(this).load(logo).into(ivClubLogo);
        tvClubName.setText(name);

        // Send email to host
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                //Toast.makeText(ClubInfoActivity.this, description, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, email);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Joining club");
                startActivity(Intent.createChooser(intent, "Send email to " + email));

            }
        });

        LinearLayout layout = findViewById(R.id.clubLinearLayout);

        btnJoinClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinedClub = !joinedClub;
                if (joinedClub) {
                    btnJoinClub.setBackgroundColor(Color.RED);
                    btnJoinClub.setText("Joined!");
                    clubsRef.update("joined", true);
                    Snackbar snackbar = Snackbar
                            .make(layout, "Welcome to our club! xD", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    btnJoinClub.setBackgroundColor(Color.BLUE);
                    btnJoinClub.setText("Join now");
                    clubsRef.update("joined", false);
                    Snackbar snackbar = Snackbar
                            .make(layout, "We're sad that you're leaving :(", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });


    }
}