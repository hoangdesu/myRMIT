package com.example.myrmit.model.arrayAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrmit.news.News;
import com.example.myrmit.news.NewsActivity;
import com.example.myrmit.R;
import com.example.myrmit.main.HomeFragment;
import com.example.myrmit.model.FirebaseHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyHolder> implements Filterable {
    private static List<News> newsList;
    private static List<News> newsListAll;
    private final Context context;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseHandler firebaseHandler = new FirebaseHandler();
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public NewsAdapter(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
        this.newsListAll = new ArrayList<>(newsList);
    }

    /**
     * Update behavior of the news
     * @param title String
     * @param like boolean
     */
    public static void updateData(String title, boolean like) {
        if (newsListAll != null) {
            for (News news : newsListAll) {
                if (news.getTitle().equals(title)) {
                    news.setLike(like);
                }
            }
        }
    }

    /**
     * on create function
     * @param viewGroup ViewGroup
     * @param viewType int
     * @return  MyHolder
     */
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.news_item, viewGroup, false);
        return new MyHolder(view);
    }

    /**
     * Set binding for view holder
     * @param holder MyHolder
     * @param position int
     */
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //Set title, thumnail for the main activity
        holder.newsTitle.setText(newsList.get(position).getTitle());
        holder.newsDescription.setText(newsList.get(position).getDescription());
        // Check the behavior of the post
        if (newsList.get(position).isLiked()) {
            System.out.println("YESS");
            holder.likeIcon.setColorFilter(Color.parseColor("#FFE60028"));
        } else {
            holder.likeIcon.setColorFilter(Color.parseColor("#FF000000"));
        }
        // Get data
        StorageReference storageReference = storage.getReference().child(newsList.get(position).getThumbnail());
        try {
            // Set the received data to the view
            final File file = File.createTempFile("image","jpg");
            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    holder.newsThumbnail.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            });
        } catch  (IOException e){
            e.printStackTrace();
        }


        // Set onclick for adapter
        holder.likeIcon.setOnClickListener(new View.OnClickListener() {         // Like behavior
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    if (!newsList.get(position).isLiked()) {
                        firebaseHandler.updatePostLike(currentUser.getEmail(), holder.newsTitle.getText().toString(), true);
                        holder.likeIcon.setColorFilter(Color.parseColor("#FFE60028"));
                        newsList.get(position).setLike(true);
                        HomeFragment.updateData(holder.newsTitle.getText().toString(), true);
                    } else {
                        firebaseHandler.updatePostLike(currentUser.getEmail(), holder.newsTitle.getText().toString(), false);
                        holder.likeIcon.setColorFilter(Color.parseColor("#FF000000"));
                        newsList.get(position).setLike(false);
                        HomeFragment.updateData(holder.newsTitle.getText().toString(), false);
                    }
                }
            }
        });

        // To read full news
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsActivity.class);
                intent.putExtra("Title",newsListAll.get(position).getTitle());
                intent.putExtra("Author",newsListAll.get(position).getAuthor());
                intent.putExtra("Image", newsListAll.get(position).getThumbnail());
                intent.putExtra("Description", newsListAll.get(position).getDescription());
                intent.putExtra("Like", newsListAll.get(position).isLiked());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    /**
     * Handle filter action from mode (liked/all) and query from search view
     */
    Filter filter = new Filter() {

        //Run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<News> filteredList = new ArrayList<>();

            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(newsListAll);
            }  else {
                if (charSequence.toString().contains("/liked")) {
                    if (charSequence.toString().equals("/liked")) {
                        for (News news : newsListAll) {
                            if (news.isLiked()) {
                                filteredList.add(news);
                            }
                        }
                    } else {
                        for (News news : newsListAll) {
                            String text = charSequence.toString().split("/liked")[0];
                            if (news.isLiked() && news.getTitle().toLowerCase().contains(text)) {
                                filteredList.add(news);
                            }
                        }
                    }
                } else {
                    for (News news : newsListAll) {
                        if (news.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filteredList.add(news);
                        }
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values =filteredList;

            return filterResults;
        }


        //Run on UI thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            newsList.clear();
            newsList.addAll((Collection<? extends News>) results.values);
            notifyDataSetChanged();
        }
    };

    public static class MyHolder extends RecyclerView.ViewHolder {
        TextView newsTitle;
        TextView newsDescription;
        CardView cardView;
        ImageView newsThumbnail;
        ImageView likeIcon;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            likeIcon = (ImageView) itemView.findViewById(R.id.like_btn);
            newsTitle = (TextView) itemView.findViewById(R.id.news_title);
            newsThumbnail = (ImageView) itemView.findViewById(R.id.news_image);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            newsDescription = (TextView) itemView.findViewById(R.id.description);
        }
    }
}
