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

public class InstructorSchedule extends AppCompatActivity {

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
        setContentView(R.layout.activity_instructor_schedule);

        cardsContainer = findViewById(R.id.cardsContainer);
        handler = new Handler();

        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        int user_id = preferences.getInt("userid", 0);

        // Fetch sessions from the database and update UI
        fetchSessionsFromDb(user_id);
    }

    private void fetchSessionsFromDb(int instructorId) {
        Log.d(TAG,"Instructor id"+instructorId);
        new Thread(() -> {
            try {
                try (Connection connection = DriverManager.getConnection(url, username, password)) {
                    String fetchSessionsQuery = "SELECT co.course_id, co.course_name, sc.session_id, sc.session_name, " +
                            "sc.start_date, sc.start_time FROM CoursesOffered co " +
                            "JOIN SessionsForCourse sc ON co.course_id = sc.course_id " +
                            "WHERE co.instructor_id = ?";

                    try (PreparedStatement statement = connection.prepareStatement(fetchSessionsQuery)) {
                        statement.setInt(1, instructorId);

                        Log.d(TAG,"Instructor id"+instructorId);

                        try (ResultSet resultSet = statement.executeQuery()) {
                            while (resultSet.next()) {
                                int courseId = resultSet.getInt("course_id");
                                String courseName = resultSet.getString("course_name");
                                int sessionId = resultSet.getInt("session_id");
                                String sessionName = resultSet.getString("session_name");
                                String startDate = resultSet.getString("start_date");
                                String startTime = resultSet.getString("start_time");

                                System.out.println("Course ID: " + courseId);
                                System.out.println("Course Name: " + courseName);
                                System.out.println("Session ID: " + sessionId);
                                System.out.println("Session Name: " + sessionName);
                                System.out.println("Session Date: " + startDate);
                                System.out.println("Session Time: " + startTime);
                                System.out.println("-----------------------------");

                                Session session = new Session(sessionId, courseId, courseName, sessionName, startDate, startTime);
                                sessions.add(session);
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
        View cardView = inflater.inflate(R.layout.instructor_schedule_session_list, null);

        TextView courseName = cardView.findViewById(R.id.CourseName);
        TextView sessionName = cardView.findViewById(R.id.SessionName);
        TextView startDate = cardView.findViewById(R.id.StartDate);
        TextView startTime = cardView.findViewById(R.id.StartTime);
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
            Toast.makeText(InstructorSchedule.this, "No app installed to handle the meeting link", Toast.LENGTH_SHORT).show();
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
