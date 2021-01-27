package com.example.myrmit.model.arrayAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myrmit.R;
import com.example.myrmit.model.objects.Course;
import com.example.myrmit.model.FirebaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CoursesArrayAdapter extends android.widget.ArrayAdapter<Course> {
    private final List<Course> list;
    private final Activity context;
    private final List<Boolean> isFeb;
    private final FirebaseHandler firebaseHandler = new FirebaseHandler();
    private final List<Boolean> isJun;
    private final boolean isStudent;
    private final ArrayList<Boolean> progressingCourse;
    private final List<Boolean> isOct;

    public CoursesArrayAdapter(Activity context, List<Course> list, List<Boolean> isFeb, List<Boolean> isJun, List<Boolean> isOct, ArrayList<Boolean> progressingCourse, boolean isStudent) {
        super(context, R.layout.course_list, list);
        this.context = context;
        this.list = list;
        this.isStudent = isStudent;
        this.isFeb = isFeb;
        this.progressingCourse = progressingCourse;
        this.isJun = isJun;
        this.isOct = isOct;
    }
    static class ViewHolder {
        protected TextView id;
        protected TextView name;
        protected CheckBox feb;
        protected TextView finish;
        protected CheckBox jun;
        protected CheckBox oct;
        protected TextView progressing;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = context.getLayoutInflater();
        View view = inflator.inflate(R.layout.course_list, null);
        final ViewHolder viewHolder = new ViewHolder();
        // Initial setting for all components
        viewHolder.id = (TextView) view.findViewById(R.id.id);
        viewHolder.name = (TextView) view.findViewById(R.id.description);
        viewHolder.oct = (CheckBox) view.findViewById(R.id.checkBox3);
        viewHolder.jun = (CheckBox) view.findViewById(R.id.checkBox2);
        viewHolder.feb = (CheckBox) view.findViewById(R.id.checkBox);
        viewHolder.finish = view.findViewById(R.id.imageView3);
        viewHolder.progressing = view.findViewById(R.id.imageView4);
        // Set Listener for checkboxs
        checkBoxListeners(viewHolder);
        // Set tag for view
        view.setTag(viewHolder);
        viewHolder.feb.setTag(list.get(position));
        viewHolder.oct.setTag(list.get(position));
        viewHolder.jun.setTag(list.get(position));
        // Get view holder from tag
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.id.setText(String.valueOf(position+1));
        // Set behavior of components based on given data
        if (!progressingCourse.get(position)) { // If this is not a progressing course
            // Set the available semester for each course
            if (!isFeb.get(position)) {
                holder.feb.setVisibility(View.INVISIBLE);
                holder.feb.setEnabled(false);
            } else {
                holder.feb.setVisibility(View.VISIBLE);
                holder.feb.setEnabled(true);
            }
            if (!isJun.get(position)) {
                holder.jun.setVisibility(View.INVISIBLE);
                holder.jun.setEnabled(false);
            } else {
                holder.jun.setVisibility(View.VISIBLE);
                holder.jun.setEnabled(true);
            }
            if (!isOct.get(position)) {
                holder.oct.setVisibility(View.INVISIBLE);
                holder.oct.setEnabled(false);
            } else {
                holder.oct.setVisibility(View.VISIBLE);
                holder.oct.setEnabled(true);
            }
            // Check whether there is the finished course or not
            if (!isFeb.get(position) && !isOct.get(position) && !isJun.get(position) && isStudent) {    // If there is the finished course
                holder.finish.setVisibility(View.VISIBLE);
            } else holder.finish.setVisibility(View.INVISIBLE);                                         // Else not
            holder.progressing.setVisibility(View.INVISIBLE);
        }
        else {                  // If this is a progressing course
            holder.feb.setVisibility(View.INVISIBLE);
            holder.feb.setEnabled(false);
            holder.jun.setVisibility(View.INVISIBLE);
            holder.jun.setEnabled(false);
            holder.oct.setVisibility(View.INVISIBLE);
            holder.oct.setEnabled(false);
            holder.progressing.setVisibility(View.VISIBLE);
        }
        // Check the role and set the check box following the role of the user
        firebaseHandler.getAccount(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String role = (String) task.getResult().get("role");
                assert role != null;
                if (!role.equals("student")){
                    holder.feb.setEnabled(false);
                    holder.jun.setEnabled(false);
                    holder.oct.setEnabled(false);
                }
                else {
                    holder.feb.setEnabled(true);
                    holder.jun.setEnabled(true);
                    holder.oct.setEnabled(true);
                }
            }
        });
        // Update status of the check boxes
        holder.name.setText(list.get(position).getName());
        holder.feb.setChecked(list.get(position).isFeb());
        holder.jun.setChecked(list.get(position).isJun());
        holder.oct.setChecked(list.get(position).isOct());
        return view;
    }

    /**
     * Set check boxs on click
     * @param viewHolder ViewHolder
     */
    private void checkBoxListeners(ViewHolder viewHolder){
        viewHolder.feb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Course element = (Course) viewHolder.feb.getTag();
                element.setFeb(buttonView.isChecked());
                if (buttonView.isChecked()) {           // Can only choose 1 out of 3
                    element.setOct(false);
                    element.setJun(false);
                    viewHolder.jun.setChecked(false);
                    viewHolder.oct.setChecked(false);

                }
            }
        });
        viewHolder.jun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Course element = (Course) viewHolder.jun.getTag();
                element.setJun(buttonView.isChecked());
                if (buttonView.isChecked()) {       // Can only choose 1 out of 3
                    element.setFeb(false);
                    element.setOct(false);
                    viewHolder.oct.setChecked(false);
                    viewHolder.feb.setChecked(false);

                }
            }
        });
        viewHolder.oct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Course element = (Course) viewHolder.oct.getTag();
                element.setOct(buttonView.isChecked());
                if (buttonView.isChecked()) {   // Can only choose 1 out of 3
                    viewHolder.jun.setChecked(false);
                    viewHolder.feb.setChecked(false);
                    element.setFeb(false);
                    element.setJun(false);

                }
            }
        });
    }

}
