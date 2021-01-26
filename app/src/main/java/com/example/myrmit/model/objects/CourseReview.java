package com.example.myrmit.model.objects;

import java.util.ArrayList;

public class CourseReview {
    String name;
    String description;
    String code;
    ArrayList<String> courses;
    public CourseReview(String name, String description, ArrayList<String> courses, String code){
        this.name = name;
        this.code = code;
        this.description = description;
        this.courses = courses;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getCourses() {
        return courses;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCourses(ArrayList<String> courses) {
        this.courses = courses;
    }
}
