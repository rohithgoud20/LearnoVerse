package com.example.learnoverse;

public enum InsertResult {
    SUCCESS("Success"),
    EMAIL_EXISTS("Email already exists"),
    NULL_VALUES("Null values provided"),
    FAILURE("Failure"),
    INVALID_EMAIL("Invalid email");

    private final String description;

    InsertResult(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
