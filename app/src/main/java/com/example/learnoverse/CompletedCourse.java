package com.example.learnoverse;

public class CompletedCourse {
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isRated() {
        return isRated;
    }

    public void setRated(boolean rated) {
        isRated = rated;
    }

    private String courseName;
    private float rating;
    private boolean isRated;

    public CompletedCourse(String courseName, float rating, boolean isRated) {
        this.courseName = courseName;
        this.rating = rating;
        this.isRated = isRated;
    }



    // Getters and setters for courseName, rating, and isRated
}
