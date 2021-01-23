package com.example.myrmit.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myrmit.R;

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
        viewHolder.name = view.findViewById(R.id.textView25);
        viewHolder.time = view.findViewById(R.id.textView28);
        viewHolder.description = view.findViewById(R.id.textView31);
        viewHolder.location = view.findViewById(R.id.textView26);
        viewHolder.call = view.findViewById(R.id.imageView9);
        viewHolder.map = view.findViewById(R.id.imageView8);
        viewHolder.phone = view.findViewById(R.id.textView21);
        viewHolder.id = view.findViewById(R.id.textView24);
        viewHolder.space = view.findViewById(R.id.textView23);
        viewHolder.id.setText(String.valueOf(position+1));
        viewHolder.name.setText(list.get(position).getName());
        viewHolder.description.setText(list.get(position).getDescription());
        viewHolder.time.setText(list.get(position).getTime());
        viewHolder.location.setText(list.get(position).getLocation());
        viewHolder.phone.setText(list.get(position).getPhone());
        viewHolder.description.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // Remove listener because we don't want this called before _every_ frame
                viewHolder.description.getViewTreeObserver().removeOnPreDrawListener(this);

                // Drawing happens after layout so we can assume getLineCount() returns the correct value
                int lineCount = viewHolder.description.getLineCount();
                if ( lineCount != 1 && viewHolder.space.getText().toString().split("").length-12 < lineCount){
                    for (int i = 1; i< lineCount; i++) {
                        viewHolder.space.setText(viewHolder.space.getText().toString() + "\n");
                    }
                }

                return true;
            }
        });

        viewHolder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + list.get(position).getPhone()));
                context.startActivity(dialIntent);
            }
        });

        /**
         * Binh set hereeeeeee
         */
        viewHolder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }
}
