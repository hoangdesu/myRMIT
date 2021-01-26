package com.example.myrmit.model.objects;

import java.util.ArrayList;

public class Note {
    ArrayList<String> time;
    ArrayList<String> note;
    ArrayList<String> type;
    public Note(){
        time = new ArrayList<>();
        note = new ArrayList<>();
        type = new ArrayList<>();
    }

    public ArrayList<String> getTime() {
        return time;
    }

    public ArrayList<String> getNote() {
        return note;
    }

    public void setTime(ArrayList<String> time) {
        this.time = time;
    }

    public void setNote(ArrayList<String> note) {
        this.note = note;
    }

    public ArrayList<String> getType() {
        return type;
    }

    public void setType(ArrayList<String> type) {
        this.type = type;
    }
}
