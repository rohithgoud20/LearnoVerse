package com.example.learnoverse;

public class Course {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSessions() {
        return sessions;
    }

    public void setSessions(int sessions) {
        this.sessions = sessions;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    private String name;
    private String description;
    private int sessions;
    private double price;
    private String meetingLink;

    // Constructor, getters, setters...

    // Example constructor
    public Course(String name, String description, int sessions, double price, String meetingLink) {
        this.name = name;
        this.description = description;
        this.sessions = sessions;
        this.price = price;
        this.meetingLink = meetingLink;
    }


}
