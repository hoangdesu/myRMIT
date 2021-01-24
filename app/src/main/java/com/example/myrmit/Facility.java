package com.example.myrmit;

import java.util.List;

public class Facility {
    private String image;
    private String title;
    private String openHour;
    private double rating;
    private List<String> facilities;

    public Facility(String image, String title, String openHour, double rating, List<String> facilities) {
        this.image = image;
        this.title = title;
        this.openHour = openHour;
        this.rating = rating;
        this.facilities = facilities;
    }

    public List<String> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOpenHour() {
        return openHour;
    }

    public void setOpenHour(String openHour) {
        this.openHour = openHour;
    }
}
