package com.example.learnoverse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FetchSessionsForUser {

    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";
  //  public static final String TABLE_NAME = "signup";


    public static void main(String[] args) {
        String userEmail = "user@example.com"; // Replace with the user's email

        new Thread(() -> {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                try (Connection connection = DriverManager.getConnection(url, username, password)) {
                    // Step 1: Fetch course IDs for the registered courses of the user
                    String fetchCourseIdsQuery = "SELECT selected_course FROM RegisteredCourses WHERE user_name = ?";
                    try (PreparedStatement fetchCourseIdsStatement = connection.prepareStatement(fetchCourseIdsQuery)) {
                        fetchCourseIdsStatement.setString(1, userEmail);

                        try (ResultSet courseIdsResultSet = fetchCourseIdsStatement.executeQuery()) {
                            while (courseIdsResultSet.next()) {
                                int courseId = courseIdsResultSet.getInt("selected_course");

                                // Step 2: Fetch session details for each course ID
                                String fetchSessionDetailsQuery = "SELECT session_name, start_date, start_time FROM SessionsForCourse WHERE course_id = ?";
                                try (PreparedStatement fetchSessionDetailsStatement = connection.prepareStatement(fetchSessionDetailsQuery)) {
                                    fetchSessionDetailsStatement.setInt(1, courseId);

                                    try (ResultSet sessionDetailsResultSet = fetchSessionDetailsStatement.executeQuery()) {
                                        while (sessionDetailsResultSet.next()) {
                                            String sessionName = sessionDetailsResultSet.getString("session_name");
                                            String startDate = sessionDetailsResultSet.getString("start_date");
                                            String startTime = sessionDetailsResultSet.getString("start_time");

                                            // Process session details (you can print, store, or use them as needed)
                                            System.out.println("Course ID: " + courseId);
                                            System.out.println("Session Name: " + sessionName);
                                            System.out.println("Session Date: " + startDate);
                                            System.out.println("Session Time: " + startTime);
                                            System.out.println("-----------------------------");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
