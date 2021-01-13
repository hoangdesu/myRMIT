package com.example.myrmit.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.myrmit.R;

import java.util.List;

public class GroupArrayAdapter extends android.widget.ArrayAdapter<Group> {
    private final List<Group> list;
    private final Activity context;

    public GroupArrayAdapter(Activity context, List<Group> list) {
        super(context, R.layout.time_list, list);
        this.context = context;
        this.list = list;

    }
    static class ViewHolder {
        protected TextView id;
        protected TextView name;
        protected TextView group1;
        protected TextView group2;
        protected CheckBox checkBox1;
        protected CheckBox checkBox2;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.time_list, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.id = (TextView) view.findViewById(R.id.textView5);
            viewHolder.name = (TextView) view.findViewById(R.id.textView7);
            viewHolder.group1 = view.findViewById(R.id.textView10);
            viewHolder.group2 = view.findViewById(R.id.textView12);
            viewHolder.checkBox1 = view.findViewById(R.id.checkBox4);
            viewHolder.checkBox2 = view.findViewById(R.id.checkBox5);
            viewHolder.checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {

                    Group element = (Group) viewHolder.checkBox1.getTag();
                    element.setGroup1(buttonView.isChecked());
                    if (buttonView.isChecked()) {
                        element.setGroup2(false);
                        viewHolder.checkBox2.setChecked(false);
                    }
                }
            });
            viewHolder.checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {

                    Group element = (Group) viewHolder.checkBox2.getTag();
                    element.setGroup2(buttonView.isChecked());
                    if (buttonView.isChecked()) {
                        element.setGroup1(false);
                        viewHolder.checkBox1.setChecked(false);
                    }
                }
            });

            view.setTag(viewHolder);
            viewHolder.checkBox1.setTag(list.get(position));
            viewHolder.checkBox2.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkBox1.setTag(list.get(position));
            ((ViewHolder) view.getTag()).checkBox2.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.id.setText(String.valueOf(position+1));
        holder.name.setText(list.get(position).getCourseName());
        holder.group1.setText("Lecturer: " + list.get(position).getLecturer() + " - Time: " + list.get(position).getTime1() + ":00 -> " + (Integer.parseInt(list.get(position).getTime1())+3) +":00"  + " ("+ convertDay(list.get(position).getDay1())+")");
        holder.group2.setText("Lecturer: " + list.get(position).getLecturer() + " - Time: " + list.get(position).getTime2() + ":00 -> " + (Integer.parseInt(list.get(position).getTime2())+3) +":00" + " ("+ convertDay(list.get(position).getDay2())+")");

        holder.checkBox1.setChecked(list.get(position).isGroup1());
        holder.checkBox2.setChecked(list.get(position).isGroup2());
        return view;
    }

    private String convertDay(String day){
        switch (day){
            case "mon":
                return "Monday" ;
            case "tue":
                return "Tuesday";
            case "wed":
                return "Wednesday";
            case "thu":
                return "Thursday";
            case "fri":
                return "Friday";
            default: break;
        }
        return null;
    }
}
