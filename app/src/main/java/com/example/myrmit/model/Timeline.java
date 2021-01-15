package com.example.myrmit.model;

import java.util.ArrayList;

public class Timeline {
    String time;
    String note;
    String type;
    public Timeline(String time, String note, String type){
        this.time = time;
        this.type =type;
        this.note = note;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
