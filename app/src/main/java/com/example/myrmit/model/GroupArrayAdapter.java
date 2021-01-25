package com.example.myrmit.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.myrmit.AllocationFragment;
import com.example.myrmit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupArrayAdapter extends android.widget.ArrayAdapter<Group> {
    private final List<Group> list;
    private final Activity context;
    private final boolean isStudent;
    private String program;
    private FirebaseHandler firebaseHandler = new FirebaseHandler();
    public GroupArrayAdapter(Activity context, List<Group> list, boolean isStudent, String program) {
        super(context, R.layout.time_list, list);
        this.context = context;
        this.isStudent = isStudent;
        this.list = list;
        this.program = program;

    }
    static class ViewHolder {
        protected TextView id;
        protected TextView name;
        protected TextView group1;
        protected TextView group2;
        protected ImageView setTime1;
        protected ImageView setTime2;
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
            viewHolder.setTime1 = view.findViewById(R.id.imageView10);
            viewHolder.setTime2 = view.findViewById(R.id.imageView11);
            viewHolder.checkBox1 = view.findViewById(R.id.checkBox4);
            viewHolder.checkBox2 = view.findViewById(R.id.checkBox5);
            view.setTag(viewHolder);
            viewHolder.checkBox1.setTag(list.get(position));
            viewHolder.checkBox2.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkBox1.setTag(list.get(position));
            ((ViewHolder) view.getTag()).checkBox2.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        if (isStudent) {
            holder.checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {

                    Group element = (Group) holder.checkBox1.getTag();
                    element.setGroup1(buttonView.isChecked());
                    if (buttonView.isChecked()) {
                        element.setGroup2(false);
                        holder.checkBox2.setChecked(false);
                    }
                }
            });
            holder.checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {

                    Group element = (Group) holder.checkBox2.getTag();
                    element.setGroup2(buttonView.isChecked());
                    if (buttonView.isChecked()) {
                        element.setGroup1(false);
                        holder.checkBox1.setChecked(false);
                    }
                }
            });
            holder.id.setText(String.valueOf(position + 1));
            holder.name.setText(list.get(position).getCourseName());
            holder.group1.setText("Lecturer: " + list.get(position).getLecturer() + " - Time: " + list.get(position).getTime1() + ":00 -> " + (Integer.parseInt(list.get(position).getTime1()) + 3) + ":00" + " (" + convertDay(list.get(position).getDay1()) + ")");
            holder.group2.setText("Lecturer: " + list.get(position).getLecturer() + " - Time: " + list.get(position).getTime2() + ":00 -> " + (Integer.parseInt(list.get(position).getTime2()) + 3) + ":00" + " (" + convertDay(list.get(position).getDay2()) + ")");
            holder.checkBox1.setChecked(list.get(position).isGroup1());
            holder.checkBox2.setChecked(list.get(position).isGroup2());
            holder.setTime1.setEnabled(false);
            holder.setTime1.setVisibility(View.INVISIBLE);
            holder.setTime2.setEnabled(false);
            holder.setTime2.setVisibility(View.INVISIBLE);
        }
        else {
            holder.checkBox1.setEnabled(false);
            holder.checkBox1.setVisibility(View.INVISIBLE);
            holder.checkBox2.setEnabled(false);
            holder.checkBox2.setVisibility(View.INVISIBLE);
            holder.id.setText(String.valueOf(position + 1));
            holder.name.setText(list.get(position).getCourseName());
            holder.group1.setText("Time: " + list.get(position).getTime1() + ":00 -> " + (Integer.parseInt(list.get(position).getTime1()) + 3) + ":00" + " (" + convertDay(list.get(position).getDay1()) + ")");
            holder.group2.setText("Time: " + list.get(position).getTime2() + ":00 -> " + (Integer.parseInt(list.get(position).getTime2()) + 3) + ":00" + " (" + convertDay(list.get(position).getDay2()) + ")");
            holder.setTime1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                    View view = context.getLayoutInflater().inflate(R.layout.dialog_change_time, null);
                    dialog.setView(view);
                    final AlertDialog alert = dialog.create();
                    Button add = view.findViewById(R.id.button9);
                    Spinner spinner = view.findViewById(R.id.spinner2);
                    Spinner spinner1 = view.findViewById(R.id.spinner3);
                    setSpinner(spinner, view, false);
                    setSpinner(spinner1,view, true);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!revert(spinner.getSelectedItem().toString(), false).equals(list.get(position).getTime1()) || !revert(spinner1.getSelectedItem().toString(), true).equals(list.get(position).getDay1())){
                                firebaseHandler.getProgram(program).collection("data").document(list.get(position).getCourseName()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        ArrayList<String> day = (ArrayList<String>) task.getResult().get("day");
                                        ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                                        if (day.get(0).equals(list.get(position).getDay1()) && time.get(0).equals(list.get(position).getTime1())){
                                            day.remove(0);
                                            time.remove(0);
                                            day.add(0, revert(spinner1.getSelectedItem().toString(), true));
                                            time.add(0, revert(spinner.getSelectedItem().toString(), false));
                                        }
                                        else if (day.get(1).equals(list.get(position).getDay1()) && time.get(1).equals(list.get(position).getTime1())){
                                            day.remove(1);
                                            time.remove(1);
                                            day.add(1, revert(spinner1.getSelectedItem().toString(), true));
                                            time.add(1, revert(spinner.getSelectedItem().toString(), false));
                                        }
                                        task.getResult().getReference().update("day", day);
                                        task.getResult().getReference().update("time", time);
                                    }
                                });
                                alert.dismiss();
                                context.recreate();
                            }
                            else Toast.makeText(context, "It is the same! Failure!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alert.show();
                }
            });
            holder.setTime2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                    View view = context.getLayoutInflater().inflate(R.layout.dialog_change_time, null);
                    dialog.setView(view);
                    final AlertDialog alert = dialog.create();
                    Button add = view.findViewById(R.id.button9);
                    Spinner spinner = view.findViewById(R.id.spinner2);
                    Spinner spinner1 = view.findViewById(R.id.spinner3);
                    setSpinner(spinner, view, false);
                    setSpinner(spinner1,view, true);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!revert(spinner.getSelectedItem().toString(), false).equals(list.get(position).getTime2()) || !revert(spinner1.getSelectedItem().toString(), true).equals(list.get(position).getDay2())){
                                firebaseHandler.getProgram(program).collection("data").document(list.get(position).getCourseName()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        ArrayList<String> day = (ArrayList<String>) task.getResult().get("day");
                                        ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                                        if (day.get(0).equals(list.get(position).getDay2()) && time.get(0).equals(list.get(position).getTime2())){
                                            day.remove(0);
                                            time.remove(0);
                                            day.add(0, revert(spinner1.getSelectedItem().toString(), true));
                                            time.add(0, revert(spinner.getSelectedItem().toString(), false));
                                        }
                                        else if (day.get(1).equals(list.get(position).getDay2()) && time.get(1).equals(list.get(position).getTime2())){
                                            day.remove(1);
                                            time.remove(1);
                                            day.add(1, revert(spinner1.getSelectedItem().toString(), true));
                                            time.add(1, revert(spinner.getSelectedItem().toString(), false));
                                        }
                                        task.getResult().getReference().update("day", day);
                                        task.getResult().getReference().update("time", time);
                                    }
                                });
                                alert.dismiss();
                                context.recreate();
                            }
                            else Toast.makeText(context, "It is the same! Failure!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alert.show();
                }
            });
        }
        return view;
    }

    private String revert(String string, boolean isDate){
        if (isDate){
            switch (string){
                case "Monday": return "mon";
                case "Tuesday": return "tue";
                case "Wednesday": return "wed";
                case "Thursday": return "thu";
                case "Friday": return "fri";
            }
        }
        return string.split(":")[0];
    }

    private void setSpinner(Spinner spinner, View view, boolean isDay){
        ArrayList<String> spinnerTime = new ArrayList<>();
        if (isDay){
            spinnerTime.add("Monday");
            spinnerTime.add("Tuesday");
            spinnerTime.add("Wednesday");
            spinnerTime.add("Thursday");
            spinnerTime.add("Friday");
        }
        else {
            for (int i = 7; i< 15; i++){
                spinnerTime.add(i+":00");
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),android.R.layout.simple_spinner_item, spinnerTime);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(null);
        spinner.setAdapter(adapter);
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
