package com.example.learnoverse;
public class RegisteredCourseInfo {

    private int selectedCourseId;
    private String courseName;
    private String instructorName;
    private float rating;
    private int noOfSessions;
    private String description;
    private String status;
    private boolean isPaymentDone;

    public RegisteredCourseInfo(int selectedCourseId, String courseName, String instructorName,
                                float rating, int noOfSessions, String description,
                                String status, boolean isPaymentDone) {
        this.selectedCourseId = selectedCourseId;
        this.courseName = courseName;
        this.instructorName = instructorName;
        this.rating = rating;
        this.noOfSessions = noOfSessions;
        this.description = description;
        this.status = status;
        this.isPaymentDone = isPaymentDone;
    }

    public int getSelectedCourseId() {
        return selectedCourseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public float getRating() {
        return rating;
    }

    public int getNoOfSessions() {
        return noOfSessions;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public boolean isPaymentDone() {
        return isPaymentDone;
    }

    public int getProgress() {
        return 2;
    }

    // You can add getters for other attributes and customize the class as needed
}
