package com.example.myrmit.clubs;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myrmit.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClubInfoActivity extends AppCompatActivity {

    private static final String TAG = "INFO";
    ImageView ivClubLogo;
    TextView tvClubName, tvClubDescription, tvClubLocation;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String description, location, email;

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

        DocumentReference clubsRef = db.collection("clubs").document(name);

        clubsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        description = document.getString("description");
                        location = document.getString("location");
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

                tvClubDescription.setText(description);
                tvClubLocation.setText(location);

            }
        });

        //Log.i("INFO", description);



        Glide.with(this).load(logo).into(ivClubLogo);
        tvClubName.setText(name);



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                //Toast.makeText(ClubInfoActivity.this, description, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(Intent.ACTION_SEND);
                startActivity(Intent.createChooser(i, "Send mail..."));

            }
        });
    }
}