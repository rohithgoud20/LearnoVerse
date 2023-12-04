package com.example.learnoverse;

import java.time.LocalDate;
import java.time.LocalTime;

public class Session {


    private  String coursename;



    private int sessionId;


    private int courseId;
    private String sessionName;
    private String startDate;
    private String startTime;


    // Constructors, getters, and setters

    public Session(int sessionId,int courseId,String coursename, String sessionName, String startDate, String startTime) {

        this.coursename = coursename;
        this.sessionName = sessionName;
        this.startDate = startDate;
        this.startTime = startTime;
        this.courseId = courseId;
        this.sessionId=sessionId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }



    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }


    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}

