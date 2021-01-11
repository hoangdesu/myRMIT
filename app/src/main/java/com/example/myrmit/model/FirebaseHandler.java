package com.example.myrmit.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FirebaseHandler {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build();

    public FirebaseHandler() {
        db.setFirestoreSettings(settings);
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public DocumentReference getProgramOfStudent(String username){
        return db.collection("users").document(username).collection("programCode").document("program");
    }

    public void confirmEnrolment(String username, ArrayList<String> list, ArrayList<String> semester){
        db.collection("users").document(username).collection("programCode").document("program").collection("data").document("enrolledCourse").update("list", list);
        db.collection("users").document(username).collection("programCode").document("program").collection("data").document("enrolledCourse").update("semester", semester);

    }

    public DocumentReference getEnrolledCourses(String username){
        return db.collection("users").document(username).collection("programCode").document("program").collection("data").document("enrolledCourse");
    }

    public DocumentReference getCompletedCourses(String username){
        return db.collection("users").document(username).collection("programCode").document("program").collection("data").document("finishCourses");
    }

    public DocumentReference getProgram(String programID){
        return db.collection("rmitprograms").document(programID);
    }
}
