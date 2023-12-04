package com.example.learnoverse;// StudentInfo.java

public class StudentInfo {

    private String name;
    private boolean paymentDone;
    private String learnerComment;

    public StudentInfo(String name, boolean paymentDone, String learnerComment) {
        this.name = name;
        this.paymentDone = paymentDone;
        this.learnerComment = learnerComment;
    }

    public String getName() {
        return name;
    }

    public boolean isPaymentDone() {
        return paymentDone;
    }

    public String getLearnerComment() {
        return learnerComment;
    }
}
