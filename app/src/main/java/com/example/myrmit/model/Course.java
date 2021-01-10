package com.example.myrmit.model;

public class Course {

    private String name;
    private boolean feb;
    private boolean jun;
    private boolean nov;

    public Course(String name) {
        this.name = name;
        feb = false;
        jun = false;
        nov = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFeb(boolean feb) {
        this.feb = feb;
    }

    public void setJun(boolean jun) {
        this.jun = jun;
    }

    public void setNov(boolean nov) {
        this.nov = nov;
    }

    public boolean isFeb() {
        return feb;
    }

    public boolean isJun() {
        return jun;
    }

    public boolean isNov() {
        return nov;
    }
}
