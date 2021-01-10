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

public class ArrayAdapter extends android.widget.ArrayAdapter<Course> {
    private final List<Course> list;
    private final Activity context;
    private List<Boolean> isFeb;
    private List<Boolean> isJun;
    private List<Boolean> isNov;
    public ArrayAdapter(Activity context, List<Course> list, List<Boolean> isFeb, List<Boolean> isJun, List<Boolean> isNov) {
        super(context, R.layout.course_list, list);
        this.context = context;
        this.list = list;
        this.isFeb = isFeb;
        this.isJun = isJun;
        this.isNov = isNov;
    }
    static class ViewHolder {
        protected TextView id;
        protected TextView name;
        protected CheckBox feb;
        protected CheckBox jun;
        protected CheckBox nov;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.course_list, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.id = (TextView) view.findViewById(R.id.id);
            viewHolder.name = (TextView) view.findViewById(R.id.description);
            viewHolder.feb = (CheckBox) view.findViewById(R.id.checkBox3);
            viewHolder.jun = (CheckBox) view.findViewById(R.id.checkBox2);
            viewHolder.nov = (CheckBox) view.findViewById(R.id.checkBox);
            if (!isFeb.get(position)){
                viewHolder.feb.setVisibility(View.INVISIBLE);
                viewHolder.feb.setEnabled(false);
            }
            if (!isJun.get(position)){
                viewHolder.jun.setVisibility(View.INVISIBLE);
                viewHolder.jun.setEnabled(false);
            }
            if (!isNov.get(position)){
                viewHolder.nov.setVisibility(View.INVISIBLE);
                viewHolder.nov.setEnabled(false);
            }
            viewHolder.feb
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            Course element = (Course) viewHolder.feb
                                    .getTag();
                            element.setFeb(buttonView.isChecked());
                            if (buttonView.isChecked()) {
                                element.setNov(false);
                                element.setJun(false);
                                viewHolder.jun.setChecked(false);
                                viewHolder.nov.setChecked(false);
                            }
                        }
                    });
            viewHolder.jun
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            Course element = (Course) viewHolder.jun
                                    .getTag();
                            element.setJun(buttonView.isChecked());
                            if (buttonView.isChecked()) {
                                element.setFeb(false);
                                element.setNov(false);
                                viewHolder.nov.setChecked(false);
                                viewHolder.feb.setChecked(false);
                            }
                        }
                    });
            viewHolder.nov
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            Course element = (Course) viewHolder.nov
                                    .getTag();
                            element.setNov(buttonView.isChecked());
                            if (buttonView.isChecked()) {
                                viewHolder.jun.setChecked(false);
                                viewHolder.feb.setChecked(false);
                                element.setFeb(false);
                                element.setJun(false);
                            }
                        }
                    });
            view.setTag(viewHolder);
            viewHolder.feb.setTag(list.get(position));
            viewHolder.nov.setTag(list.get(position));
            viewHolder.jun.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).feb.setTag(list.get(position));
            ((ViewHolder) view.getTag()).jun.setTag(list.get(position));
            ((ViewHolder) view.getTag()).nov.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.id.setText(String.valueOf(position+1));
        holder.name.setText(list.get(position).getName());
        holder.feb.setChecked(list.get(position).isFeb());
        holder.jun.setChecked(list.get(position).isJun());
        holder.nov.setChecked(list.get(position).isNov());
        return view;
    }
}
