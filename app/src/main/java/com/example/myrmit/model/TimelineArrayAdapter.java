package com.example.myrmit.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.myrmit.R;

import java.util.List;

public class TimelineArrayAdapter extends android.widget.ArrayAdapter<Timeline> {
    private final List<Timeline> list;
    private final Activity context;

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

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.note_list, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.space = view.findViewById(R.id.space);
            viewHolder.time = view.findViewById(R.id.time);
            viewHolder.note = view.findViewById(R.id.note);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).space.setTag(list.get(position));
            ((ViewHolder) view.getTag()).time.setTag(list.get(position));
            ((ViewHolder) view.getTag()).note.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        if (!list.get(position).getType().equals("class")){
            view.setBackgroundColor(Color.WHITE);
        }
        holder.time.setText(list.get(position).getTime() +":00");
        holder.note.setText(list.get(position).getNote());
        if (holder.note.getLayout() != null && holder.note.getLayout().getLineCount() != 1 && holder.space.getText().toString().split("").length <= holder.note.getLayout().getLineCount()+1){
            for (int i = 1; i< holder.note.getLayout().getLineCount(); i++) {
                holder.space.setText(holder.space.getText().toString() + "\n");
            }
        }
        return view;
    }

}
