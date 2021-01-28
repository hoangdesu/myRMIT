package com.example.myrmit.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FirebaseHandler {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build();

    public FirebaseHandler() {
        db.setFirestoreSettings(settings);
    }

    public DocumentReference getAccount(String username){
        return db.collection("users").document(username);
    }

    public DocumentReference getProgramOfUser(String username){
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

    public DocumentReference getProgressingCourse(String username){
        return db.collection("users").document(username).collection("programCode").document("program").collection("data").document("progressingCourse");
    }

    public void confirmAllocation(String username, String day, String time, String courseName){
        db.collection("users").document(username).collection("programCode").document("program").collection("data").document("progressingCourse").collection("data").document(courseName).update("time", time);
        db.collection("users").document(username).collection("programCode").document("program").collection("data").document("progressingCourse").collection("data").document(courseName).update("day", day);
    }

    public DocumentReference getCurrentSemester(){
        return db.collection("semester").document("now");
    }

    public DocumentReference getDateData(String date, String username){
        return db
                .collection("users").document(username)
                .collection("programCode").document("calendar")
                .collection("data").document(date);
    }

    public Task<QuerySnapshot> getNews() {
        return db.collection("news").whereEqualTo("type", "news").get();
    }

    public Task<QuerySnapshot> getEvents() {
        return db.collection("news").whereEqualTo("type", "event").get();
    }

    public Task<QuerySnapshot> getFacilities() {
        return db.collection("facilities").get();
    }

    public Task<QuerySnapshot> getRooms() {
        return db.collection("booking").document("rooms").collection("data").get();
    }

    public void updateTimetable(String username){
        db.collection("users").document(username).collection("programCode").document("calendar").collection("data").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot documentSnapshot: task.getResult()){
                    ArrayList<String> timeline = (ArrayList<String>) documentSnapshot.get("time");
                    ArrayList<String> note = (ArrayList<String>) documentSnapshot.get("note");
                    ArrayList<String> type = (ArrayList<String>) documentSnapshot.get("type");
                    for (int i = 0; i < type.size(); i++){
                        if (type.get(i).equals("class")){
                            timeline.remove(i);
                            note.remove(i);
                            type.remove(i);
                            i--;
                        }
                    }
                    documentSnapshot.getReference().update("time", timeline);
                    documentSnapshot.getReference().update("note", note);
                    documentSnapshot.getReference().update("type", type);
                }
            }
        });
    }

    public void updatePostLike(String id, String title, boolean like) {
        if (like) {
            db.collection("news").document(title).update("likes", FieldValue.arrayUnion(id));
        } else {
            db.collection("news").document(title).update("likes", FieldValue.arrayRemove(id));
        }
    }

    public void addClassTime(String username, ArrayList<String> dates, ArrayList<String> time, ArrayList<String> notes) {
        for (String date : dates) {
            db.collection("users").document(username).collection("programCode").document("calendar").collection("data").document(date).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    ArrayList<String> timeline = (ArrayList<String>) task.getResult().get("time");
                    ArrayList<String> note = (ArrayList<String>) task.getResult().get("note");
                    ArrayList<String> type = (ArrayList<String>) task.getResult().get("type");
                    timeline.addAll(time);
                    for (int i = 0 ; i < notes.size(); i++) {
                        type.add("class");
                    }
                    note.addAll(notes);
                    task.getResult().getReference().update("time", timeline);
                    task.getResult().getReference().update("note", note);
                    task.getResult().getReference().update("type", type);
                }
            });
        }
    }

    public DocumentReference getCurrentCalendar(String username){
        return  db.collection("users").document(username).collection("programCode").document("calendar");
    }

    public CollectionReference getRMITServices(){
        return db.collection("services");
    }

    public CollectionReference getAllAccounts(){
        return db.collection("users");
    }

    public CollectionReference getRMITPrograms(){
        return db.collection("rmitprograms");
    }

    public CollectionReference getAllClubs() {
        return db.collection("clubs");
    }
}
