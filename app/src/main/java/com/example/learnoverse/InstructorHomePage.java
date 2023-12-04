package com.example.learnoverse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InstructorHomePage extends AppCompatActivity {
    private ImageView butnot,profileIcon;
    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";
    public static final String TABLE_NAME = "instructorprofiles";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_home_page);

        profileIcon = findViewById(R.id.butprofile);
        TextView welcomeMsg = findViewById(R.id.welcometextView);
        butnot=findViewById(R.id.butnotification);
        // Retrieve instructor's information from SharedPreferences or any other source
        // Replace the following line with your logic to get instructor details
        final String[] InsName = {""};
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String emailId = preferences.getString("login_email_id", "");
        try {
            fetchname(new HomePage.OnFetchNameCallback() {
                @Override
                public void onNameFetched(String name) {
                    // This method is called when the name is fetched
                    InsName[0] = name;
                    welcomeMsg.setText("Welcome " + InsName[0]);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Offer Courses Card
        findViewById(R.id.courses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(InstructorHomePage.this, InstructorCourses.class));
                 }
        });
        butnot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(InstructorHomePage.this, NotificationsActivity.class));
            }
        });
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(InstructorHomePage.this, ProfilepageInstructor.class));
            }
        });

        // Upload Materials Card
        findViewById(R.id.cardUploadMaterials).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InstructorHomePage.this, InstructorUploadMaterials.class));
            }

        });

        // Schedule Card
        findViewById(R.id.Schedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InstructorHomePage.this, InstructorSchedule.class));
            }
        });


        findViewById(R.id.Students).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InstructorHomePage.this, InstructorRegisteredStudents.class));
            }
        });

        // Chats Card


//        // Create Course Button
//        Button createCourseButton = findViewById(R.id.createCourseButton);
//        createCourseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Add logic to navigate to the page for creating a new course
//                startActivity(new Intent(InstructorHomePage.this, CreateCourseActivity.class));
//            }
//        });

        // Add more components and logic as needed
    }

    public void fetchname(HomePage.OnFetchNameCallback callback) throws SQLException {
        new Thread(() -> {
            // Do your work

            String fetchedName = "";

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                String emailId = preferences.getString("login_email_id", "");

                ResultSet rs = statement.executeQuery("SELECT first_name FROM instructorprofiles WHERE email='" + emailId + "'");

                if (rs.next()) {
                    fetchedName = rs.getString("first_name");
                } else {
                    Log.d("fetchname", "No records found for email ID: " + emailId);
                }

                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            // After the job is finished, use the callback to pass the fetched name
            callback.onNameFetched(fetchedName);
        }).start();
    }

    // Define a callback interface
    public interface OnFetchNameCallback {
        void onNameFetched(String name);
    }
}
