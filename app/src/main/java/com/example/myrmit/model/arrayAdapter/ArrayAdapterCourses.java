package com.example.myrmit.model.arrayAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.myrmit.R;
import com.example.myrmit.model.objects.CourseReview;

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
        // Setup the components
        viewHolder.name = view.findViewById(R.id.textView36);
        viewHolder.description = view.findViewById(R.id.textView38);
        viewHolder.code = view.findViewById(R.id.textView35);
        viewHolder.space = view.findViewById(R.id.textView34);
        viewHolder.info = view.findViewById(R.id.imageView14);
        viewHolder.name.setText(list.get(position).getName());

        // Set onClick for viewing info
        viewHolder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                View view = context.getLayoutInflater().inflate(R.layout.courseslist_dialog, null);
                dialog.setView(view);
                final AlertDialog alert = dialog.create();
                ListView listView = view.findViewById(R.id.list_courses);
                ArrayCoursesView arrayAdapter = new ArrayCoursesView(context, list.get(position).getCourses());
                listView.setAdapter(arrayAdapter);
                alert.show();
            }
        });

        // Set behavior
        viewHolder.description.setText(list.get(position).getDescription());
        viewHolder.code.setText(list.get(position).getCode());
        // Set line extending
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
