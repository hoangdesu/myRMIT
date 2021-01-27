package com.example.myrmit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myrmit.model.FirebaseHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {

    TextView tvUsername;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    ProgressBar progressBarGPA;
    ProgressBar credits_progress_bar;
    TextView tvGPA;
    TextView tvCredits;
    TextView tvStudent_ID;
    TextView tvDOB;
    TextView tvProgram;
    TextView tvGender;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button logout;

    public RecordFragment() {
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
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        logout = view.findViewById(R.id.log_out_btn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });


        // Display user's information
        tvUsername = view.findViewById(R.id.tv_fragment_record_username);
        tvGPA = view.findViewById(R.id.tvGPA);
        tvCredits = view.findViewById(R.id.tvCredits);
        tvStudent_ID = view.findViewById(R.id.tvStudent_ID);
        tvDOB = view.findViewById(R.id.tvDOB);
        tvGender = view.findViewById(R.id.tvGender);
        tvProgram = view.findViewById(R.id.tvProgram);
        progressBarGPA = view.findViewById(R.id.progressBarGPA);
        credits_progress_bar = view.findViewById(R.id.credits_progress_bar);

        progressBarGPA.setMax(4 * 10);
        progressBarGPA.setProgress((int) (3.4 * 10));
        credits_progress_bar.setMax(384);
        credits_progress_bar.setProgress(192);

        CollectionReference users = FirebaseFirestore.getInstance().collection("users");

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            //Log.i("EMAIL", userEmail);
            assert userEmail != null;
            DocumentReference userRef = users.document(userEmail);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(DocumentSnapshot user) {
                    String firstName = user.getString("firstName");
                    String lastName = user.getString("lastName");
                    String fullName = firstName + " " + lastName;
                    String name = user.getString("name");

                    String dob = user.getString("dob");
                    String gender = user.getString("gender");
                    Double gpa = user.getDouble("gpa");
                    String program = user.getString("program");
                    String studentID = user.getString("studentID");
                    Double credits = user.getDouble("credits");


                    if (firstName != null) {
                        tvUsername.setText(fullName);
                    } else {
                        tvUsername.setText(userEmail.substring(0, 8));
                    }

                    if ((dob != null) && (gpa != null)) {
                        tvGPA.setText(String.valueOf(gpa));
                        progressBarGPA.setProgress((int) (gpa * 10), true);
                        credits_progress_bar.setProgress(credits.intValue(), true);

                        tvCredits.setText(credits.intValue() + "/384");

                        tvStudent_ID.setText(studentID);
                        tvDOB.setText(dob);
                        tvGender.setText(gender);
                        tvProgram.setText(program);
                    }

                }
            });
        }




        return view;
    }
}