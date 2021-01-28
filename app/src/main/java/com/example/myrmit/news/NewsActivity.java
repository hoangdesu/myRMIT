package com.example.myrmit.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myrmit.R;
import com.example.myrmit.main.HomeFragment;
import com.example.myrmit.model.FirebaseHandler;
import com.example.myrmit.model.arrayAdapter.NewsAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class NewsActivity extends AppCompatActivity {

    private TextView aTitle;
    private TextView aAuthor;
    private ImageView aImage;
    private TextView aDescription;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private ImageView aLike;
    private final FirebaseHandler firebaseHandler = new FirebaseHandler();
    private Boolean like = true;
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    /**
     * On create function
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Setting stuffs
        aTitle =  (TextView) findViewById(R.id.title);
        aAuthor = (TextView) findViewById(R.id.author);
        aImage = (ImageView) findViewById(R.id.image);
        aDescription = (TextView) findViewById(R.id.description);
        aLike = (ImageView) findViewById(R.id.like_btn);

        //Getting data to populate view
        Intent intent = getIntent();
        String title = intent.getExtras().getString("Title");
        String author = intent.getExtras().getString("Author");
        String image = intent.getExtras().getString("Image");
        String description = intent.getExtras().getString("Description");
        like = intent.getExtras().getBoolean("Like");

        if (like) {
            aLike.setColorFilter(Color.parseColor("#FFE60028"));
        } else {
            aLike.setColorFilter(Color.parseColor("#FF000000"));
        }

        //Setting like icon on click event
        aLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    if (!like) {
                        firebaseHandler.updatePostLike(currentUser.getEmail(), title, true); //update like to database
                        aLike.setColorFilter(Color.parseColor("#FFE60028")); //Set like icon color
                        NewsAdapter.updateData(title, true); //update local data in NewsAdapter
                        HomeFragment.updateData(title, true); //update local data in home fragment
                        like = true;
                    } else {
                        firebaseHandler.updatePostLike(currentUser.getEmail(), title, false);
                        aLike.setColorFilter(Color.parseColor("#FF000000"));
                        NewsAdapter.updateData(title, false);
                        HomeFragment.updateData(title, false);
                        like = false;
                    }
                }
            }
        });

        populateView(title, author, description, image);
    }


    /**
     * Display news
     * @param title String
     * @param author String
     * @param description String
     * @param image String
     */
    private  void populateView(String title, String author, String description, String image) {
        aTitle.setText(title);
        aAuthor.setText(author);
        aDescription.setText(description);

        //Getting image from database
        StorageReference storageReference = storage.getReference().child(image);
        try {
            final File file = File.createTempFile("image","jpg");
            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    aImage.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NewsActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            });
        } catch  (IOException e){
            e.printStackTrace();
        }
    }

}