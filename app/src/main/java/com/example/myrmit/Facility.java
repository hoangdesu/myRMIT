package com.example.myrmit;

public class Facility {
    private String image;
    private String title;
    private String openHour;

    public Facility(String image, String title, String openHour) {
        this.image = image;
        this.title = title;
        this.openHour = openHour;
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
