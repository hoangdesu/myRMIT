package com.example.myrmit.mainActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myrmit.R;
import com.example.myrmit.SignInActivity;
import com.example.myrmit.model.FirebaseHandler;
import com.example.myrmit.model.arrayAdapter.HistoryArrayAdapter;
import com.example.myrmit.model.objects.History;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 *
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {
    private TextView tvUsername;
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressBar progressBarGPA;
    private ProgressBar credits_progress_bar;
    private TextView tvGPA;
    private TextView tvCredits;
    private final FirebaseHandler firebaseHandler = new FirebaseHandler();
    private TextView tvStudent_ID;
    private CardView history;
    private TextView tvDOB;
    private TextView tvProgram;
    private ImageView avatar;
    private TextView tvGender;
    private TextView tvRole;
    private Button logout;

    public RecordFragment() {}

    /**
     * On create function
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    /**
     * On create view function
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        initialSetting(view);
        setItems();
        return view;
    }

    /**
     * Set the behavior of the items
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setItems(){
        if (currentUser != null) {              // If this is not a guest account
            String userEmail = currentUser.getEmail();
            firebaseHandler.getAccount(userEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(DocumentSnapshot user) {
                    // Get all the information of the user
                    String role = user.getString("role");
                    String name = user.getString("name");
                    String dob = user.getString("dob");
                    String gender = user.getString("gender");
                    String studentID = userEmail.split("@")[0];
                    // Get the program ID of the user as well
                    user.getReference().collection("programCode").document("program").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String program = (String) task.getResult().get("code");
                            if (role.equals("student")) {           // If there is a student
                                studentSetting(task, program, gender, name, studentID, dob);
                            }
                            else {                                  // If there is a lecturer
                                setItems("lecturer", gender, name, studentID, dob, 0, 0, program, 0);
                            }
                        }
                    });
                }
            });
        }
        else {                          // If this is a guest account
            setItems("guest", null,null,null,null,0,0,null,0);
        }
    }

    /**
     * Initial setting for all components
     * @param view View
     */
    private void initialSetting(View view){
        // Configure all the components
        logout = view.findViewById(R.id.log_out_btn);
        tvUsername = view.findViewById(R.id.tv_fragment_record_username);
        tvGPA = view.findViewById(R.id.tvGPA);
        tvCredits = view.findViewById(R.id.tvCredits);
        avatar = view.findViewById(R.id.avatar);
        history = view.findViewById(R.id.history_card);
        tvStudent_ID = view.findViewById(R.id.tvStudent_ID);
        tvDOB = view.findViewById(R.id.tvDOB);
        tvRole = view.findViewById(R.id.role);
        tvGender = view.findViewById(R.id.tvGender);
        tvProgram = view.findViewById(R.id.tvProgram);
        progressBarGPA = view.findViewById(R.id.progressBarGPA);
        credits_progress_bar = view.findViewById(R.id.credits_progress_bar);
        progressBarGPA.setMax(40);
        progressBarGPA.setProgress((int) (0));
        credits_progress_bar.setMax(0);
        credits_progress_bar.setProgress(0);

        // Set on button click for logging out
        logoutOnClick();
    }

    /**
     * set listener for log out button
     */
    private void logoutOnClick(){
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser()!= null) {           // If this is a user, warn and ask the user if he/she want to log out
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure you want to log out?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(getContext(), SignInActivity.class));
                                    getActivity().finish();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                else {                                                              // If this is a guest, log out immediately
                    startActivity(new Intent(getContext(), SignInActivity.class));
                    getActivity().finish();
                }
            }
        });
    }

    /**
     * Setting for the student
     * @param task Task<DocumentSnapshot>
     * @param program String
     * @param gender String
     * @param name String
     * @param studentID String
     * @param dob String
     */
    private void studentSetting(Task<DocumentSnapshot> task, String program, String gender, String name, String studentID, String dob){
        task.getResult().getReference().collection("data").document("finishCourses").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Get finished course's records
                ArrayList<String> gradeList = (ArrayList<String>) task.getResult().get("grade");
                ArrayList<String> courseList = (ArrayList<String>) task.getResult().get("courseList");
                assert gradeList != null;
                int credits = gradeList.size() * 12;
                //
                firebaseHandler.getProgram(program).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @SuppressLint({"DefaultLocale", "UseCompatLoadingForDrawables"})
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        // Get list size to get the max credit of the given program
                        ArrayList<String> list = (ArrayList<String>) task.getResult().get("courses");
                        ArrayList<History> histories = new ArrayList<>();
                        // Store the history objects ( finished courses)
                        for (int i = 0; i < Objects.requireNonNull(courseList).size(); i++){
                            histories.add(get(courseList.get(i), gradeList.get(i)));
                        }
                        assert list != null;
                        // Count the gpa by the list of grades
                        double gpa = getGPA(gradeList);
                        setHistoryClick(histories);
                        // Set items following the given data
                        setItems("student", gender, name, studentID, dob, gpa, credits, program, list.size());
                    }
                });
            }
        });
    }

    /**
     * Get gpa from list of grades
     * @param gradeList ArrayList<String>
     * @return double
     */
    private double getGPA(ArrayList<String> gradeList){
        double gpa = 0;
        for (String grade : gradeList) {
            switch (grade) {
                case "PA":
                    gpa += 1;
                    break;
                case "CR":
                    gpa += 2;
                    break;
                case "DI":
                    gpa += 3;
                    break;
                case "HD":
                    gpa += 4;
                    break;
                default:
                    break;
            }
        }
        return (gpa/gradeList.size());
    }

    /**
     * Set the behavior of the items based on the role of the user
     * @param role String
     * @param gender String
     * @param name String
     * @param studentID String
     * @param dob String
     * @param gpa double
     * @param credits int
     * @param program String
     * @param size int
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetTextI18n", "DefaultLocale", "UseCompatLoadingForDrawables"})
    private void setItems(String role, String gender, String name, String studentID, String dob, double gpa, int credits, String program, int size){
        if (!role.equals("guest")){                     // If this is not a guest
            if (role.equals("lecturer")){               // If there is a lecturer
                tvRole.setText("Lecturer");
                tvGPA.setText("N/A");
                tvCredits.setText("N/A");
            }
            else {                                      // If there is a student
                credits_progress_bar.setMax(size * 12);
                tvRole.setText("Student");
                tvGPA.setText(String.format("%.2f", gpa));
                progressBarGPA.setProgress((int) (gpa * 10), true);
                credits_progress_bar.setProgress((int) credits, true);
                tvCredits.setText(credits + "/" + (size * 12));
            }

            // This is the setting for both 2 these roles
            tvProgram.setText(program);
            tvUsername.setText(name);
            tvStudent_ID.setText(studentID);
            tvDOB.setText(dob);
            tvGender.setText(gender);
            if (gender.equals("Male")){
                avatar.setImageDrawable(getResources().getDrawable(R.drawable.man));
            } else avatar.setImageDrawable(getResources().getDrawable(R.drawable.temp_avatar));
        }
        else {                  // If this is a guest, set the items by this way
            tvGPA.setText("N/A");
            tvUsername.setText("Guest");
            tvRole.setText("Guest");
            tvCredits.setText("N/A");
            tvStudent_ID.setText("N/A");
            tvDOB.setText("N/A");
            tvGender.setText("N/A");
            tvProgram.setText("N/A");
        }

    }

    /**
     * set History button click
     * @param histories ArrayList<History>
     */
    private void setHistoryClick(ArrayList<History> histories){
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       // Show an alert dialog for the user
                final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                View view = Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.dialog_view_history, null);
                dialog.setView(view);
                final AlertDialog alert = dialog.create();
                // Set up the list view which contains the finished courses and grades
                ListView listView = view.findViewById(R.id.historylist);
                ArrayAdapter<History> adapter = new HistoryArrayAdapter(getActivity(), histories);
                listView.setAdapter(adapter);
                alert.show();
            }
        });
    }

    /**
     * Get history object from course name and gpa
     * @param name String
     * @param gpa String
     * @return History
     */
    private History get(String name, String gpa){
        return new History(name, gpa);
    }

}