package com.example.myrmit;

import android.content.Context;
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

import com.example.myrmit.model.FirebaseHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyHolder> implements Filterable {
    private Context context;
    private List<News> newsList;
    private List<News> newsListAll;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseHandler firebaseHandler = new FirebaseHandler();

    public NewsAdapter(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
        this.newsListAll = new ArrayList<>(newsList);
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.news_item, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //Set title, thumnail for the main activity
        holder.newsTitle.setText(newsList.get(position).getTitle());
        holder.newsDescription.setText(newsList.get(position).getDescription());
        if (newsList.get(position).isLiked()) {
            holder.likeIcon.setColorFilter(Color.parseColor("#FFE60028"));
        } else {
            holder.likeIcon.setColorFilter(Color.parseColor("#FF000000"));
        }
        StorageReference storageReference = storage.getReference().child(newsList.get(position).getThumbnail());
        try {
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

        holder.likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newsList.get(position).isLiked()) {
                    firebaseHandler.updatePostLike("s3715271@rmit.edu.vn", holder.newsTitle.getText().toString(), true);
                    holder.likeIcon.setColorFilter(Color.parseColor("#FFE60028"));
                    newsList.get(position).setLike(true);
                } else {
                    firebaseHandler.updatePostLike("s3715271@rmit.edu.vn", holder.newsTitle.getText().toString(), false);
                    holder.likeIcon.setColorFilter(Color.parseColor("#FF000000"));
                    newsList.get(position).setLike(false);
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send data to NewsActivity
                /*Intent intent = new Intent(context,NewsActivity.class);
                intent.putExtra("Title",data.get(position).getTitle());
                intent.putExtra("Author",data.get(position).getAuthor());
                intent.putExtra("Thumbnail", data.get(position).getThumbnail());
                context.startActivity(intent);*/
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

    Filter filter2 = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
    };

    public class MyHolder extends RecyclerView.ViewHolder {
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
