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

/**
 * Work with firebase firestore
 */
public class FirebaseHandler {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirebaseHandler() {}

    /**
     * Get Account from username
     * @param username String
     * @return DocumentReference
     */
    public DocumentReference getAccount(String username){
        return db.collection("users").document(username);
    }

    /**
     * Get program ID of the user
     * @param username  String
     * @return DocumentReference
     */
    public DocumentReference getProgramOfUser(String username){
        return db.collection("users").document(username).collection("programCode").document("program");
    }

    /**
     * Update data if the user confirm the enrollment
     * @param username String
     * @param list ArrayList<String>
     * @param semester ArrayList<String>
     */
    public void confirmEnrolment(String username, ArrayList<String> list, ArrayList<String> semester){
        db.collection("users").document(username).collection("programCode").document("program").collection("data").document("enrolledCourse").update("list", list);
        db.collection("users").document(username).collection("programCode").document("program").collection("data").document("enrolledCourse").update("semester", semester);

    }

    /**
     * Get current enrolled courses
     * @param username String
     * @return DocumentReference
     */
    public DocumentReference getEnrolledCourses(String username){
        return db.collection("users").document(username).collection("programCode").document("program").collection("data").document("enrolledCourse");
    }

    /**
     * Get completed courses
     * @param username String
     * @return DocumentReference
     */
    public DocumentReference getCompletedCourses(String username){
        return db.collection("users").document(username).collection("programCode").document("program").collection("data").document("finishCourses");
    }

    /**
     * Get list of courses and data of the given program
     * @param programID String
     * @return DocumentReference
     */
    public DocumentReference getProgram(String programID){
        return db.collection("rmitprograms").document(programID);
    }

    /**
     * Get progressing courses
     * @param username String
     * @return DocumentReference
     */
    public DocumentReference getProgressingCourse(String username){
        return db.collection("users").document(username).collection("programCode").document("program").collection("data").document("progressingCourse");
    }

    /**
     * Update new data after the user confirm the allocation
     * @param username String
     * @param day String
     * @param time String
     * @param courseName String
     */
    public void confirmAllocation(String username, String day, String time, String courseName){
        db.collection("users").document(username).collection("programCode").document("program").collection("data").document("progressingCourse").collection("data").document(courseName).update("time", time);
        db.collection("users").document(username).collection("programCode").document("program").collection("data").document("progressingCourse").collection("data").document(courseName).update("day", day);
    }

    /**
     * Get current semester to creating a new calendar
     * @return DocumentReference
     */
    public DocumentReference getCurrentSemester(){
        return db.collection("semester").document("now");
    }

    /**
     * Get data (notes) from given date from calendar
     * @param date String
     * @param username String
     * @return DocumentReference
     */
    public DocumentReference getDateData(String date, String username){
        return db
                .collection("users").document(username)
                .collection("programCode").document("calendar")
                .collection("data").document(date);
    }

    /**
     * Get news list
     * @return Task<QuerySnapshot>
     */
    public Task<QuerySnapshot> getNews() {
        return db.collection("news").whereEqualTo("type", "news").get();
    }

    /**
     * Get event list
     * @return Task<QuerySnapshot>
     */
    public Task<QuerySnapshot> getEvents() {
        return db.collection("news").whereEqualTo("type", "event").get();
    }

    /**
     * Get facilities list
     * @return Task<QuerySnapshot>
     */
    public Task<QuerySnapshot> getFacilities() {
        return db.collection("facilities").get();
    }

    public Task<QuerySnapshot> getRooms() {
        return db.collection("booking").document("rooms").collection("data").get();
    }

    public void updateRoomAvailability(boolean available, String document, String time, String user) {
        db.collection("booking").document("rooms").collection("data").document(document).update("available",available);
        db.collection("booking").document("rooms").collection("data").document(document).update("bookedBy",user);
        db.collection("booking").document("rooms").collection("data").document(document).update("bookedAt",time);
    }

    /**
     * Update new timetable after allocating
     * @param username String
     */
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

    /**
     * Update status of the post to firebase
     * @param id String
     * @param title String
     * @param like String
     */
    public void updatePostLike(String id, String title, boolean like) {
        if (like) {
            db.collection("news").document(title).update("likes", FieldValue.arrayUnion(id));
        } else {
            db.collection("news").document(title).update("likes", FieldValue.arrayRemove(id));
        }
    }

    /**
     * Add class time (note) to the given date after allocating
     * @param username String
     * @param dates  ArrayList<String>
     * @param time ArrayList<String>
     * @param notes ArrayList<String>
     */
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

    /**
     * Get current calendar of the user
     * @param username String
     * @return DocumentReference
     */
    public DocumentReference getCurrentCalendar(String username){
        return  db.collection("users").document(username).collection("programCode").document("calendar");
    }

    /**
     * Get services of the RMIT
     * @return CollectionReference
     */
    public CollectionReference getRMITServices(){
        return db.collection("services");
    }

    /**
     * Get all accounts (students/lecturers) of the RMIT
     * @return CollectionReference
     */
    public CollectionReference getAllAccounts(){
        return db.collection("users");
    }

    /**
     * Get all the RMIT programs
     * @return CollectionReference
     */
    public CollectionReference getRMITPrograms(){
        return db.collection("rmitprograms");
    }

    /**
     * Get the list of tutor of the RMIT
     * @return DocumentReference
     */
    public DocumentReference getTutorList(){
        return db.collection("booking").document("tutors");
    }

    /**
     * Get all Clubs of the RMIT
     * @return CollectionReference
     */
    public CollectionReference getAllClubs() {
        return db.collection("clubs");
    }

    public DocumentReference getClub(String name){
        return db.collection("Clubs").document(name);
    }
}