package com.example.learnoverse;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

public class InstructorHomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_home_page);

        ImageView profileIcon = findViewById(R.id.butprofile);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

    TextView welcomeMsg = findViewById(R.id.welcometextView);

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

    public void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this,v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_profile) {
                    openInstructorProfile();
                    return true;
                } else if (itemId == R.id.menu_logout) {
                    logout();
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openInstructorProfile() {
        Intent intent = new Intent(this, InstructorProfile.class);
        startActivity(intent);
    }

}
