package com.example.learnoverse;
public class courseSessions {
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

    private String sessionName;
    private String startDate;
    private String startTime;

    public courseSessions(String sessionName, String startDate, String startTime) {
        this.sessionName = sessionName;
        this.startDate = startDate;
        this.startTime = startTime;
    }


}
