package com.example.myrmit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyHolder> {
    private Context context;
    private List<News> data;
    private FirebaseStorage storage = FirebaseStorage.getInstance("gs://myrmit-c2020");

    public NewsAdapter(Context context, List<News> data) {
        this.context = context;
        this.data = data;
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
        holder.newsTitle.setText(data.get(position).getTitle());
        holder.newsThumbnail.setImageResource(data.get(position).getThumbnail());
        StorageReference storageReference = storage.getReference().child("temp.jpg");
        System.out.println("yeyeyeyeye");
        System.out.println(storage.getReference().getBucket().toString());
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
        return data.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView newsTitle;
        CardView cardView;
        ImageView newsThumbnail;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            newsTitle = (TextView) itemView.findViewById(R.id.news_title);
            newsThumbnail = (ImageView) itemView.findViewById(R.id.news_image);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }
}
