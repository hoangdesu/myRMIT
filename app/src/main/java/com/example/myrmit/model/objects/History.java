package com.example.myrmit.model.objects;

public class History {
    String courseName;
    String gpa;
    public History(String courseName, String gpa){
        this.courseName = courseName;
        this.gpa = gpa;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getGpa() {
        return gpa;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setGpa(String gpa) {
        this.gpa = gpa;
    }
}
