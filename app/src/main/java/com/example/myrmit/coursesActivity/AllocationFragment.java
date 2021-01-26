package com.example.myrmit.coursesActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.example.myrmit.R;
import com.example.myrmit.model.*;
import com.example.myrmit.model.arrayAdapter.GroupArrayAdapter;
import com.example.myrmit.model.objects.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

/**
 * Allocation page
 *      - Student: choose their time to learn a specific progressing course
 *      - Lecturer: view and change the time of a specific progressing course
 *              * progressing course of lecturer based on the progressing courses of student
 */
public class AllocationFragment extends Fragment {
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    private ListView listView;
    private ArrayList<Group> groups;
    ImageView nothing;
    private Button confirm;
    private ImageView loading;
    private View view;
    private final String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    public AllocationFragment() {
    }

    /**
     * On create function
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * On create View function
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initialSetting(inflater, container);
        return view;
    }

    /**
     * Set up all the stuffs and components at the beginning
     * @param inflater LayoutInflater
     * @param container ViewGroup
     */
    private void initialSetting(LayoutInflater inflater, ViewGroup container){
        view = inflater.inflate(R.layout.allocation_fragment, container, false);
        confirm = view.findViewById(R.id.button3);
        loading = view.findViewById(R.id.imageView12);
        listView = view.findViewById(R.id.group);
        nothing = view.findViewById(R.id.imageView13);
        setConfirmClick();
        setList();
    }

    /**
     * setup for the confirm button
     */
    private void setConfirmClick() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm(v);
            }
        });
    }

    /**
     * To check if there is a change in allocation
     * @param task Task<QuerySnapshot>
     * @return boolean
     */
    private boolean isChange(Task<QuerySnapshot> task){
        boolean isChange = false;

        // Get data from firebase and compare with current data
        for (DocumentSnapshot documentSnapshot : task.getResult()){
            for (int i = 0; i < groups.size(); i++){
                if (documentSnapshot.getId().equals(groups.get(i).getCourseName())) {
                    if (groups.get(i).isGroup1()) {
                        isChange = !documentSnapshot.get("day").equals(groups.get(i).getDay1());
                        break;
                    } else if (groups.get(i).isGroup2()) {
                        isChange = !documentSnapshot.get("day").equals(groups.get(i).getDay2());
                        break;
                    } else {
                        isChange = !documentSnapshot.get("day").equals("");
                        break;
                    }
                }
            }
            if (isChange){
                break;
            }
        }
        return isChange;
    }

    /**
     * If there is a change, the system will store it to the hash map
     * @param date HashMap<String, HashMap<String, String>>
     */
    private void storeChanges(HashMap<String, HashMap<String, String>> date){
        for (int i = 0 ; i < groups.size(); i++){
            if (!getChosenGroupDay(groups.get(i)).equals("")){
                if (!date.containsKey(getChosenGroupDay(groups.get(i))) ) {
                    HashMap<String, String> chosenTime = new HashMap<String, String>();
                    chosenTime.put(groups.get(i).getCourseName(), getChosenGroupTime(groups.get(i)));       // Store chosen time with the course name
                    date.put(getChosenGroupDay(groups.get(i)), chosenTime);                                 // Store the day with stored data above
                }
                else date.get(getChosenGroupDay(groups.get(i))).put(groups.get(i).getCourseName(), getChosenGroupTime(groups.get(i)));  // If the student did not choose any group to study
            }
            firebaseHandler.confirmAllocation(user, getChosenGroupDay(groups.get(i)), getChosenGroupTime(groups.get(i)), groups.get(i).getCourseName());   // Update the firebase based on the new data
        }
    }

    /**
     * With each new data, we also update to the timetable in firebase
     * @param date HashMap<String, HashMap<String, String>>
     */
    private void updateAllocation(HashMap<String, HashMap<String, String>> date){
        for (String day : date.keySet()){
            firebaseHandler.getCurrentCalendar(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    ArrayList<String> dates = (ArrayList<String>) task.getResult().get("date");
                    ArrayList<String> dateList = new ArrayList<>();
                    ArrayList<String> time = new ArrayList<>();
                    ArrayList<String> notes = new ArrayList<>();
                    for (String date : dates){
                        try {
                            if (isDate(day, date)){     // If there is a week-day the student selected
                                dateList.add(date);     // Add it
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    HashMap<String, String> map = date.get(day);
                    for (String d : map.keySet()){                  // Add a new note (reminder)
                        notes.add("Have a class: " + d + " !");
                        time.add(map.get(d));
                    }
                    firebaseHandler.addClassTime(user, dateList, time,notes);       // Update directly to the firebase
                }
            });
        }
    }

    /**
     * After the confirm button is clicked
     * @param v View
     */
    private void confirm(View v){
        firebaseHandler.getProgressingCourse(user).collection("data").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (isChange(task)){            // Check if there is a change, then update the new changes
                    firebaseHandler.updateTimetable(user);
                    HashMap<String, HashMap<String, String>> date = new HashMap<>();
                    storeChanges(date);
                    updateAllocation(date);

                    // Refresh the page and display the message to notify the user
                    setList();
                    Toast.makeText(v.getContext(), "Change Successful!", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(v.getContext(), "No Change!", Toast.LENGTH_SHORT).show();           // If there is no change which means that the same with the old one.
            }
        });
    }

    /**
     * Get the chosen day of each course
     * @param group Group
     * @return String
     */
    private String getChosenGroupDay(Group group){
        if (group.isGroup1()){
            return group.getDay1();
        }
        else if (group.isGroup2()){
            return group.getDay2();
        }
        return "";
    }

    /**
     * Get the chosen time of each course
     * @param group Group
     * @return String
     */
    private String getChosenGroupTime(Group group){
        if (group.isGroup1()){
            return group.getTime1();
        }
        else if (group.isGroup2()){
            return group.getTime2();
        }
        return "";
    }

    /**
     * Check if there is the week-day the student selected
     * @param dayOfWeek String
     * @param date String
     * @return boolean
     * @throws ParseException e
     */
    private boolean isDate(String dayOfWeek, String date) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(date));
        switch (dayOfWeek){
            case "mon": return cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;
            case "tue": return cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY;
            case "wed": return cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY;
            case "thu": return cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY;
            case "fri": return cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
            default: break;
        }
        return false;
    }

    /**
     * After handling all the data, finalize the list and finish loading
     * @param programID String
     * @param isStudent boolean
     * @param progressing ArrayList<String>
     */
    private void finalizeList(String programID, boolean isStudent, ArrayList<String> progressing){
        if (groups.size() == progressing.size()){
            try {
                ArrayAdapter<Group> adapter = new GroupArrayAdapter(getActivity(), groups, true, programID);
                listView.setAdapter(adapter);
                if (isStudent) {
                    confirm.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                    loading.setEnabled(false);
                }
                else confirm.setEnabled(false);
            }catch (Exception ignored){}
        }
    }

    /**
     * set the chosen group from the firebase to the list
     * @param d String
     * @param day ArrayList<String>
     * @param t String
     * @param time ArrayList<String>
     */
    private void setChosenGroup(String d, ArrayList<String> day, String t, ArrayList<String> time){
        if (d.equals(day.get(0)) &&  t.equals(time.get(0))){
            groups.get(groups.size()-1).setGroup1(true);
        }
        else if (d.equals(day.get(1))&& t.equals(time.get(1))){
            groups.get(groups.size()-1).setGroup2(true);
        }
    }

    /**
     * Set up the allocation by course name
     * This is for the student only
     * @param course String
     * @param programID String
     * @param courseProgressingTask Task<DocumentSnapshot>
     * @param progressing ArrayList<String>
     * @param lecturer String[]
     */
    private void studentSetAllocationByCourse(String course, String programID, Task<DocumentSnapshot> courseProgressingTask, ArrayList<String> progressing, String[] lecturer){
        firebaseHandler.getProgram(programID).collection("data").document(course).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {                      // Get day and time from firebase (chosen time and day of the user)
                ArrayList<String> day = (ArrayList<String>) task.getResult().get("day");
                ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                courseProgressingTask.getResult().getReference().collection("data").document(course).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {             // Get all groups (time and day of the courses)
                        String d = (String) task.getResult().get("day");
                        String t = (String) task.getResult().get("time");
                        if (lecturer[0].equals("")) {                                           // If the lecturer is not found yet
                            firebaseHandler.getAllAccounts().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                        if (documentSnapshot.get("role").equals("lecturer")) {
                                            firebaseHandler.getProgramOfUser(documentSnapshot.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.getResult().get("code").equals(programID)) {               // Found the lecturer
                                                        lecturer[0] = (String) documentSnapshot.get("name");            // Store name
                                                        groups.add(get(day.get(0), time.get(0), day.get(1), time.get(1), course, lecturer[0])); // Add the chosen group
                                                        setChosenGroup(d, day, t, time);                                // Set the day and time the student has chosen before
                                                        finalizeList(programID, true, progressing);             // Finalize
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                        else {              // If we already find the lecture's name from the previous course, then use it
                                            // Store and finalize the list
                            groups.add(get(day.get(0), time.get(0), day.get(1), time.get(1), course, lecturer[0]));
                            setChosenGroup(d,day,t,time);
                            finalizeList(programID, true, progressing);
                        }
                    }
                });
            }
        });
    }

    /**
     * If there is the student account, set up by this way
     */
    private void studentSetList(){
        firebaseHandler.getProgramOfUser(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String programID = (String) task.getResult().get("code");                // Get the programID
                firebaseHandler.getProgressingCourse(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> courseProgressingTask) {
                        ArrayList<String> progressing = (ArrayList<String>) courseProgressingTask.getResult().get("list");
                        emptyCondition(progressing);                // If there is no progressing courses
                        String[] lecturer = {""};
                        for (String course : progressing) {         // Otherwise, Setup the list
                            studentSetAllocationByCourse(course, programID, courseProgressingTask, progressing, lecturer);
                        }
                    }
                });
            }
        });

    }

    /**
     * If there is no progressing course
     * @param list ArrayList<String>
     */
    private void emptyCondition(ArrayList<String> list){
        if (list.size() == 0 || list == null){
            nothing.setVisibility(View.VISIBLE);
            loading.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Set up the timetable for lecturer after getting all data
     * @param weekDay ArrayList<String>
     * @param timeDay ArrayList<String>
     * @param noteDay ArrayList<String>
     */
    private void setTimeTableForLecturer(ArrayList<String> weekDay, ArrayList<String> timeDay, ArrayList<String> noteDay){
        firebaseHandler.getCurrentCalendar(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> dates = (ArrayList<String>) task.getResult().get("date");
                for (String date : dates){
                    ArrayList<String> days = new ArrayList<>();
                    ArrayList<String> time = new ArrayList<>();
                    ArrayList<String> notes = new ArrayList<>();
                    for (int i = 0; i < weekDay.size(); i++) {              // Store all the class time to the timetable
                        try {
                            if (isDate(weekDay.get(i), date)) {
                                if (days.size() == 0) {
                                    days.add(date);
                                }
                                time.add(timeDay.get(i));
                                notes.add(noteDay.get(i));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    firebaseHandler.addClassTime(user, days, time,notes);       // Update the timetable if there is any change
                }
            }
        });
    }

    /**
     * If there is the lecture account, setup the list by this way
     */
    private void lecturerSetList(){
        firebaseHandler.getProgressingCourse(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> list = (ArrayList<String>) task.getResult().get("list");
                emptyCondition(list);
                firebaseHandler.getProgramOfUser(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {              // Get program ID
                        String programCode = (String) task.getResult().get("code");
                        ArrayList<String> weekDay = new ArrayList<>();
                        ArrayList<String> timeDay = new ArrayList<>();
                        ArrayList<String> noteDay = new ArrayList<>();
                        for (String course: list){                                          // Setup list for allocation page and update timetable based on progressing course
                            firebaseHandler.getProgram(programCode).collection("data").document(course).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    // Add data
                                    ArrayList<String> day = (ArrayList<String>) task.getResult().get("day");
                                    ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                                    weekDay.addAll(day);
                                    timeDay.addAll(time);
                                    noteDay.add("Have a class: " + course +" !");
                                    noteDay.add("Have a class: " + course +" !");
                                    groups.add(get(day.get(0), time.get(0), day.get(1), time.get(1), course, ""));
                                    // Finalize list
                                    finalizeList(programCode, false,list);
                                    if (groups.size() == list.size()){
                                        // Update time table for lecturer
                                        setTimeTableForLecturer(weekDay,timeDay,noteDay);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    /**
     * Reset to reload the list
     */
    private void resetList(){
        groups = new ArrayList<>();
        listView.setAdapter(null);
        confirm.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
    }

    /**
     * Set list for allocation page
     */
    private void setList() {
        resetList();
        firebaseHandler.getAccount(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {      // Get role of the account first
                String role = (String) task.getResult().get("role");
                // Then set list based on the user's role
                if (role.equals("student")){
                    studentSetList();
                }
                else {
                    firebaseHandler.updateTimetable(user);
                    lecturerSetList();
                }
            }
        });
    }

    /**
     * Get group object by given parameters
     * @param day1 String
     * @param time1 String
     * @param day2  String
     * @param time2 String
     * @param courseName String
     * @param lecturerName String
     * @return Group
     */
    private Group get(String day1, String time1, String day2, String time2, String courseName, String lecturerName) {
        return new Group(lecturerName, day1, time1, courseName, day2, time2);
    }
}