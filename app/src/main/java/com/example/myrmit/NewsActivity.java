package com.example.myrmit;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrmit.model.FirebaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private List<News> newsList;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    FirebaseHandler firebaseHandler = new FirebaseHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        newsList = new ArrayList<>();

        firebaseHandler.getNews().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> titles = (ArrayList<String>) task.getResult().get("title");
                ArrayList<String> descriptions = (ArrayList<String>) task.getResult().get("description");
                ArrayList<String> thumbnails = (ArrayList<String>) task.getResult().get("thumbnail");

                for (int i = 0; i < titles.size(); i++) {
                    newsList.add(new News(thumbnails.get(i),titles.get(i),descriptions.get(i),"RMIT"));
                }

                System.out.println("NewsList size: " + newsList.size());
                recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                newsAdapter = new NewsAdapter(NewsActivity.this, newsList);
                recyclerView.setLayoutManager(new GridLayoutManager(NewsActivity.this, 1));
                recyclerView.setAdapter(newsAdapter);
            }
        });
    }

}