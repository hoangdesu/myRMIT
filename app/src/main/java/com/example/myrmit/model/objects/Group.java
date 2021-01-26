package com.example.myrmit.model.objects;

public class Group {
    String lecturer;
    String courseName;
    String day1;
    String time1;
    String day2;
    String time2;
    boolean isGroup1 = false;
    boolean isGroup2 = false;
    public Group(String lecturer, String day1, String time1, String courseName, String day2, String time2){
        this.lecturer = lecturer;
        this.day1 = day1;
        this.time1 = time1;
        this.day2 = day2;
        this.time2 = time2;
        this.courseName = courseName;

    }

    public void setGroup1(boolean group1) {
        isGroup1 = group1;
    }

    public void setGroup2(boolean group2) {
        isGroup2 = group2;
    }

    public boolean isGroup1() {
        return isGroup1;
    }

    public boolean isGroup2() {
        return isGroup2;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getDay1() {
        return day1;
    }

    public String getDay2() {
        return day2;
    }

    public String getTime1() {
        return time1;
    }

    public String getTime2() {
        return time2;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setDay1(String day1) {
        this.day1 = day1;
    }

    public void setDay2(String day2) {
        this.day2 = day2;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }
}
