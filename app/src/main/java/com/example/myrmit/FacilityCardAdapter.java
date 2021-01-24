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
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FacilityCardAdapter extends PagerAdapter {
    private List<Facility> facilityList;
    private LayoutInflater layoutInflater;
    private Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public FacilityCardAdapter(List<Facility> facilityList, Context context) {
        this.facilityList = facilityList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return facilityList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.facility_item, container, false);

        ImageView facilityImage;
        TextView title, openHour;

        facilityImage = (ImageView) view.findViewById(R.id.image);
        title = (TextView) view.findViewById(R.id.title);
        openHour = (TextView) view.findViewById(R.id.description);

        StorageReference storageReference = storage.getReference().child(facilityList.get(position).getImage());

        try {
            final File file = File.createTempFile("image","jpg");
            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
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
        title.setText(facilityList.get(position).getTitle());
        openHour.setText(facilityList.get(position).getOpenHour());

        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
