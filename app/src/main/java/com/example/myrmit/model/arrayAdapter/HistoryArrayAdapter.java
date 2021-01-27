package com.example.myrmit.model.arrayAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myrmit.R;

import com.example.myrmit.model.objects.History;

import java.util.ArrayList;
import java.util.List;

public class HistoryArrayAdapter extends android.widget.ArrayAdapter<History> {
    private final Activity context;
    private final ArrayList<History> list;

    public HistoryArrayAdapter(Activity context, ArrayList<History> list) {
        super(context, R.layout.history_item, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView name;
        protected TextView id;
        protected TextView gpa;
    }

    /**
     * Get view item function
     * @param position int
     * @param convertView View
     * @param parent ViewGroup
     * @return View
     */
    @SuppressLint({"InflateParams","ViewHolder", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = context.getLayoutInflater();
        View view = inflator.inflate(R.layout.history_item, null);
        final ViewHolder viewHolder = new ViewHolder();
        // Initial setting for all components
        viewHolder.gpa = (TextView) view.findViewById(R.id.gparecord);
        viewHolder.name = (TextView) view.findViewById(R.id.coursename1);
        viewHolder.id = view.findViewById(R.id.idnumber);
        // Set tag
        view.setTag(viewHolder);
        // Get view holder from tag
        ViewHolder holder = (ViewHolder) view.getTag();
        // Set the behavior for all components based on given data from History object
        holder.id.setText(String.valueOf(position+1));
        holder.gpa.setText(list.get(position).getGpa());
        holder.name.setText(list.get(position).getCourseName());
        return view;
    }
}
