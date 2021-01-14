package com.example.myrmit;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private List<News> newsList;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        newsList = new ArrayList<>();
        newsList.add(new News(R.drawable.temp_news_image, "RMIT Post Graduation Day", "If you’re thinking about starting your master’s journey in 2021, don’t miss this opportunity to join our Master’s workshops so that you can experience RMIT for yourself and discover how you can transform your perspectives, transform your possibilities and transform yourself at RMIT!","RMIT"));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        newsAdapter = new NewsAdapter(this, newsList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(newsAdapter);
    }

    public void onLikeClick(View view){
        ImageView likeButton = (ImageView) findViewById(R.id.like_btn);
        likeButton.setColorFilter(Color.parseColor("#FFE60028"));
    }
}