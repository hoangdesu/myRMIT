package com.example.myrmit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.media.Image;
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

import com.example.myrmit.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AllocationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView listView;
    private ArrayList<Group> groups;
    ImageView nothing;
    private Button confirm;
    private ImageView loading;
    private String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    public AllocationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.allocation_fragment, container, false);
        confirm = view.findViewById(R.id.button3);
        loading = view.findViewById(R.id.imageView12);
        listView = view.findViewById(R.id.group);
        nothing = view.findViewById(R.id.imageView13);
        confirmChange();
        setList(view);
        return view;
    }

    private void confirmChange() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm(v);
            }
        });
    }

    private void confirm(View v){
        firebaseHandler.getProgressingCourse(user).collection("data").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean isChange = false;
                firebaseHandler.updateTimetable(user);
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
                if (isChange){
                    HashMap<String, HashMap<String, String>> date = new HashMap<>();
                    for (int i = 0 ; i < groups.size(); i++){
                        if (!getChosenGroupDay(groups.get(i)).equals("")){
                            if (!date.containsKey(getChosenGroupDay(groups.get(i))) ) {
                                HashMap<String, String> chosenTime = new HashMap<String, String>();
                                chosenTime.put(groups.get(i).getCourseName(), getChosenGroupTime(groups.get(i)));
                                date.put(getChosenGroupDay(groups.get(i)), chosenTime);
                            }
                            else date.get(getChosenGroupDay(groups.get(i))).put(groups.get(i).getCourseName(), getChosenGroupTime(groups.get(i)));
                        }
                        firebaseHandler.confirmAllocation(user, getChosenGroupDay(groups.get(i)), getChosenGroupTime(groups.get(i)), groups.get(i).getCourseName());
                    }
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
                                        if (isDate(day, date)){
                                            dateList.add(date);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                HashMap<String, String> map = date.get(day);
                                for (String d : map.keySet()){
                                    notes.add("Have a class: " + d + " !");
                                    time.add(map.get(d));
                                }
                                firebaseHandler.addClassTime(user, dateList, time,notes);
                            }
                        });
                    }
                    setList(v);
                    Toast.makeText(v.getContext(), "Change Successful!", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(v.getContext(), "No Change!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getChosenGroupDay(Group group){
        if (group.isGroup1()){
            return group.getDay1();
        }
        else if (group.isGroup2()){
            return group.getDay2();
        }
        return "";
    }
    private String getChosenGroupTime(Group group){
        if (group.isGroup1()){
            return group.getTime1();
        }
        else if (group.isGroup2()){
            return group.getTime2();
        }
        return "";
    }

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

    private void setList(View v) {
        groups = new ArrayList<>();
        listView.setAdapter(null);
        confirm.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
        firebaseHandler.getAccount(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String role = (String) task.getResult().get("role");
                if (role.equals("student")){
                    firebaseHandler.getProgramOfStudent(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String code = (String) task.getResult().get("code");
                            firebaseHandler.getProgressingCourse(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> taskk) {
                                    ArrayList<String> progressing = (ArrayList<String>) taskk.getResult().get("list");
                                    ArrayList<Group> groups = new ArrayList<>();
                                    if (progressing.size() == 0 || progressing == null){
                                        nothing.setVisibility(View.VISIBLE);
                                        loading.setVisibility(View.INVISIBLE);
                                    }
                                    for (String course : progressing) {
                                        firebaseHandler.getProgram(code).collection("data").document(course).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                ArrayList<String> day = (ArrayList<String>) task.getResult().get("day");
                                                ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                                                taskk.getResult().getReference().collection("data").document(course).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        String d = (String) task.getResult().get("day");
                                                        String t = (String) task.getResult().get("time");
                                                        firebaseHandler.getAllAccounts().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                for (DocumentSnapshot documentSnapshot:task.getResult()){
                                                                    if (documentSnapshot.get("role").equals("lecturer")) {
                                                                        firebaseHandler.getProgramOfStudent(documentSnapshot.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.getResult().get("code").equals(code)){
                                                                                    groups.add(get(day.get(0), time.get(0), day.get(1), time.get(1), course, (String) documentSnapshot.get("name")));
                                                                                    if (d.equals(day.get(0)) &&  t.equals(time.get(0))){
                                                                                        groups.get(groups.size()-1).setGroup1(true);
                                                                                    }
                                                                                    else if (d.equals(day.get(1))&& t.equals(time.get(1))){
                                                                                        groups.get(groups.size()-1).setGroup2(true);
                                                                                    }
                                                                                    else {
                                                                                        if (!d.equals("")&& !t.equals("")){
                                                                                            confirm(v);
                                                                                        }
                                                                                    }
                                                                                    if (groups.size() == progressing.size()){
                                                                                        try {
                                                                                            ArrayAdapter<Group> adapter = new GroupArrayAdapter(getActivity(), groups, true, code);
                                                                                            listView.setAdapter(adapter);
                                                                                            confirm.setVisibility(View.VISIBLE);
                                                                                            loading.setVisibility(View.INVISIBLE);
                                                                                            loading.setEnabled(false);
                                                                                        }catch (Exception ignored){}
                                                                                    }
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
                else {
                    firebaseHandler.updateTimetable(user);
                    firebaseHandler.getProgressingCourse(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            ArrayList<String> list = (ArrayList<String>) task.getResult().get("list");
                            ArrayList<Group> groups = new ArrayList<>();
                            if (list.size() == 0 || list == null){
                                nothing.setVisibility(View.VISIBLE);
                                loading.setVisibility(View.INVISIBLE);
                            }
                            firebaseHandler.getProgramOfStudent(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    String programCode = (String) task.getResult().get("code");
                                    ArrayList<String> weekDay = new ArrayList<>();
                                    ArrayList<String> timeDay = new ArrayList<>();
                                    ArrayList<String> noteDay = new ArrayList<>();
                                    for (String course: list){
                                        firebaseHandler.getProgram(programCode).collection("data").document(course).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                ArrayList<String> day = (ArrayList<String>) task.getResult().get("day");
                                                ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                                                weekDay.addAll(day);
                                                timeDay.addAll(time);
                                                noteDay.add("Have a class: " + course +" !");
                                                noteDay.add("Have a class: " + course +" !");
                                                groups.add(get(day.get(0), time.get(0), day.get(1), time.get(1), course, ""));
                                                if (groups.size() == list.size()){
                                                    firebaseHandler.getCurrentCalendar(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            ArrayList<String> dates = (ArrayList<String>) task.getResult().get("date");
                                                            for (String date : dates){
                                                                ArrayList<String> dateList = new ArrayList<>();
                                                                ArrayList<String> time = new ArrayList<>();
                                                                ArrayList<String> notes = new ArrayList<>();
                                                                for (int i = 0; i < weekDay.size(); i++) {
                                                                    try {
                                                                        if (isDate(weekDay.get(i), date)) {
                                                                            if (dateList.size() == 0) {
                                                                                dateList.add(date);
                                                                            }
                                                                            time.add(timeDay.get(i));
                                                                            notes.add(noteDay.get(i));
                                                                        }
                                                                    } catch (ParseException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                                firebaseHandler.addClassTime(user, dateList, time,notes);
                                                            }
                                                        }
                                                    });
                                                    try {
                                                        ArrayAdapter<Group> adapter = new GroupArrayAdapter(getActivity(), groups, false, programCode);
                                                        listView.setAdapter(adapter);
                                                        confirm.setEnabled(false);
                                                    }catch (Exception ignored){}
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private Group get(String day1, String time1, String day2, String time2, String courseName, String lecturerName) {
        Group group = new Group(lecturerName, day1, time1, courseName, day2, time2);
        groups.add(group);
        return group;
    }
}