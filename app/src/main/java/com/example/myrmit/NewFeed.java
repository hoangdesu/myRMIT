package com.example.myrmit;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrmit.model.FirebaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewFeed extends AppCompatActivity {

    private List<News> newsList;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private SearchView searchView;
    private int filterOptions = 2;
    private boolean updateFlag = false;
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    private String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        newsList = new ArrayList<>();

        searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (filterOptions == 1) {
                    newsAdapter.getFilter().filter(newText + "/liked");
                } else {
                    newsAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        firebaseHandler.getNews().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String title, description, thumbnail;
                boolean isLike = false;
                List<String> likes = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    title = Objects.requireNonNull(documentSnapshot.getData().get("title")).toString();
                    description = Objects.requireNonNull(documentSnapshot.getData().get("description")).toString().replace("\\n","\n\n");
                    thumbnail = Objects.requireNonNull(documentSnapshot.getData().get("thumbnail")).toString();
                    likes = (ArrayList<String>) documentSnapshot.get("likes");
                    if (currentUser != null && likes.contains(currentUser)) {
                        isLike = true;
                    }
                    System.out.println(likes.toString());
                    System.out.println("like: " + isLike);
                    newsList.add(new News(thumbnail,title,description,"RMIT",isLike));
                }

                System.out.println("NewsList size: " + newsList.size());
                recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                newsAdapter = new NewsAdapter(NewFeed.this, newsList);
                recyclerView.setLayoutManager(new GridLayoutManager(NewFeed.this, 1));
                recyclerView.setAdapter(newsAdapter);
                updateFlag = true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (updateFlag) {
            newsAdapter.notifyDataSetChanged();
        }
    }

    public void filter(View view) {
        View dialogView = LayoutInflater.from(NewFeed.this).inflate(R.layout.filter_dialog,null);
        RadioButton allBtn = ((RadioButton) dialogView.findViewById(R.id.all_btn));
        RadioButton likedBtn = ((RadioButton) dialogView.findViewById(R.id.like_btn));

        if (filterOptions == 2) {
            allBtn.setChecked(true);
        } else {
            likedBtn.setChecked(true);
        }

        Button applyBtn = dialogView.findViewById(R.id.apply_button);

        AlertDialog.Builder builder = new AlertDialog.Builder(NewFeed.this,R.style.AlertDialogTheme);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likedBtn.isChecked() && filterOptions == 2) {
                    filterOptions = 1;
                    newsAdapter.getFilter().filter("/liked");
                } else if (allBtn.isChecked() && filterOptions == 1) {
                    newsAdapter.getFilter().filter("");
                    filterOptions = 2;
                }
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

}