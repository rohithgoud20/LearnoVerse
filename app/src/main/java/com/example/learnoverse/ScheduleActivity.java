package com.example.learnoverse;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.time.format.DateTimeFormatterBuilder;


import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    private LinearLayout cardsContainer;
    private ArrayList<Session> sessions = new ArrayList<>();
    private Handler handler;

    // Add your database connection details here
    private static final String DATABASE_NAME = "learnoverse";
    private static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    private static final String username = "admin";
    private static final String password = "learnoverse";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        cardsContainer = findViewById(R.id.cardsContainer);
        handler = new Handler();

        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String login_email_id = preferences.getString("login_email_id", "");

        // Fetch sessions from the database and update UI
        fetchSessionsFromDb(login_email_id);
    }

    private void fetchSessionsFromDb(String login_email_id) {
        new Thread(() -> {
            try {
                try (Connection connection = DriverManager.getConnection(url, username, password)) {
                    // Step 1: Fetch course IDs for the registered courses of the user
                    String fetchCourseIdsQuery = "SELECT rc.selected_course, co.course_name " +
                            "FROM RegisteredCourses rc " +
                            "JOIN CoursesOffered co ON rc.selected_course = co.course_id " +
                            "WHERE rc.user_name = ?";

                    try (PreparedStatement fetchCourseIdsStatement = connection.prepareStatement(fetchCourseIdsQuery)) {
                        fetchCourseIdsStatement.setString(1, login_email_id);

                        try (ResultSet courseIdsResultSet = fetchCourseIdsStatement.executeQuery()) {
                            while (courseIdsResultSet.next()) {
                                int courseId = courseIdsResultSet.getInt("selected_course");
                                String courseName = courseIdsResultSet.getString("course_name");

                                // Step 2: Fetch session details for each course ID
                                String fetchSessionDetailsQuery = "SELECT session_id,session_name, start_date, start_time FROM SessionsForCourse WHERE course_id = ?";
                                try (PreparedStatement fetchSessionDetailsStatement = connection.prepareStatement(fetchSessionDetailsQuery)) {
                                    fetchSessionDetailsStatement.setInt(1, courseId);

                                    try (ResultSet sessionDetailsResultSet = fetchSessionDetailsStatement.executeQuery()) {
                                        while (sessionDetailsResultSet.next()) {
                                            int sessionid = sessionDetailsResultSet.getInt("session_id");
                                            String sessionName = sessionDetailsResultSet.getString("session_name");
                                            String startDate = sessionDetailsResultSet.getString("start_date");
                                            String startTime = sessionDetailsResultSet.getString("start_time");
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                            LocalDate start_Date = LocalDate.parse(startDate, formatter);
                                            System.out.println("session ID: " + sessionid);
                                            System.out.println("Course ID: " + courseId);
                                            System.out.println("Course Name: " + courseName);
                                            System.out.println("Session Name: " + sessionName);
                                            System.out.println("Session Date: " + startDate);
                                            System.out.println("Session Time: " + startTime);
                                            System.out.println("-----------------------------");
                                            Session session = new Session(sessionid,courseId,courseName, sessionName, startDate, startTime);
                                            sessions.add(session);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Update UI with fetched sessions on the main thread
            handler.post(() -> {
                for (Session session : sessions) {
                    addScheduleCard(session);
                }
            });
        }).start();
    }

    private void addScheduleCard(Session session) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.schedule_session_list, null);

        TextView courseName = cardView.findViewById(R.id.CourseName);
        TextView sessionName = cardView.findViewById(R.id.SessionName);
        TextView startDate = cardView.findViewById(R.id.StartDate);
        TextView startTime = cardView.findViewById(R.id.StartTime);
        Button meetLinkButton = cardView.findViewById(R.id.MeetlinkButton);
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Convert the session date and time to LocalDateTime
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("dd-MM-yyyy h")
                .optionalStart()
                .appendPattern(":mm a")
                .optionalEnd()
                .optionalStart()
                .appendPattern(":mma")
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        String sessionDateTimeString = session.getStartDate() + " " + session.getStartTime().trim();
        LocalDateTime sessionDateTime = LocalDateTime.parse(sessionDateTimeString, formatter);

        // Check if the course id is not null
        Integer courseId = session.getCourseId();
        Integer sessionId = session.getSessionId();
        Log.d(TAG,"given courseid"+courseId+sessionId);
        if (courseId != null) {
            // Compare session date/time with current date/time
            if (sessionDateTime.isAfter(currentDateTime)) {
                // Session is in the future, disable the button
                meetLinkButton.setEnabled(false);
            } else {
                // Session is now or in the past, enable the button
                meetLinkButton.setEnabled(true);

                // Set an onClickListener for the button if needed
                meetLinkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle button click
                        new Thread(() -> {
                            try {
                                // Use try-with-resources to manage the database connection
                                try (Connection connection = DriverManager.getConnection(url, username, password)) {
                                    // Prepare the query to fetch meeting link based on course_id
                                    String query = "SELECT meeting_link FROM CoursesOffered WHERE course_id = ?";
                                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                                        preparedStatement.setInt(1, courseId);

                                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                                            // Check if there is a result
                                            if (resultSet.next()) {
                                                String meetingLink = resultSet.getString("meeting_link");
                                                Log.d(TAG,"meet link inside result set"+meetingLink);

                                                // Use the meeting link as needed (e.g., open a web browser)
                                               runOnUiThread(() -> openMeetingLink(meetingLink,courseId,sessionId));
                                            } else {
                                                // No meeting link found for the specified course_id
                                                runOnUiThread(() -> Toast.makeText(ScheduleActivity.this, "Meeting link not found for course ", Toast.LENGTH_SHORT).show());
                                            }
                                        }
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                });
            }
        } else {
            // Log an error or handle the case where courseId is null
            Log.e(TAG, "CourseId is null for session: " + session.getSessionName());
        }

        courseName.setText(session.getCoursename());
        sessionName.setText(session.getSessionName());
        startDate.setText(session.getStartDate());
        startTime.setText(session.getStartTime());

        // Add the inflated view to the cards container
        cardsContainer.addView(cardView);
    }

    private void openMeetingLink(String meetingLink,int course_id, int session_id) {
        // Create an Intent to open a web page
        Log.d(TAG,"meeting link"+ meetingLink);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // Set the data of the Intent to the meeting URL
        intent.setData(Uri.parse(meetingLink));

        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(meetingLink));

        // Check if there is an activity that can handle this Intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the activity
            startActivity(intent);
            SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
            String login_email_id = preferences.getString("login_email_id", "");
            markAttendance(login_email_id,course_id,session_id);

        } else {
            // Provide user feedback if no activity is available to handle the Intent
            Toast.makeText(ScheduleActivity.this, "No app installed to handle the meeting link", Toast.LENGTH_SHORT).show();
        }
    }

    private void markAttendance(String useremailid, int courseid, int sessionid) {
        new Thread(() -> {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                // Check if the attendance entry already exists
                String checkQuery = "SELECT * FROM attendance WHERE useremailid = '" + useremailid +
                        "' AND courseid = " + courseid + " AND sessionid = " + sessionid;
                ResultSet resultSet = statement.executeQuery(checkQuery);

                if (resultSet.next()) {
                    // If entry exists, update the attended_lecture to 'yes'
                    String updateQuery = "UPDATE attendance SET attended_lecture = 1 WHERE useremailid = '" +
                            useremailid + "' AND courseid = " + courseid + " AND sessionid = " + sessionid;
                    statement.executeUpdate(updateQuery);
                } else {
                    // If entry doesn't exist, insert a new entry
                    String insertQuery = "INSERT INTO attendance (useremailid, courseid, sessionid, attended_lecture) " +
                            "VALUES ('" + useremailid + "', " + courseid + ", " + sessionid + ", 1)";
                    statement.execute(insertQuery);
                }

                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
