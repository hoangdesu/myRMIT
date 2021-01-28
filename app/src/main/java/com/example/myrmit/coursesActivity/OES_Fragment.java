package com.example.myrmit.coursesActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myrmit.R;
import com.example.myrmit.model.objects.Course;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.myrmit.model.*;
import com.example.myrmit.model.arrayAdapter.CoursesArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

/**
 * For enrolling courses
 */
public class OES_Fragment extends Fragment {
    private final FirebaseHandler firebaseHandler = new FirebaseHandler();
    private final String user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    private ListView listView;
    private ArrayList<Course> courses;
    private Button confirm;
    private View view;
    private ImageView loading;
    public OES_Fragment() {}

    /**
     * On create function
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Reset the list's components before reload the list
     */
    private void resetList(){
        courses = new ArrayList<>();
        listView.setAdapter(null);
        loading.setVisibility(View.VISIBLE);
        confirm.setVisibility(View.INVISIBLE);
    }

    /**
     * Set the check boxs for available semester for each course
     * @param completedCourses ArrayList<String>
     * @param courseList ArrayList<String>
     * @param isFeb ArrayList<Boolean>
     * @param isJun ArrayList<Boolean>
     * @param isOct ArrayList<Boolean>
     * @param feb ArrayList<String>
     * @param jun ArrayList<String>
     * @param oct ArrayList<String>
     */
    private void setAvailableCourses(ArrayList<String> completedCourses, ArrayList<String> courseList, ArrayList<Boolean> isFeb, ArrayList<Boolean> isJun, ArrayList<Boolean> isOct, ArrayList<String> feb, ArrayList<String> jun, ArrayList<String> oct){
        for (int i = 0; i < courseList.size(); i++) {
            courses.add(get(courseList.get(i)));
            boolean isExist = false;
            for (String course: completedCourses){
                if (course.equals(courseList.get(i))){
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                isFeb.add(feb.get(i).equals("1"));
                isJun.add(jun.get(i).equals("1"));
                isOct.add(oct.get(i).equals("1"));
            }
            else {
                isFeb.add(false);
                isJun.add(false);
                isOct.add(false);
            }
        }
    }

    /**
     * Set the current enrolment (initial)
     * @param enrolledList ArrayList<String>
     * @param sem ArrayList<String>
     */
    private void setCurrentEnrolment(ArrayList<String> enrolledList, ArrayList<String> sem){
        for (int i = 0; i < Objects.requireNonNull(enrolledList).size(); i++) {
            for (Course course : courses) {
                if (course.getName().equals(enrolledList.get(i))) {
                    if (sem.get(i).equals("feb")) {
                        course.setFeb(true);
                    } else if (sem.get(i).equals("jun")) {
                        course.setJun(true);
                    } else course.setOct(true);
                    break;
                }
            }
        }
    }

    /**
     * set the progressing course's behaviour
     * @param progressList ArrayList<String>
     * @param courseList ArrayList<String>
     * @param progress ArrayList<Boolean>
     */
    private void setProgressingCourses(ArrayList<String> progressList, ArrayList<String> courseList, ArrayList<Boolean> progress){
        for (String course: courseList){
            boolean isExist = false;
            for (String inProgress : progressList){
                if (course.equals(inProgress)){
                    isExist = true;
                    break;
                }
            }
            progress.add(isExist);
        }
    }

    /**
     * After loading and working with data, show the list
     * @param isFeb ArrayList<Boolean>
     * @param isJun ArrayList<Boolean>
     * @param isOct ArrayList<Boolean>
     * @param progress ArrayList<Boolean>
     */
    private void finalizeList(ArrayList<Boolean> isFeb, ArrayList<Boolean> isJun, ArrayList<Boolean> isOct, ArrayList<Boolean> progress, boolean isStudent){
        ArrayAdapter<Course> adapter = new CoursesArrayAdapter(getActivity(), courses, isFeb, isJun, isOct, progress, isStudent);
        listView.setAdapter(adapter);
        loading.setVisibility(View.INVISIBLE);
        confirm.setVisibility(View.VISIBLE);
    }

    /**
     * If this is the student's account, set list by this way
     * @param courseList ArrayList<String>
     * @param isFeb ArrayList<Boolean>
     * @param isJun ArrayList<Boolean>
     * @param isOct ArrayList<Boolean>
     * @param feb ArrayList<String>
     * @param jun ArrayList<String>
     * @param oct ArrayList<String>
     */
    private void setListForStudent(ArrayList<String> courseList, ArrayList<Boolean> isFeb, ArrayList<Boolean> isJun, ArrayList<Boolean> isOct, ArrayList<String> feb, ArrayList<String> jun, ArrayList<String> oct){
        firebaseHandler.getCompletedCourses(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {          // Get all completed courses
                ArrayList<String> completedCourses = (ArrayList<String>) task.getResult().get("courseList");
                assert completedCourses != null;
                setAvailableCourses(completedCourses, courseList,isFeb,isJun,isOct,feb,jun,oct);        // Set the behavior of these courses
                firebaseHandler.getEnrolledCourses(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {          // Get all enrolled courses
                        ArrayList<String> enrolledList = (ArrayList<String>) task.getResult().get("list");
                        ArrayList<String> sem = (ArrayList<String>) task.getResult().get("semester");
                        assert sem != null;
                        setCurrentEnrolment(enrolledList,sem);                              // Set the behavior of these courses as well
                        task.getResult().getReference().getParent().document("progressingCourse").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {      // Get all progressing courses
                                ArrayList<String> progressList = (ArrayList<String>) task.getResult().get("list");
                                assert progressList != null;
                                ArrayList<Boolean> progress = new ArrayList<>();
                                setProgressingCourses(progressList,courseList,progress);            // Set the behavior of these courses too
                                try {
                                    finalizeList(isFeb, isJun, isOct, progress, true);                    // Finalize the list
                                }catch (Exception ignored){}
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Set new enrolment page for the lecturer if there is a change in progressing courses
     */
    private void setNewEnrolmentForLecturer(String programID){
        firebaseHandler.getAllAccounts().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> taskk) {
                ArrayList<String> teachingCourses = new ArrayList<>();
                int[] counter = {0};
                for (DocumentSnapshot documentSnapshot: taskk.getResult()){     // for every account
                    String role = (String) documentSnapshot.get("role");
                    assert role != null;
                    if (role.equals("student")){                    // If there is a student
                        documentSnapshot.getReference().collection("programCode").document("program").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                String program = (String)task.getResult().get("code");
                                if (program.equals(programID)){         // If this student is studying in the program that you are taking
                                    firebaseHandler.getProgressingCourse(documentSnapshot.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {          // Get this student's progressing courses
                                            counter[0]++;
                                            ArrayList<String> courses = (ArrayList<String>) task.getResult().get("list");
                                            assert courses != null;
                                            for (String course : courses){                                      // Store all the progressing courses
                                                if (teachingCourses.size() == 0 || !teachingCourses.contains(course)){
                                                    teachingCourses.add(course);
                                                }
                                            }
                                            if (counter[0] == taskk.getResult().size()){                        // Update to the firebase and reload the list again
                                                firebaseHandler.getProgressingCourse(user).update("list", teachingCourses);
                                                Toast.makeText(getActivity(), "Update new data Successful! Reloading!", Toast.LENGTH_SHORT).show();
                                                getActivity().recreate();
                                            }
                                        }
                                    });
                                }
                                else counter[0]++;
                            }
                        });
                    }
                    else counter[0]++;
                }
            }
        });
    }

    /**
     * If this is the lecturer's account, set the list by this way
     * @param courseList ArrayList<String>
     * @param isFeb ArrayList<Boolean>
     * @param isJun ArrayList<Boolean>
     * @param isOct ArrayList<Boolean>
     */
    private void setListForLecturer(ArrayList<String> courseList, ArrayList<Boolean> isFeb, ArrayList<Boolean> isJun, ArrayList<Boolean> isOct, String programID){
        firebaseHandler.getProgressingCourse(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {              // Get the progressing courses
                ArrayList<String> progressingCourse = (ArrayList<String>) task.getResult().get("list");
                assert progressingCourse != null;
                if (progressingCourse.size() == 0){             // If there is nothing in progressing course list
                    setNewEnrolmentForLecturer(programID);
                }
                else {                                          // else
                    ArrayList<Boolean> progress = new ArrayList<>();        // Store all the progressing courses to the course objects
                    for (String course: courseList){
                        courses.add(get(course));
                        isFeb.add(false);
                        isJun.add(false);
                        isOct.add(false);
                        boolean isExist = false;
                        for (String inProgress : progressingCourse){
                            if (course.equals(inProgress)){
                                isExist = true;
                                break;
                            }
                        }
                        progress.add(isExist);
                    }
                    try {                               // Finalize and show the list
                        finalizeList(isFeb,isJun,isOct,progress, false);
                    } catch (Exception ignored){}
                }
            }
        });

    }

    /**
     * Set List View
     */
    private void setList() {
        resetList();            // Reset all components and stuffs to reload the list
        firebaseHandler.getProgramOfUser(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {                          // Get programID
                String programID = (String)task.getResult().get("code");
                firebaseHandler.getProgram(programID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {                  // Get all courses with available semesters
                        ArrayList<String> courseList = (ArrayList<String>) task.getResult().get("courses");
                        ArrayList<String> feb = (ArrayList<String>) task.getResult().get("feb");
                        ArrayList<String> jun = (ArrayList<String>) task.getResult().get("jun");
                        ArrayList<String> oct = (ArrayList<String>) task.getResult().get("oct");
                        ArrayList<Boolean> isFeb = new ArrayList<>();
                        ArrayList<Boolean> isJun = new ArrayList<>();
                        ArrayList<Boolean> isOct = new ArrayList<>();
                        assert courseList != null;
                        assert feb != null;
                        assert jun != null;
                        assert oct != null;
                        firebaseHandler.getAccount(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {       // Get account
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {                  // Get role
                                String role = (String) task.getResult().get("role");
                                assert role != null;
                                if (role.equals("student")){                                                // If this is the student's account, set a list for student
                                    setListForStudent(courseList, isFeb, isJun, isOct, feb, jun, oct);
                                }
                                else if (role.equals("lecturer")){                                          // If this is the lecturer's account, set a list for lecturer
                                    setListForLecturer(courseList,isFeb,isJun,isOct, programID);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Get courses based on its name
     * @param courseName String
     * @return Course
     */
    private Course get(String courseName) {
        return new Course(courseName);
    }

    /**
     * On Create View
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initialSetting(inflater,container);
        setConfirmClick(view);
        setList();
        return view;
    }

    /**
     * Setting all stuffs and components at the beginning
     * @param inflater LayoutInflater
     * @param container ViewGroup
     */
    private void initialSetting(LayoutInflater inflater, ViewGroup container){
        view = inflater.inflate(R.layout.oes_fragment, container, false);
        loading = view.findViewById(R.id.imageView);
        confirm = view.findViewById(R.id.button2);
        listView = view.findViewById(R.id.listview);

    }

    /**
     * Store the changes
     * @param list ArrayList<String>
     * @param semester ArrayList<String>
     */
    private void storeChanges(ArrayList<String> list, ArrayList<String> semester){
        for (Course course: courses){
            if (course.isFeb() || course.isJun() || course.isOct()){        // Get the selected semester, then store it
                list.add(course.getName());
                if (course.isFeb()){
                    semester.add("feb");
                }
                else if (course.isJun()){
                    semester.add("jun");
                }
                else if (course.isOct()){
                    semester.add("oct");
                }
            }
        }
    }

    /**
     * Set confirm button onClick
     * @param view View
     */
    private void setConfirmClick(View view){
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseHandler.getEnrolledCourses(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ArrayList<String> list = new ArrayList<>();
                        ArrayList<String> semester = new ArrayList<>();
                        storeChanges(list,semester);
                        // Maximum for each semester is 4, so the system will check and notify to the user whether it meet the rule or not
                        if (countMaxEnrol(semester, "feb") < 5 && countMaxEnrol(semester, "jun") < 5 && countMaxEnrol(semester, "oct") < 5) {   // If yes
                            ArrayList<String> enrolledCourse = (ArrayList<String>) task.getResult().get("list");
                            ArrayList<String> newSemester = (ArrayList<String>) task.getResult().get("semester");
                            assert enrolledCourse != null;
                            if (isChange(enrolledCourse, list, semester, newSemester)) {            // Store to the firebase
                                Toast.makeText(view.getContext(), "Save Successful!", Toast.LENGTH_SHORT).show();
                                firebaseHandler.confirmEnrolment("s3740819@rmit.edu.vn", list, semester);
                                setList();
                            } else Toast.makeText(view.getContext(), "There is no change in enrolment!", Toast.LENGTH_SHORT).show();        // If there is no change, tell the user
                        }
                        else {              // If no, tell the user to change the enrolment
                            if (countMaxEnrol(semester, "feb") >= 5){
                                showDialog(view, "The max number of courses for Feb semester is 4 only! Please try again!");
                            } else if (countMaxEnrol(semester, "jun") >= 5){
                                showDialog(view, "The max number of courses for Jun semester is 4 only! Please try again!");
                            } else showDialog(view, "The max number of courses for Nov semester is 4 only! Please try again!");
                        }
                    }
                });

            }
        });
    }

    /**
     * Count the number of courses the student choose
     * @param list ArrayList<String>
     * @param semester String
     * @return int
     */
    private int countMaxEnrol(ArrayList<String> list, String semester){
        int count = 0;
        for (String sem: list){
            if (sem.equals(semester)){
                count++;
            }
        }
        return count;
    }

    /**
     * Show the dialog to warn the user select more than 4 courses/sem
     * @param view View
     * @param text String
     */
    public void showDialog(View view, String text){
        AlertDialog alertDialog = new AlertDialog.Builder(view.getContext(), R.style.Theme_AppCompat_Dialog_Alert).create();
        alertDialog.setTitle("Fail!");
        alertDialog.setMessage(text);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /**
     * To check whether is the change or not
     * @param enrolCourses ArrayList<String>
     * @param newList ArrayList<String>
     * @param semester ArrayList<String>
     * @param newSemester ArrayList<String>
     * @return boolean
     */
    private boolean isChange(ArrayList<String> enrolCourses, ArrayList<String> newList, ArrayList<String> semester, ArrayList<String> newSemester){
        if (enrolCourses.size() != newList.size()){
            return true;
        }
        else {
            for (int i = 0; i < newList.size(); i++){
                if (!newList.get(i).equals(enrolCourses.get(i))){
                    return true;
                }
                else if (!semester.get(i).equals(newSemester.get(i))){
                    return true;
                }
            }
            return false;
        }
    }

}