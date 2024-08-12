package edu.virginia.sde.reviews;

import java.sql.Timestamp;

public class Review {
    private int id;
    private int courseId;
    private String username;
    private double rating;
    private String comment;
    private Timestamp timestamp;
    private String mnemonic;
    private int number;
    private String title;

    public Review(int id, String mnemonic, int number, String title, String username, double rating, String comment, Timestamp timestamp) {
        this.id = id;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
        this.mnemonic = mnemonic;
        this.number = number;
        this.title = title;
    }
    public Review(int id, int courseId, String username, double rating, String comment, Timestamp timestamp) {
        this.id = id;
        this.courseId = courseId;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }
    public Review(int id, int courseId, String username, double rating, String comment) {
        this.id = id;
        this.courseId = courseId;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getMnemonic() { return mnemonic; }
    public void setMnemonic(String mnemonic) { this.mnemonic = mnemonic; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    // Getters and setters as before
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String toString() {
        return  "rating=" + rating +
                ", comment='" + comment + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
