package com.example.myrmit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.myrmit.model.Course;


import java.util.ArrayList;
import java.util.List;
import com.example.myrmit.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class OES_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView listView;
    private ImageView loading;
    public OES_Fragment() {
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
    private void setList() {
        firebaseHandler.getProgramOfStudent("s3740819@rmit.edu.vn").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                System.out.println((String)task.getResult().get("code"));
                firebaseHandler.getProgram((String)task.getResult().get("code")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ArrayList<String> courseList = (ArrayList<String>) task.getResult().get("courses");
                        ArrayList<String> feb = (ArrayList<String>) task.getResult().get("feb");
                        ArrayList<String> jun = (ArrayList<String>) task.getResult().get("jun");
                        ArrayList<String> nov = (ArrayList<String>) task.getResult().get("nov");
                        ArrayList<Boolean> isFeb = new ArrayList<>();
                        ArrayList<Boolean> isJun = new ArrayList<>();
                        ArrayList<Boolean> isNov = new ArrayList<>();
                        assert courseList != null;
                        List<Course> list = new ArrayList<Course>();
                        for (int i = 0 ; i < courseList.size(); i++){
                            list.add(get(courseList.get(i)));
                            isFeb.add(feb.get(i).equals("1"));
                            isJun.add(jun.get(i).equals("1"));
                            isNov.add(nov.get(i).equals("1"));
                        }
                        ArrayAdapter<Course> adapter = new com.example.myrmit.model.ArrayAdapter(getActivity(), list, isFeb, isJun, isNov);
                        listView.setAdapter(adapter);
                        loading.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }

    private Course get(String s) {
        return new Course(s);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.oes_fragment, container, false);
        loading = view.findViewById(R.id.imageView);
        listView = view.findViewById(R.id.listview);
        setList();
        return view;
    }
}