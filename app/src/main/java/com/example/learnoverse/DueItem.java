package com.example.learnoverse;

public class DueItem {
    private String courseName;
    private String instructorName;
    private String dueDate;

    public DueItem(String courseName, String instructorName, String dueDate) {
        this.courseName = courseName;
        this.instructorName = instructorName;
        this.dueDate = dueDate;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public String getDueDate() {
        return dueDate;
    }
}
