package com.example.myrmit.model.arrayAdapter;

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

import com.example.myrmit.R;
import com.example.myrmit.model.FirebaseHandler;
import com.example.myrmit.model.objects.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupArrayAdapter extends android.widget.ArrayAdapter<Group> {
    private final List<Group> list;
    private final Activity context;
    private final boolean isStudent;
    private final String program;
    private final FirebaseHandler firebaseHandler = new FirebaseHandler();
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
        LayoutInflater inflator = context.getLayoutInflater();
        View view = inflator.inflate(R.layout.time_list, null);
        final ViewHolder viewHolder = new ViewHolder();
        // Initial setting for all components
        viewHolder.id = (TextView) view.findViewById(R.id.textView5);
        viewHolder.name = (TextView) view.findViewById(R.id.textView7);
        viewHolder.group1 = view.findViewById(R.id.textView10);
        viewHolder.group2 = view.findViewById(R.id.textView12);
        viewHolder.setTime1 = view.findViewById(R.id.imageView10);
        viewHolder.setTime2 = view.findViewById(R.id.imageView11);
        viewHolder.checkBox1 = view.findViewById(R.id.checkBox4);
        viewHolder.checkBox2 = view.findViewById(R.id.checkBox5);
        // Set tag
        view.setTag(viewHolder);
        viewHolder.checkBox1.setTag(list.get(position));
        viewHolder.checkBox2.setTag(list.get(position));

        // Get view holder from the tag
        ViewHolder holder = (ViewHolder) view.getTag();

        // Set the behavior for all components based on the user's role
        if (isStudent) {
            studentSetting(holder, position);
        }
        else {
            lecturerSetting(holder, position);
        }
        return view;
    }

    /**
     * Setting for lecturer role
     * @param holder ViewHolder
     * @param position int
     */
    @SuppressLint("SetTextI18n")
    private void lecturerSetting(ViewHolder holder, int position){
        // Set behavior for the components based on the lecturer role
        holder.checkBox1.setEnabled(false);
        holder.checkBox1.setVisibility(View.INVISIBLE);
        holder.checkBox2.setEnabled(false);
        holder.checkBox2.setVisibility(View.INVISIBLE);
        // Set information about 2 groups
        holder.id.setText(String.valueOf(position + 1));
        holder.name.setText(list.get(position).getCourseName());
        holder.group1.setText("Time: " + list.get(position).getTime1() + ":00 -> " + (Integer.parseInt(list.get(position).getTime1()) + 3) + ":00" + " (" + convertDay(list.get(position).getDay1()) + ")");
        holder.group2.setText("Time: " + list.get(position).getTime2() + ":00 -> " + (Integer.parseInt(list.get(position).getTime2()) + 3) + ":00" + " (" + convertDay(list.get(position).getDay2()) + ")");

        // Change time on click
        setTimeOnClickListener(holder.setTime1, position, true);
        setTimeOnClickListener(holder.setTime2, position, false);
    }

    /**
     * Set change time on click
     * @param setTime ImageView
     * @param position int
     * @param isFirstGroup boolean
     */
    private void setTimeOnClickListener(ImageView setTime, int position, boolean isFirstGroup){
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an alert dialog
                final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                View view = context.getLayoutInflater().inflate(R.layout.dialog_change_time, null);
                dialog.setView(view);
                final AlertDialog alert = dialog.create();
                // Setup all the needed components
                Button confirm = view.findViewById(R.id.button9);
                Spinner spinner = view.findViewById(R.id.spinner2);
                Spinner spinner1 = view.findViewById(R.id.spinner3);
                // Setup the spinner
                setSpinner(spinner, view, false);
                setSpinner(spinner1,view, true);
                // Get time and date of chosen group
                String getTime;
                String getDay;
                if (isFirstGroup){
                    getTime =list.get(position).getTime1();
                    getDay = list.get(position).getDay1();
                }
                else {
                    getDay = list.get(position).getDay2();
                    getTime = list.get(position).getTime2();
                }
                // confirm change on click
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!revert(spinner.getSelectedItem().toString(), false).equals(getTime)
                                || !revert(spinner1.getSelectedItem().toString(), true).equals(getDay)){    // If the new change is different from the old one
                            firebaseHandler.getProgram(program).collection("data").document(list.get(position).getCourseName()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    // Get the data from firebase
                                    ArrayList<String> day = (ArrayList<String>) task.getResult().get("day");
                                    ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                                    // Check whether it is the first group or second group in firebase
                                    // Then remove replace the chosen one by the new data
                                    if (day.get(0).equals(getDay) && time.get(0).equals(getTime)){
                                        day.remove(0);
                                        time.remove(0);
                                        day.add(0, revert(spinner1.getSelectedItem().toString(), true));
                                        time.add(0, revert(spinner.getSelectedItem().toString(), false));
                                    }
                                    else if (day.get(1).equals(getDay) && time.get(1).equals(getTime)){
                                        day.remove(1);
                                        time.remove(1);
                                        day.add(1, revert(spinner1.getSelectedItem().toString(), true));
                                        time.add(1, revert(spinner.getSelectedItem().toString(), false));
                                    }
                                    // Update new data to the firebase
                                    task.getResult().getReference().update("day", day);
                                    task.getResult().getReference().update("time", time);
                                    task.getResult().getReference().collection("data").document(list.get(position).getCourseName()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            String isChange = task.getResult().getString("change");
                                            if (isChange.equals("0")) isChange = "1";
                                            else isChange = "0";
                                            task.getResult().getReference().update("change", isChange);
                                        }
                                    });
                                    // Refresh the page to get the new data
                                    Toast.makeText(context, "Change successful! Reload the page!", Toast.LENGTH_SHORT).show();
                                    alert.dismiss();
                                    context.recreate();
                                }
                            });
                        }
                        else Toast.makeText(context, "There is no change! Failure!", Toast.LENGTH_SHORT).show();   // If the new change is the same with the old one
                    }
                });
                alert.show();
            }
        });
    }

    /**
     * Setting for student role
     * @param holder ViewHolder
     * @param position int
     */
    @SuppressLint("SetTextI18n")
    private void studentSetting(ViewHolder holder, int position){
        // Set the information about 2 groups
        holder.id.setText(String.valueOf(position + 1));
        holder.name.setText(list.get(position).getCourseName());
        holder.group1.setText("- Lecturer: " + list.get(position).getLecturer() + "\n- Time: " + list.get(position).getTime1() + ":00 -> " + (Integer.parseInt(list.get(position).getTime1()) + 3) + ":00" + " (" + convertDay(list.get(position).getDay1()) + ")");
        holder.group2.setText("- Lecturer: " + list.get(position).getLecturer() + "\n- Time: " + list.get(position).getTime2() + ":00 -> " + (Integer.parseInt(list.get(position).getTime2()) + 3) + ":00" + " (" + convertDay(list.get(position).getDay2()) + ")");
        holder.checkBox1.setChecked(list.get(position).isGroup1());
        holder.checkBox2.setChecked(list.get(position).isGroup2());
        holder.setTime1.setEnabled(false);
        holder.setTime1.setVisibility(View.INVISIBLE);
        holder.setTime2.setEnabled(false);
        holder.setTime2.setVisibility(View.INVISIBLE);
        // Set the check boxes listener
        checkBoxListeners(holder);
    }

    /**
     * Check box listener
     * @param holder ViewHolder
     */
    private void checkBoxListeners(ViewHolder holder){
        holder.checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                Group element = (Group) holder.checkBox1.getTag();
                element.setGroup1(buttonView.isChecked());
                if (buttonView.isChecked()) {               // Allow the user to select 1 out of 2 only
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
                if (buttonView.isChecked()) {               // Allow the user to select 1 out of 2 only
                    element.setGroup1(false);
                    holder.checkBox1.setChecked(false);
                }
            }
        });
    }

    /**
     * Revert back from full name of the day to the keyword
     * @param string String
     * @param isDate boolean
     * @return String
     */
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

    /**
     * Setup the Spinner
     * @param spinner Spinner
     * @param view View
     * @param isDay boolean
     */
    private void setSpinner(Spinner spinner, View view, boolean isDay){
        ArrayList<String> spinnerTime = new ArrayList<>();
        if (isDay){                 // If there is the spinner for selecting week-day
            spinnerTime.add("Monday");
            spinnerTime.add("Tuesday");
            spinnerTime.add("Wednesday");
            spinnerTime.add("Thursday");
            spinnerTime.add("Friday");
        }
        else {                     // If there is the spinner for selecting time
            for (int i = 7; i< 15; i++){
                spinnerTime.add(i+":00");
            }
        }
        // Set adapter to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),android.R.layout.simple_spinner_item, spinnerTime);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(null);
        spinner.setAdapter(adapter);
    }

    /**
     * Convert the keyword to the full name of the day
     * @param day String
     * @return String
     */
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
