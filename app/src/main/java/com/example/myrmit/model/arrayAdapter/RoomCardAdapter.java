package com.example.myrmit.model.arrayAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.PagerAdapter;

import com.example.myrmit.Facility;
import com.example.myrmit.R;
import com.example.myrmit.bookingActivity.Room;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RoomCardAdapter extends PagerAdapter {
    private List<Room> roomList;
    private LayoutInflater layoutInflater;
    private Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public RoomCardAdapter(List<Room> roomList, Context context) {
        this.roomList = roomList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.room_item, container, false);

        ImageView roomImage, availableImage;
        TextView name, capacity;
        RatingBar ratingBar;

        roomImage = (ImageView) view.findViewById(R.id.image);
        name = (TextView) view.findViewById(R.id.room_id);
        capacity = (TextView) view.findViewById(R.id.capacity);
        ratingBar = (RatingBar) view.findViewById(R.id.rating);
        availableImage = (ImageView) view.findViewById(R.id.availability_image);

        StorageReference storageReference = storage.getReference().child(roomList.get(position).getImage());

        try {
            final File file = File.createTempFile("image","jpg");
            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    roomImage.setImageBitmap(bitmap);
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

        name.setText(roomList.get(position).getName());
        capacity.setText(String.valueOf(roomList.get(position).getCapacity()));
        ratingBar.setRating((float) roomList.get(position).getRating());

        if (roomList.get(position).isAvailable()) {
            availableImage.setImageResource(R.drawable.tick);
        } else {
            availableImage.setImageResource(R.drawable.cross);
        }

        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
