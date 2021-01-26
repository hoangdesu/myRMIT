package com.example.myrmit.model.arrayAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myrmit.MapsActivity;
import com.example.myrmit.R;
import com.example.myrmit.model.objects.RMITService;

import java.util.List;

public class ArrayAdapterService extends android.widget.ArrayAdapter<RMITService>{
    private final List<RMITService> list;
    private final Activity context;
    public ArrayAdapterService(Activity context, List<RMITService> list) {
        super(context, R.layout.service_item, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView name;
        protected TextView phone;
        protected TextView id;
        protected TextView description;
        protected TextView location;
        protected ImageView call;
        protected ImageView map;
        protected TextView space;
        protected TextView time;
    }

    @SuppressLint({"InflateParams", "SetTextI18n", "ViewHolder"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        LayoutInflater inflator = context.getLayoutInflater();
        view = inflator.inflate(R.layout.service_item, null);
        final ViewHolder viewHolder = new ViewHolder();
        // Initial setting for all components
        viewHolder.name = view.findViewById(R.id.textView25);
        viewHolder.time = view.findViewById(R.id.textView28);
        viewHolder.description = view.findViewById(R.id.textView31);
        viewHolder.location = view.findViewById(R.id.textView26);
        viewHolder.call = view.findViewById(R.id.imageView9);
        viewHolder.map = view.findViewById(R.id.imageView8);
        viewHolder.phone = view.findViewById(R.id.textView21);
        viewHolder.id = view.findViewById(R.id.textView24);
        viewHolder.space = view.findViewById(R.id.textView23);
        // Set their behavior based on given data
        viewHolder.id.setText(String.valueOf(position+1));
        viewHolder.name.setText(list.get(position).getName());
        viewHolder.description.setText(list.get(position).getDescription());
        viewHolder.time.setText("Time Work: " + list.get(position).getTime());
        viewHolder.location.setText("Location: " + list.get(position).getLocation());
        viewHolder.phone.setText("Phone Call: " + list.get(position).getPhone());
        // Set line extending
        viewHolder.description.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                viewHolder.description.getViewTreeObserver().removeOnPreDrawListener(this);
                int lineCount = viewHolder.description.getLineCount();
                if (viewHolder.space.getText().toString().split("").length - 11 < lineCount){
                    for (int i = 1; i< lineCount; i++) {
                        viewHolder.space.setText(viewHolder.space.getText().toString() + "\n");
                    }
                }
                return true;
            }
        });
        // Set on Click
        setOnClick(viewHolder, position);
        return view;
    }

    /**
     * Set on Click for necessary items
     * @param viewHolder ViewHolder
     * @param position int
     */
    private void setOnClick(ViewHolder viewHolder, int position){
        viewHolder.call.setOnClickListener(new View.OnClickListener() {         // Get the phone number and move to calling app
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + list.get(position).getPhone()));
                context.startActivity(dialIntent);
            }
        });

        viewHolder.map.setOnClickListener(new View.OnClickListener() {          // Get the location and move to map to view where it is
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                String location = viewHolder.location.getText().toString();
                if (location.charAt(10) == '1') {
                    intent.putExtra("Location", "Building 1");
                } else if (location.contains("Sport Hall")) {
                    intent.putExtra("Location", "Sport Hall");
                } else if (location.charAt(10) == '2') {
                    intent.putExtra("Location", "Building 2");
                }
                context.startActivity(intent);
            }
        });
    }
}
