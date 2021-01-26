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

import org.jetbrains.annotations.NotNull;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class TimetableFragment extends Fragment {
    private final FirebaseHandler firebaseHandler = new FirebaseHandler();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy");
    private ListView listView;
    private Button add;
    private View view;
    private HorizontalCalendar[] calendar;
    private ImageView today;
    private final String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private ImageView nothing;
    private final ArrayList<String> spinnerTime = new ArrayList<>();
    public TimetableFragment() {}

    /**
     * On Create function
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * On Create View function
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initialSetting(inflater, container);
        add.setVisibility(View.INVISIBLE);
        firebaseHandler.getCurrentSemester().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String sem = (String) task.getResult().get("semester");
                Calendar endDate =  Calendar.getInstance();
                final Date[] date = new Date[1];
                setCalendar(sem, date, endDate);
                setNewCalendar(date, endDate);
            }
        });
        return view;
    }

    /**
     * Set up all the components at the beginning
     * @param inflater LayoutInflater
     * @param container ViewGroup
     */
    private void initialSetting(LayoutInflater inflater, ViewGroup container){
        view = inflater.inflate(R.layout.timetable_fragment, container, false);
        listView = view.findViewById(R.id.timelist);
        add = view.findViewById(R.id.button4);
        today = view.findViewById(R.id.imageView7);
        nothing = view.findViewById(R.id.imageView6);
        // Build the calendar
        calendar = new HorizontalCalendar[]{new HorizontalCalendar.Builder(view.getRootView(), R.id.calendarView)
                .datesNumberOnScreen(5)
                .range(Calendar.getInstance(), Calendar.getInstance())
                .configure().textSize(12, 12, 14).colorTextBottom(Color.YELLOW, Color.GREEN).end()
                .build()};
    }

    /**
     * Set calendar based on the current semester
     * @param semester String
     * @param date Date[]
     * @param endDate Calendar
     */
    private void setCalendar(String semester, Date[] date, Calendar endDate){
        // Set the start date based on the current semester
        if (semester.split(",")[0].equals("feb")) {
            date[0] = new GregorianCalendar(Integer.parseInt(semester.split(", ")[1]), Calendar.FEBRUARY, 20).getTime();//the year field adds 1900 on to it.
        }
        else if (semester.split(",")[0].equals("jun")){
            date[0] = new GregorianCalendar(Integer.parseInt(semester.split(", ")[1]), Calendar.JUNE, 20).getTime();//the year field adds 1900 on to it.
        }
        else date[0] = new GregorianCalendar(Integer.parseInt(semester.split(", ")[1]), Calendar.OCTOBER, 20).getTime();//the year field adds 1900 on to it.
        // Store the start date
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(date[0]);
        startDate.add(Calendar.MONTH, 0);
        // End date will be the date after the start date 4 months later
        endDate.setTime(date[0]);
        endDate.add(Calendar.MONTH, 4);
        // Refresh the calendar and set range for it
        calendar[0].refresh();
        calendar[0].setRange(startDate,endDate);
    }

    /**
     * Set a new calendar if this is a new student/lecturer
     * @param date Date[]
     * @param endDate Calendar
     */
    private void setNewCalendar(Date[] date, Calendar endDate){
        firebaseHandler.getCurrentCalendar(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> dates = (ArrayList<String>) task.getResult().get("date");
                if (dates == null || dates.size() == 0){        // If this is a new user
                    dates = new ArrayList<>();
                    // Setup and build the data structure to the firebase
                    while( date[0].before(calendar[0].getDateAt(calendar[0].positionOfDate(endDate)).getTime())){
                        dates.add(sdf.format(date[0]));
                        date[0] = addDays(date[0]);
                    }
                    task.getResult().getReference().update("date", dates);
                    task.getResult().getReference().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            ArrayList<String> dates = (ArrayList<String>) task.getResult().get("date");
                            for (String date : dates) {                 // Keep setting up the data structure for note in calendar
                                task.getResult().getReference().collection("data").document(date).set(new Note());
                            }
                        }
                    });
                }
                // Finalize the calendar
                add.setVisibility(View.VISIBLE);
                calendar[0].goToday(true);
                setOnClick();
            }
        });
    }

    /**
     * Set on Click
     */
    private void setOnClick(){
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
        setAddOnClick();
    }

    /**
     * Set on add note click
     */
    private void setAddOnClick(){
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressLint("InflateParams")
            public void onClick(View v) {
                // Create and show a dialog
                final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                View view = getLayoutInflater().inflate(R.layout.dialog_timeline, null);
                // Setup all the components of the dialog
                dialog.setView(view);
                final AlertDialog alert = dialog.create();
                Button add = view.findViewById(R.id.button5);
                TextView day = view.findViewById(R.id.day);
                Button cancel = view.findViewById(R.id.button7);
                Spinner spinner = view.findViewById(R.id.spinner);
                EditText note = view.findViewById(R.id.add_note);
                // Set behavior for each component
                setAddDialog(note,spinner,cancel,add,alert, day);
                // Show the alert dialog
                alert.show();
            }
        });
    }

    /**
     * Setup the behavior of all components from "Add" dialog
     * @param note EditText
     * @param spinner Spinner
     * @param cancel Button
     * @param add Button
     * @param alert AlertDialog
     * @param day TextView
     */
    private void setAddDialog(EditText note, Spinner spinner, Button cancel, Button add,AlertDialog alert, TextView day){
        // Text handler
        day.setText(sdf.format(calendar[0].getSelectedDate().getTime()));
        note.addTextChangedListener(new TextWatcher() {
            private String text;
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { text = arg0.toString(); }
            public void afterTextChanged(Editable arg0) {
                // Set the limit of line
                int lineCount = note.getLineCount();
                if(lineCount > 17){
                    note.setText(text);
                }
            }
        });
        // Set spinner
        setSpinnerTime(spinner, view);
        // Set on click
        setOnClick(cancel, add, alert, note, spinner);
    }

    /**
     * Set on click for all the necessary stuffs
     * @param cancel Button
     * @param add Button
     * @param alert AlertDialog
     * @param note EditText
     * @param spinner Spinner
     */
    private void setOnClick(Button cancel, Button add, AlertDialog alert, EditText note, Spinner spinner){
        cancel.setOnClickListener(new View.OnClickListener() {      // Cancel on click
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {         // Add on click
            @Override
            public void onClick(View v) {
                if (!spinner.getSelectedItem().toString().equals("Time") && !note.getText().toString().equals("")) {        // If fill all the form
                    firebaseHandler.getCurrentCalendar(user).collection("data").document(sdf.format(calendar[0].getSelectedDate().getTime())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {          // Update date, time, and note based on the detail the user has filled
                            // Get current data
                            ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                            ArrayList<String> noteList = (ArrayList<String>) task.getResult().get("note");
                            ArrayList<String> type = (ArrayList<String>) task.getResult().get("type");
                            // Add new data
                            time.add(spinner.getSelectedItem().toString().split(":")[0]);
                            noteList.add(note.getText().toString());
                            type.add("note");
                            // Store new changes to the firebase
                            task.getResult().getReference().update("time", time);
                            task.getResult().getReference().update("note", noteList);
                            task.getResult().getReference().update("type", type);
                            // Refresh the list
                            setList(sdf.format(calendar[0].getSelectedDate().getTime()));
                            alert.dismiss();
                        }
                    });
                }
                // If there is a missing field, the system cannot allow the user to add a new note
                else if (spinner.getSelectedItem().toString().equals("Time")) Toast.makeText(view.getContext(), "Please choose the time!" , Toast.LENGTH_SHORT).show();
                else if (note.getText().toString().equals("")) Toast.makeText(view.getContext(), "There is empty note!" , Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Set spinner time
     * @param spinner Spinner
     * @param view View
     */
    private void setSpinnerTime(Spinner spinner, View view){
        firebaseHandler.getCurrentCalendar(user).collection("data").document(sdf.format(calendar[0].getSelectedDate().getTime())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Get the data from firebase
                ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                ArrayList<String> type = (ArrayList<String>) task.getResult().get("type");
                spinnerTime.clear();
                // Add time selection for spinner
                spinnerTime.add("Time");
                for (int i = 0; i< 24; i++){
                    spinnerTime.add(String.valueOf(i) + ":00");
                }
                assert type != null;
                // remove the time that contains note
                for (int i = 0; i < Objects.requireNonNull(time).size(); i++){
                    if (!type.get(i).equals("class")){
                        spinnerTime.remove((time.get(i)+":00"));
                    }
                }
                // Set adapter for spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),android.R.layout.simple_spinner_item, spinnerTime);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(null);
                spinner.setAdapter(adapter);
            }
        });
    }

    /**
     * Set list of note based on the selected date
     * @param date String
     */
    private void setList(String date) {
        nothing.setVisibility(View.VISIBLE);
        listView.setAdapter(null);
        firebaseHandler.getDateData(date, user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Get data from firebase
                ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                assert time != null;
                if (time.size() != 0){          // If there is a note or more
                    // Get more data
                    ArrayList<Timeline> timelineArrayList = new ArrayList<>();
                    ArrayList<String> note = (ArrayList<String>) task.getResult().get("note");
                    ArrayList<String> type = (ArrayList<String>) task.getResult().get("type");
                    assert note != null;
                    assert type != null;
                    // Store the data to the list
                    for (int i = 0; i< time.size(); i++){
                        timelineArrayList.add(get(time.get(i), note.get(i), type.get(i)));
                    }
                    // Sort the list to correct the time flow
                    sortTimeline(timelineArrayList);
                    // Set adapter for the list
                    ArrayAdapter<Timeline> adapter = new TimelineArrayAdapter(getActivity(), timelineArrayList);
                    listView.setAdapter(adapter);
                    nothing.setVisibility(View.INVISIBLE);
                    setListViewListener(timelineArrayList);
                }
            }
        });
    }

    /**
     * dialog for modifying note
     * @param timelines ArrayList<Timeline>
     * @param position int
     * @param alert AlertDialog
     */
    private void setDialogForModifying(ArrayList<Timeline> timelines, int position, AlertDialog alert){
        // Set the custom dialog for modifying note
        final AlertDialog.Builder dialog1 = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        View v1 = getLayoutInflater().inflate(R.layout.change_dialog, null);
        // Set up all necessary components
        dialog1.setView(v1);
        final AlertDialog alert1 = dialog1.create();
        Button confirm = v1.findViewById(R.id.button6);
        Button cancel = v1.findViewById(R.id.button8);
        EditText newNote = v1.findViewById(R.id.add_note2);
        // Set the current note to the note field
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
        // Set the limit of lines for edit text
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
        // Set on click for needed items
        setOnClick(confirm, cancel, newNote, timelines, position, alert1);
        // Show the alert dialog and dismiss the old one
        alert1.show();
        alert.dismiss();
    }

    private void setOnClick(Button confirm, Button cancel, EditText newNote, ArrayList<Timeline> timelines, int position, AlertDialog alert1){
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newNote.getText().toString().equals("")) {         // If there is at least a word
                    firebaseHandler.getCurrentCalendar(user)
                            .collection("data").document(sdf.format(calendar[0].getSelectedDate().getTime())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            // Get the data from firebase
                            ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                            ArrayList<String> note = (ArrayList<String>) task.getResult().get("note");
                            assert note != null;
                            // Edit the note at that time and date
                            for (int i = 0; i < Objects.requireNonNull(time).size(); i++) {
                                if (time.get(i).equals(timelines.get(position).getTime())) {
                                    note.remove(i);
                                    note.add(i, newNote.getText().toString());
                                    break;
                                }
                            }
                            // Update the new note
                            task.getResult().getReference().update("note", note);
                            // Refresh the list and dismiss the dialog
                            setList(sdf.format(calendar[0].getSelectedDate().getTime()));
                            alert1.dismiss();
                        }
                    });
                }
                // If there is nothing in note field
                else Toast.makeText(getContext(), "Empty Note!", Toast.LENGTH_SHORT).show();
            }
        });
        // Exit the dialog
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert1.dismiss();
            }
        });
    }

    /**
     * Item of list view on click
     * @param timelines ArrayList<Timeline>
     */
    private void setListViewListener(ArrayList<Timeline> timelines){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!timelines.get(position).getType().equals("class")){            // The user only can change the note/data of the custom note instead of the fixed note
                    // Start an alert dialog
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.Theme_AppCompat_Dialog_Alert);
                    // Setup the components
                    View v = getLayoutInflater().inflate(R.layout.dialog_item_click, null);
                    dialog.setView(v);
                    final AlertDialog alert = dialog.create();
                    Button change = v.findViewById(R.id.change);
                    Button delete = v.findViewById(R.id.delete);
                    // change button is click
                    change.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Set dialog for modifying
                            setDialogForModifying(timelines, position, alert);
                        }
                    });
                    // delete button is click
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the note from firebase
                            deleteNote(timelines, position, alert);
                        }
                    });
                    alert.show();
                }
            }
        });
    }

    /**
     * Delete a chosen note
     * @param timelines ArrayList<Timeline>
     * @param position int
     * @param alert AlertDialog
     */
    private void deleteNote(ArrayList<Timeline> timelines, int position, AlertDialog alert){
        firebaseHandler.getCurrentCalendar(user)
                .collection("data").document(sdf.format(calendar[0].getSelectedDate().getTime())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Get data from firebase
                ArrayList<String> time = (ArrayList<String>) task.getResult().get("time");
                ArrayList<String> type = (ArrayList<String>) task.getResult().get("type");
                ArrayList<String> note = (ArrayList<String>) task.getResult().get("note");
                assert type != null;
                assert note != null;
                // Find the chosen note and delete it
                for (int i = 0; i < Objects.requireNonNull(time).size(); i++){
                    if (time.get(i).equals(timelines.get(position).getTime()) && note.get(i).equals(timelines.get(position).getNote())
                            && type.get(i).equals(timelines.get(position).getType())){
                        time.remove(i);
                        type.remove(i);
                        note.remove(i);
                        break;
                    }
                }
                // Update the new change to the firebase
                task.getResult().getReference().update("time", time);
                task.getResult().getReference().update("note", note);
                task.getResult().getReference().update("type", type);
                // Refresh the list and exit the dialog
                setList(sdf.format(calendar[0].getSelectedDate().getTime()));
                alert.dismiss();
            }
        });
    }

    /**
     * To sort the note following the real timeline
     * @param timelines ArrayList<Timeline>
     */
    private void sortTimeline(ArrayList<Timeline> timelines){
        ArrayList<Timeline> sortedArray = new ArrayList<>();
        // Sort the timeline from start of the day still the end
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
        // Prioritize the fixed note to be at the first and they will be highlighted
        for (int i = 0; i< sortedArray.size(); i++){
            if (sortedArray.get(i).getType().equals("class")){
                timelines.add(sortedArray.get(i));
                sortedArray.remove(i);
                i--;
            }
        }
        timelines.addAll(sortedArray);
    }

    /**
     * Get the timeline based on given parameters
     * @param time String
     * @param note String
     * @param type String
     * @return Timeline
     */
    private Timeline get(String time, String note, String type) {
        return new Timeline(time,note, type);
    }

    /**
     * Get Date after the given date
     * @param day Date
     * @return Date
     */
    private static Date addDays(Date day) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(day);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }
}