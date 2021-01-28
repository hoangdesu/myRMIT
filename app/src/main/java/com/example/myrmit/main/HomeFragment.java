package com.example.myrmit.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myrmit.ClickableViewPager;
import com.example.myrmit.news.News;
import com.example.myrmit.news.NewsActivity;
import com.example.myrmit.R;
import com.example.myrmit.model.FirebaseHandler;
import com.example.myrmit.model.arrayAdapter.SwipeCardAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    ClickableViewPager viewPager;
    SwipeCardAdapter swipeCardAdapter;
    static List<News> newsList = new ArrayList<News>();
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    TextView tvHelloUser;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseHandler.getEvents().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    if (currentUser != null && likes.contains(currentUser.getEmail())) {
                        isLike = true;
                    }
                    newsList.add(new News(thumbnail,title,description,"RMIT",isLike));
                }

                swipeCardAdapter = new SwipeCardAdapter(newsList, getContext());
                viewPager = view.findViewById(R.id.viewPager);
                viewPager.setAdapter(swipeCardAdapter);
                viewPager.setOnItemClickListener(new ClickableViewPager.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getContext(), NewsActivity.class);
                        intent.putExtra("Title",newsList.get(position).getTitle());
                        intent.putExtra("Author",newsList.get(position).getAuthor());
                        intent.putExtra("Image", newsList.get(position).getThumbnail());
                        intent.putExtra("Description", newsList.get(position).getDescription());
                        intent.putExtra("Like", newsList.get(position).isLiked());
                        startActivity(intent);
                    }
                });
            }
        });

        /* Set welcoming message to corresponding user */
        tvHelloUser = view.findViewById(R.id.tvHelloUser);
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            DocumentReference userRef = firebaseHandler.getAccount(userEmail);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot user) {
                    String name = user.getString("name");
                    String message = "Hello, " + name + "!";
                    tvHelloUser.setText(message);
                }
            });
        }
        else tvHelloUser.setText("Hello, My Guest!");

        return view;
    }

    //update local data to display like
    public static void updateData(String title, boolean like) {
        for (News news : newsList) {
            if (news.getTitle().equals(title)) {
                news.setLike(like);
                break;
            }
        }
    }
}