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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FacilityCardAdapter extends PagerAdapter {
    private final List<Facility> facilityList;
    private final Context context;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    public FacilityCardAdapter(List<Facility> facilityList, Context context) {
        this.facilityList = facilityList;
        this.context = context;
    }

    /**
     * Get size
     * @return int
     */
    @Override
    public int getCount() {
        return facilityList.size();
    }

    /**
     * check view
     * @param view View
     * @param object Object
     * @return boolean
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    /**
     * Set instantiate item
     * @param container ViewGroup
     * @param position int
     * @return Object
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.facility_item, container, false);

        // Setup all the components
        ImageView facilityImage;
        TextView title, openHour;
        RatingBar ratingBar;
        // Configurations
        facilityImage = (ImageView) view.findViewById(R.id.image);
        title = (TextView) view.findViewById(R.id.title);
        openHour = (TextView) view.findViewById(R.id.open_hour);
        ratingBar = (RatingBar) view.findViewById(R.id.rating);

        // Store the image to fire store
        StorageReference storageReference = storage.getReference().child(facilityList.get(position).getImage());

        try {
            // Get image from firebase
            final File file = File.createTempFile("image","jpg");
            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Set image
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    facilityImage.setImageBitmap(bitmap);
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

        // Set behavior based on given data
        title.setText(facilityList.get(position).getTitle());
        openHour.setText(facilityList.get(position).getOpenHour());
        ratingBar.setRating((float) facilityList.get(position).getRating());

        container.addView(view, 0);


        return view;
    }

    /**
     * Destroy item function
     * @param container ViewGroup
     * @param position int
     * @param object Object
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
