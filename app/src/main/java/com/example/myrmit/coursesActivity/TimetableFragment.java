package com.example.myrmit.coursesActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

import com.example.myrmit.R;
import com.example.myrmit.model.*;
import com.example.myrmit.model.arrayAdapter.TimelineArrayAdapter;
import com.example.myrmit.model.objects.Note;
import com.example.myrmit.model.objects.Timeline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class TimetableFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy");
    private ListView listView;
    private Button add;
    private HorizontalCalendar[] calendar;
    private ImageView today;
    private String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private ImageView nothing;
    ArrayList<String> spinnerTime = new ArrayList<>();
    public TimetableFragment() {
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
        View view = inflater.inflate(R.layout.timetable_fragment, container, false);
        listView = view.findViewById(R.id.timelist);
        add = view.findViewById(R.id.button4);
        today = view.findViewById(R.id.imageView7);
        nothing = view.findViewById(R.id.imageView6);
        calendar = new HorizontalCalendar[]{new HorizontalCalendar.Builder(view.getRootView(), R.id.calendarView)
                .datesNumberOnScreen(5)
                .range(Calendar.getInstance(), Calendar.getInstance())
                .configure().textSize(12, 12, 14).colorTextBottom(Color.YELLOW, Color.GREEN).end()
                .build()};
        firebaseHandler.getCurrentSemester().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String sem = (String) task.getResult().get("semester");
                final Date[] date = new Date[1];
                if (sem.split(",")[0].equals("feb")) {
                    date[0] = new GregorianCalendar(Integer.parseInt(sem.split(", ")[1]), Calendar.FEBRUARY, 20).getTime();//the year field adds 1900 on to it.
                }
                else if (sem.split(",")[0].equals("jun")){
                    date[0] = new GregorianCalendar(Integer.parseInt(sem.split(", ")[1]), Calendar.JUNE, 20).getTime();//the year field adds 1900 on to it.
                }
                else date[0] = new GregorianCalendar(Integer.parseInt(sem.split(", ")[1]), Calendar.OCTOBER, 20).getTime();//the year field adds 1900 on to it.
                Calendar startDate = Calendar.getInstance();
                startDate.setTime(date[0]);
                startDate.add(Calendar.MONTH, 0);
                Calendar endDate =  Calendar.getInstance();
                endDate.setTime(date[0]);
                endDate.add(Calendar.MONTH, 4);
                calendar[0].refresh();
                calendar[0].setRange(startDate,endDate);

                ////////////////////////////////////////////////////////////////
                firebaseHandler.getCurrentCalendar(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ArrayList<String> dates = (ArrayList<String>) task.getResult().get("date");
                        if (dates == null || dates.size() == 0){
                            dates = new ArrayList<>();
                            while( date[0].before(calendar[0].getDateAt(calendar[0].positionOfDate(endDate)).getTime())){
                                dates.add(sdf.format(date[0]));
                                date[0] = addDays(date[0]);
                            }
                            task.getResult().getReference().update("date", dates);
                            task.getResult().getReference().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    ArrayList<String> dates = (ArrayList<String>) task.getResult().get("date");
                                    for (String date : dates) {
                                        task.getResult().getReference().collection("data").document(date).set(new Note());
                                    }
                                }
                            });
                        }
                        calendar[0].goToday(true);
                    }
                });
                /////////////////////////////////////////////////////////////////
                add();
            }
        });
        calendar[0].setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                setList(sdf.format(date.getTime()));
            }
        });
        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar[0].goToday(true);
            }
        });
        return view;
    }

    private void add(){
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressLint("InflateParams")
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                View view = getLayoutInflater().inflate(R.layout.dialog_timeline, null);
                dialog.setView(view);
                final AlertDialog alert = dialog.create();
                Button add = view.findViewById(R.id.button5);
                TextView day = view.findViewById(R.id.day);
                Button cancel = view.findViewById(R.id.button7);
                Spinner spinner = view.findViewById(R.id.spinner);
                day.setText(sdf.format(calendar[0].getSelectedDate().getTime()));
                EditText note = view.findViewById(R.id.add_note);
                note.addTextChangedListener(new TextWatcher() {
                    private String text;

                    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    }

                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                        text = arg0.toString();
                    }

                    public void afterTextChanged(Editable arg0) {
                        int lineCount = note.getLineCount();
                        if(lineCount > 17){
                            note.setText(text);
                        }
                    }
                });
                setSpinnerTime(spinner, view);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!spinner.getSelectedItem().toString().equals("Time") && !note.getText().toString().equals("")) {
                            firebaseHandler.getCurrentCalendar(user).collection("data").document(sdf.format(calendar[0].getSelectedDate().getTime())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                                    ArrayList<String> noteList = (ArrayList<String>) task.getResult().get("note");
                                    ArrayList<String> type = (ArrayList<String>) task.getResult().get("type");
                                    time.add(spinner.getSelectedItem().toString().split(":")[0]);
                                    noteList.add(note.getText().toString());
                                    type.add("note");
                                    task.getResult().getReference().update("time", time);
                                    task.getResult().getReference().update("note", noteList);
                                    task.getResult().getReference().update("type", type);
                                    setList(sdf.format(calendar[0].getSelectedDate().getTime()));
                                    alert.dismiss();
                                }
                            });
                        }
                        else if (spinner.getSelectedItem().toString().equals("Time")) Toast.makeText(view.getContext(), "Please choose the time!" , Toast.LENGTH_SHORT).show();
                        else if (note.getText().toString().equals("")) Toast.makeText(view.getContext(), "There is empty note!" , Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
        });
    }

    private void setSpinnerTime(Spinner spinner, View view){
        firebaseHandler.getCurrentCalendar(user).collection("data").document(sdf.format(calendar[0].getSelectedDate().getTime())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                ArrayList<String> type = (ArrayList<String>) task.getResult().get("type");
                spinnerTime.clear();
                spinnerTime.add("Time");
                for (int i = 0; i< 24; i++){
                    spinnerTime.add(String.valueOf(i) + ":00");
                }
                assert type != null;
                for (int i = 0; i < Objects.requireNonNull(time).size(); i++){
                    if (!type.get(i).equals("class")){
                        spinnerTime.remove((time.get(i)+":00"));
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),android.R.layout.simple_spinner_item, spinnerTime);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(null);
                spinner.setAdapter(adapter);
            }
        });
    }

    private void setList(String date) {
        nothing.setVisibility(View.VISIBLE);
        listView.setAdapter(null);
        firebaseHandler.getDateData(date, user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                assert time != null;
                if (time.size() != 0){
                    ArrayList<Timeline> timelineArrayList = new ArrayList<>();
                    ArrayList<String> note = (ArrayList<String>) task.getResult().get("note");
                    ArrayList<String> type = (ArrayList<String>) task.getResult().get("type");
                    assert note != null;
                    assert type != null;
                    for (int i = 0; i< time.size(); i++){
                        timelineArrayList.add(get(time.get(i), note.get(i), type.get(i)));
                    }
                    sortTimeline(timelineArrayList);
                    ArrayAdapter<Timeline> adapter = new TimelineArrayAdapter(getActivity(), timelineArrayList);
                    listView.setAdapter(adapter);
                    nothing.setVisibility(View.INVISIBLE);
                    setListViewListener(timelineArrayList);
                }
            }
        });
    }

    private void setListViewListener(ArrayList<Timeline> timelines){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!timelines.get(position).getType().equals("class")){
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.Theme_AppCompat_Dialog_Alert);
                    View v = getLayoutInflater().inflate(R.layout.dialog_item_click, null);
                    dialog.setView(v);
                    final AlertDialog alert = dialog.create();
                    Button change = v.findViewById(R.id.change);
                    Button delete = v.findViewById(R.id.delete);
                    change.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog.Builder dialog1 = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                            View v1 = getLayoutInflater().inflate(R.layout.change_dialog, null);
                            dialog1.setView(v1);
                            final AlertDialog alert1 = dialog1.create();
                            Button confirm = v1.findViewById(R.id.button6);
                            Button cancel = v1.findViewById(R.id.button8);
                            EditText newNote = v1.findViewById(R.id.add_note2);
                            firebaseHandler.getCurrentCalendar(user)
                                    .collection("data").document(sdf.format(calendar[0].getSelectedDate().getTime())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                                    ArrayList<String> note = (ArrayList<String>) task.getResult().get("note");
                                    assert note != null;
                                    for (int i = 0; i < Objects.requireNonNull(time).size(); i++){
                                        if (time.get(i).equals(timelines.get(position).getTime())){
                                            newNote.setText(note.get(i));
                                            break;
                                        }
                                    }
                                }
                            });
                            newNote.addTextChangedListener(new TextWatcher() {
                                private String text;

                                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

                                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {text = arg0.toString();}

                                public void afterTextChanged(Editable arg0) {
                                    int lineCount = newNote.getLineCount();
                                    if(lineCount > 17){
                                        newNote.setText(text);
                                    }
                                }
                            });

                            confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (newNote.getText().toString().equals("")) {
                                        firebaseHandler.getCurrentCalendar(user)
                                                .collection("data").document(sdf.format(calendar[0].getSelectedDate().getTime())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                                                ArrayList<String> note = (ArrayList<String>) task.getResult().get("note");
                                                assert note != null;
                                                for (int i = 0; i < Objects.requireNonNull(time).size(); i++) {
                                                    if (time.get(i).equals(timelines.get(position).getTime())) {
                                                        note.remove(i);
                                                        note.add(i, newNote.getText().toString());
                                                        break;
                                                    }
                                                }
                                                task.getResult().getReference().update("note", note);
                                            }
                                        });
                                        setList(sdf.format(calendar[0].getSelectedDate().getTime()));
                                        alert1.dismiss();
                                    }
                                    else Toast.makeText(getContext(), "Empty Note!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert1.dismiss();
                                }
                            });
                            alert1.show();
                            alert.dismiss();
                        }
                    });
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            firebaseHandler.getCurrentCalendar(user)
                                    .collection("data").document(sdf.format(calendar[0].getSelectedDate().getTime())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                                    ArrayList<String> type = (ArrayList<String>) task.getResult().get("type");
                                    ArrayList<String> note = (ArrayList<String>) task.getResult().get("note");
                                    assert type != null;
                                    assert note != null;
                                    for (int i = 0; i < Objects.requireNonNull(time).size(); i++){
                                        if (time.get(i).equals(timelines.get(position).getTime()) && note.get(i).equals(timelines.get(position).getNote())
                                                && type.get(i).equals(timelines.get(position).getType())){
                                            time.remove(i);
                                            type.remove(i);
                                            note.remove(i);
                                            break;
                                        }
                                    }
                                    task.getResult().getReference().update("time", time);
                                    task.getResult().getReference().update("note", note);
                                    task.getResult().getReference().update("type", type);
                                    setList(sdf.format(calendar[0].getSelectedDate().getTime()));
                                    alert.dismiss();
                                }
                            });
                        }
                    });
                    alert.show();
                }
            }
        });
    }

    private void sortTimeline(ArrayList<Timeline> timelines){
        ArrayList<Timeline> sortedArray = new ArrayList<>();
        for (int i = 0; i <= 23; i++){
            for (int ii = 0; ii < timelines.size(); ii++){
                if (Integer.parseInt(timelines.get(ii).getTime()) == i){
                    sortedArray.add(timelines.get(ii));
                    timelines.remove(ii);
                    ii--;
                }
            }
        }
        timelines.clear();
        for (int i = 0; i< sortedArray.size(); i++){
            if (sortedArray.get(i).getType().equals("class")){
                timelines.add(sortedArray.get(i));
                sortedArray.remove(i);
                i--;
            }
        }
        timelines.addAll(sortedArray);

    }

    private Timeline get(String time, String note, String type) {
        return new Timeline(time,note, type);
    }

    private static Date addDays(Date d1) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d1);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }
}