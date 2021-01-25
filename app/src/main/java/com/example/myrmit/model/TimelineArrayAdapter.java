package com.example.myrmit.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.myrmit.R;

import java.util.List;

public class TimelineArrayAdapter extends android.widget.ArrayAdapter<Timeline> {
    private final List<Timeline> list;
    private final Activity context;
    private int color;
    public TimelineArrayAdapter(Activity context, List<Timeline> list) {
        super(context, R.layout.note_list, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView time;
        protected TextView note;
        protected TextView space;
    }

    @SuppressLint({"InflateParams", "SetTextI18n", "ViewHolder"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        LayoutInflater inflator = context.getLayoutInflater();
        view = inflator.inflate(R.layout.note_list, null);
        color = ((ColorDrawable)view.getBackground()).getColor();
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.space = view.findViewById(R.id.space);
        viewHolder.time = view.findViewById(R.id.time);
        viewHolder.note = view.findViewById(R.id.note);
        if (list.get(position).getType().equals("note")){
            view.setBackgroundColor(Color.WHITE);
        }
        else view.setBackgroundColor(color);
        viewHolder.time.setText(list.get(position).getTime() +":00");
        viewHolder.note.setText(list.get(position).getNote());
        viewHolder.note.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                viewHolder.note.getViewTreeObserver().removeOnPreDrawListener(this);

                int lineCount = viewHolder.note.getLineCount();
                if (viewHolder.space.getText().toString().split("").length-1 <= lineCount){
                    for (int i = 1; i< lineCount; i++) {
                        viewHolder.space.setText(viewHolder.space.getText().toString() + "\n");
                    }
                }

                return true;
            }
        });
        return view;
    }

}
