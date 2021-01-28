package com.example.myrmit.model.arrayAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.myrmit.R;
import com.example.myrmit.model.objects.Room;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RoomCardAdapter extends PagerAdapter {
    private final List<Room> roomList;
    private final Context context;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    private ImageView roomImage, availableImage;

    public RoomCardAdapter(List<Room> roomList, Context context) {
        this.roomList = roomList;
        this.context = context;
    }

    /**
     * Get room list
     * @return List<Room>
     */
    public List<Room> getRoomList() {
        return roomList;
    }

    /**
     * Update the status of the room
     * @param available boolean
     * @param index int
     */
    public void updateAvailability(boolean available, int index) {
        roomList.get(index).setAvailable(available);
        if (available) {
            availableImage.setImageResource(R.drawable.tick);
        } else {
            availableImage.setImageResource(R.drawable.cross);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.room_item, container, false);
        // Setup all components
        roomImage = (ImageView) view.findViewById(R.id.image);
        TextView name = (TextView) view.findViewById(R.id.room_id);
        TextView capacity = (TextView) view.findViewById(R.id.capacity);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating);
        availableImage = (ImageView) view.findViewById(R.id.availability_image);

        // Get data's reference
        StorageReference storageReference = storage.getReference().child(roomList.get(position).getImage());

        try {
            // Get image from firebase
            final File file = File.createTempFile("image","jpg");
            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Set the received image
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

        // Set behavior for all items
        name.setText(roomList.get(position).getName());
        capacity.setText(String.valueOf(roomList.get(position).getCapacity()));
        ratingBar.setRating((float) roomList.get(position).getRating());

        // Set availability fo the room
        if (roomList.get(position).isAvailable()) {
            availableImage.setImageResource(R.drawable.tick);
        } else {
            availableImage.setImageResource(R.drawable.cross);
        }

        container.addView(view, 0);
        return view;
    }

    /**
     * Destroy item
     * @param container ViewGroup
     * @param position int
     * @param object Object
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
