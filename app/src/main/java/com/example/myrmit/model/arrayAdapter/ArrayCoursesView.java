package com.example.myrmit.model.arrayAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myrmit.R;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayCoursesView extends android.widget.ArrayAdapter<String> {
    private final ArrayList<String> list;
    private final Activity context;
    public ArrayCoursesView(Activity context, ArrayList<String> list) {
        super(context, R.layout.list_courses_guess, list);
        this.list = list;
        this.context = context;
    }

    static class ViewHolder {
        protected TextView name;
        protected TextView id;
        protected TextView space;
    }

    /**
     * Get View
     * @param position int
     * @param convertView View
     * @param parent ViewGroup
     * @return  View
     */
    @SuppressLint({"InflateParams", "SetTextI18n", "ViewHolder"})
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflator = context.getLayoutInflater();
        View view = inflator.inflate(R.layout.list_courses_guess, null);
        final ViewHolder viewHolder = new ViewHolder();
        // Initial setting for all components
        viewHolder.name = view.findViewById(R.id.textView42);
        viewHolder.id = view.findViewById(R.id.textViewId);
        viewHolder.space = view.findViewById(R.id.textView40);
        // Set their behavior based on given data
        viewHolder.name.setText(list.get(position).toString());
        viewHolder.id.setText(String.valueOf(position+1));
        // Set line extending
        viewHolder.name.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                viewHolder.name.getViewTreeObserver().removeOnPreDrawListener(this);
                int lineCount = viewHolder.name.getLineCount();
                if (viewHolder.space.getText().toString().split("").length-1 < lineCount){
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
