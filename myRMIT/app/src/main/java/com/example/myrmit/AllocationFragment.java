package com.example.myrmit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myrmit.model.FirebaseHandler;
import com.example.myrmit.model.Group;
import com.example.myrmit.model.GroupArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

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
        confirmChange(view);
        setList();
        return view;
    }

    private void confirmChange(View view){
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < groups.size(); i++){
                    if (groups.get(i).isGroup1()){
                        firebaseHandler.confirmAllocation("s3740819@rmit.edu.vn", groups.get(i).getDay1(), groups.get(i).getTime1(), groups.get(i).getCourseName());
                    }
                    else if (groups.get(i).isGroup2()){
                        firebaseHandler.confirmAllocation("s3740819@rmit.edu.vn", groups.get(i).getDay2(), groups.get(i).getTime2(), groups.get(i).getCourseName());
                    }
                    else firebaseHandler.confirmAllocation("s3740819@rmit.edu.vn", "", "", groups.get(i).getCourseName());
                }
                setList();
            }
        });
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
                                                ArrayAdapter<Group> adapter = new GroupArrayAdapter(getActivity(), groups);
                                                listView.setAdapter(adapter);
                                                confirm.setVisibility(View.VISIBLE);
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