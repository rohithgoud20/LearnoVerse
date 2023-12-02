package com.example.learnoverse;

import java.util.Objects;

public class Sessions {
    private String date;
    private String time;
    private String sessionName;  // Added property for session name

    public Sessions(String date, String time, String sessionName) {
        this.date = date;
        this.time = time;
        this.sessionName = sessionName;
        // Initialize other properties...
    }

    // Getter and setter methods for date, time, sessionName, and other properties...

    // Example implementation of equals method (for List.contains() to work properly)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Sessions session = (Sessions) obj;
        return date.equals(session.date) && time.equals(session.time) && sessionName.equals(session.sessionName);
    }

    // Example implementation of hashCode method (for List.contains() to work properly)
    @Override
    public int hashCode() {
        return Objects.hash(date, time, sessionName);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }
}
