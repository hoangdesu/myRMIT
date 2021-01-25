package com.example.myrmit.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myrmit.R;

import java.util.List;

public class ArrayAdapterCourses extends android.widget.ArrayAdapter<CourseReview> {
    private final List<CourseReview> list;
    private final Activity context;
    public ArrayAdapterCourses(Activity context, List<CourseReview> list) {
        super(context, R.layout.course_list_guess, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView name;
        protected TextView description;
        protected TextView space;
        protected ImageView info;
        protected TextView code;
    }

    @SuppressLint({"InflateParams", "SetTextI18n", "ViewHolder"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        LayoutInflater inflator = context.getLayoutInflater();
        view = inflator.inflate(R.layout.course_list_guess, null);
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.name = view.findViewById(R.id.textView36);
        viewHolder.description = view.findViewById(R.id.textView38);
        viewHolder.code = view.findViewById(R.id.textView35);
        viewHolder.space = view.findViewById(R.id.textView34);
        viewHolder.info = view.findViewById(R.id.imageView14);
        viewHolder.name.setText(list.get(position).getName());
        viewHolder.description.setText(list.get(position).getDescription());
        viewHolder.code.setText(list.get(position).getCode());
        viewHolder.description.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                viewHolder.description.getViewTreeObserver().removeOnPreDrawListener(this);
                int lineCount = viewHolder.description.getLineCount();
                if (viewHolder.space.getText().toString().split("").length-3 <= lineCount){
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
