package com.example.myrmit.news;

public class News {
    private String thumbnail;
    private String title;
    private String description;
    private String author;
    private boolean isLiked;

    public News(String thumbnail, String title, String description, String author, boolean isLiked) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.description = description;
        this.author = author;
        this.isLiked = isLiked;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLike(boolean like) {
        isLiked = like;
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
