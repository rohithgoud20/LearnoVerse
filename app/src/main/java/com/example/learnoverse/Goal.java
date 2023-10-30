package com.example.learnoverse;

public class Goal {
    private int id;

    public Goal() {

    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getGoalText() {
        return goalText;
    }

    public void setGoalText(String goalText) {
        this.goalText = goalText;
    }

    public int getNoOfSessions() {
        return noOfSessions;
    }

    public void setNoOfSessions(int noOfSessions) {
        this.noOfSessions = noOfSessions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String userName;
    private String courseName;
    private String instructorName;
    private String goalText;
    private int noOfSessions;
    private String status;

    public Goal(int id, String userName, String courseName, String instructorName, String goalText, int noOfSessions, String status) {
        this.id = id;
        this.userName = userName;
        this.courseName = courseName;
        this.instructorName = instructorName;
        this.goalText = goalText;
        this.noOfSessions = noOfSessions;
        this.status = status;
    }

    public void setId(int gid) {
         id = gid;
    }

    // Constructors, getters, and setters
}
