package com.example.myrmit.model.objects;

public class RMITService {
    String name;
    String time;
    String location;
    String phone;
    String description;
    public RMITService(String name, String time, String location, String phone, String description){
        this.name = name;
        this.location = location;
        this.time = time;
        this.description = description;
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getPhone() {
        return phone;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
