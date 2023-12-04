package com.example.learnoverse;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class InstructorHomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_home_page);

        ImageView profileIcon = findViewById(R.id.butprofile);
        TextView welcomeMsg = findViewById(R.id.welcometextView);
        ImageView notification = findViewById(R.id.butnotification);

        // Retrieve instructor's information from SharedPreferences or any other source
        // Replace the following line with your logic to get instructor details
        String instructorName = "Instructor Name";

        welcomeMsg.setText("Welcome, " + instructorName);

        // Offer Courses Card
        findViewById(R.id.courses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(InstructorHomePage.this, InstructorCourses.class));
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

        findViewById(R.id.Chats).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InstructorHomePage.this, Chats.class));
            }
        });
        findViewById(R.id.Students).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InstructorHomePage.this, InstructorRegisteredStudents.class));
            }
        });

        // Chats Card
        findViewById(R.id.Analysis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InstructorHomePage.this, InstructorAnalysis.class));
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InstructorHomePage.this, NotificationsActivity.class));
            }
        });


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
}
