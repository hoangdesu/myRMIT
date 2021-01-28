package com.example.myrmit.model.objects;

import java.util.ArrayList;

public class TutorItem {
    String name;
    String major;
    String phone;
    String mail;
    String role;
    ArrayList<String> time;
    ArrayList<String> day;
    String isBook;
    public TutorItem(String name, String mail, String major, String phone, String role, String isBook, ArrayList<String> day, ArrayList<String> time){
        this.name = name;
        this.mail = mail;
        this.major = major;
        this.phone = phone;
        this.role = role;
        this.isBook = isBook;
        this.day = day;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public ArrayList<String> getTime() {
        return time;
    }

    public ArrayList<String> getDay() {
        return day;
    }

    public String getIsBook() {
        return isBook;
    }

    public String getMail() {
        return mail;
    }

    public String getMajor() {
        return major;
    }

    public String getRole() {
        return role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTime(ArrayList<String> time) {
        this.time = time;
    }

    public void setDay(ArrayList<String> day) {
        this.day = day;
    }

    public void setIsBook(String isBook) {
        this.isBook = isBook;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
