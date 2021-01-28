package com.example.myrmit.model.arrayAdapter;

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
import androidx.viewpager.widget.PagerAdapter;

import com.example.myrmit.model.objects.News;
import com.example.myrmit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SwipeCardAdapter extends PagerAdapter {
    private final List<News> newsList;
    private final Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public SwipeCardAdapter(List<News> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        // Configure all the components
        View view = layoutInflater.inflate(R.layout.event_item, container, false);
        ImageView newsImage = (ImageView) view.findViewById(R.id.news_image);
        TextView title = (TextView) view.findViewById(R.id.news_title);
        TextView description = (TextView) view.findViewById(R.id.description);

        // Start setting up
        setupCard(newsImage, title,  description, position);
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    /**
     * Setup the card (image, details)
     * @param newsImage ImageView
     * @param title TextView
     * @param description TextView
     * @param position int
     */
    private void setupCard(ImageView newsImage, TextView title, TextView description, int position){
        StorageReference storageReference = storage.getReference().child(newsList.get(position).getThumbnail());

        try {
            final File file = File.createTempFile("image","jpg");
            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    newsImage.setImageBitmap(bitmap);
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
        title.setText(newsList.get(position).getTitle());
        description.setText(newsList.get(position).getDescription());
    }
}
