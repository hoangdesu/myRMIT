package com.example.myrmit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private Button confirm;
    public AllocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        listView = view.findViewById(R.id.group);
        confirmChange();
        setList();
        return view;
    }

    private void confirmChange() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseHandler.getProgressingCode("s3740819@rmit.edu.vn").collection("data").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean isChange = false;
                        firebaseHandler.updateTimetable("s3740819@rmit.edu.vn");
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
                                firebaseHandler.confirmAllocation("s3740819@rmit.edu.vn", getChosenGroupDay(groups.get(i)), getChosenGroupTime(groups.get(i)), groups.get(i).getCourseName());
                            }
                            for (String day : date.keySet()){
                                firebaseHandler.getCurrentCalendar("s3740819@rmit.edu.vn").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        ArrayList<String> dates = (ArrayList<String>) task.getResult().get("date");
                                        ArrayList<String> dateList = new ArrayList<>();
                                        ArrayList<String> time = new ArrayList<>();
                                        ArrayList<String> courseName = new ArrayList<>();
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
                                            courseName.add("Have a class: " + d + " !");
                                            time.add(map.get(d));
                                        }
                                        firebaseHandler.addClassTime("s3740819@rmit.edu.vn", dateList, time,courseName);
                                    }
                                });
                            }
                            setList();
                            Toast.makeText(v.getContext(), "Change Successful!", Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(v.getContext(), "No Change!", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void setList() {
        groups = new ArrayList<>();
        listView.setAdapter(null);
        confirm.setVisibility(View.INVISIBLE);
        firebaseHandler.getProgramOfStudent("s3740819@rmit.edu.vn").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String code = (String) task.getResult().get("code");
                firebaseHandler.getProgressingCode("s3740819@rmit.edu.vn").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> taskk) {
                        ArrayList<String> progressing = (ArrayList<String>) taskk.getResult().get("list");
                        ArrayList<Group> groups = new ArrayList<>();
                        for (String course : progressing) {
                            firebaseHandler.getProgram(code).collection("data").document(course).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    ArrayList<String> day = (ArrayList<String>) task.getResult().get("day");
                                    ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                                    taskk.getResult().getReference().collection("data").document(course).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            groups.add(get(day.get(0), time.get(0), day.get(1), time.get(1), course));
                                            String d = (String) task.getResult().get("day");
                                            String t = (String) task.getResult().get("time");
                                            if (d.equals(day.get(0)) &&  t.equals(time.get(0))){
                                                groups.get(groups.size()-1).setGroup1(true);
                                            }
                                            else if (d.equals(day.get(1))&& t.equals(time.get(1))){
                                                groups.get(groups.size()-1).setGroup2(true);
                                            }
                                            if (groups.size() == progressing.size()){
                                                try {
                                                    ArrayAdapter<Group> adapter = new GroupArrayAdapter(getActivity(), groups);
                                                    listView.setAdapter(adapter);
                                                    confirm.setVisibility(View.VISIBLE);
                                                }catch (Exception ignored){}

                                            }
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

    private Group get(String day1, String time1, String day2, String time2, String courseName) {
        Group group = new Group("Minh Dinh", day1, time1, courseName, day2, time2);
        groups.add(group);
        return group;
    }
}