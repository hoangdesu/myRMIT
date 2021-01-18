package com.example.myrmit;

public class News {
    private String thumbnail;
    private String title;
    private String description;
    private String author;

    public News(String thumbnail, String title, String description, String author) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.description = description;
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
