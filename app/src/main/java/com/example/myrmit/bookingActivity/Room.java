package com.example.myrmit.bookingActivity;

public class Room {
    private int capacity;
    private String name;
    private boolean available;
    private String image;
    private double rating;


    public Room(int capacity, String name, boolean available, String image, double rating) {
        this.capacity = capacity;
        this.name = name;
        this.available = available;
        this.image = image;
        this.rating = rating;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
