package com.example.learnoverse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Profileinput extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileinput);
    }

    public void openLearnerProfile(View view) {
        // Navigate to the Learner profile page (replace with your actual activity name)
        Intent intent = new Intent(this, learnerprofile.class);
        startActivity(intent);
    }

    public void openInstructorProfile(View view) {
        // Navigate to the Instructor profile page (replace with your actual activity name)
        Intent intent = new Intent(this,InstructorProfile.class);
        startActivity(intent);
    }
}