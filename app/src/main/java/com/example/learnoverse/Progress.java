package com.example.learnoverse;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Progress extends AppCompatActivity implements  NewGoal.NewGoalDialogListener {
    private RecyclerView goalsRecyclerView;
    private GoalAdapter goalAdapter;
    private List<CompletedCourse> completedCoursesList;
    private CompletedCourseAdapter completedCourseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_page);
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String username = preferences.getString("username", null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

         goalsRecyclerView = findViewById(R.id.goalsRecyclerView);
        goalsRecyclerView.setLayoutManager(layoutManager);
        goalAdapter = new GoalAdapter(getGoalsForUser(username)); // Replace with your method to get goals
        goalsRecyclerView.setAdapter(goalAdapter);
        List<Goal> inProgressGoals = getInProgressGoalsForUser(username);
        // Initialize the RecyclerView and Adapter for "In Progress" goals
        RecyclerView inProgressRecyclerView = findViewById(R.id.ProgressRecyclerView);
        inProgressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ProgramAdapter inProgressAdapter = new ProgramAdapter(inProgressGoals);
        inProgressRecyclerView.setAdapter(inProgressAdapter);
        RecyclerView completedCoursesRecyclerView = findViewById(R.id.completedCoursesRecyclerView);
        completedCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list of completed courses and the adapter
        completedCoursesList = new ArrayList<>();
        completedCourseAdapter = new CompletedCourseAdapter(completedCoursesList);
        completedCoursesRecyclerView.setAdapter(completedCourseAdapter);

        // Get references to user input elements
        EditText courseNameEditText = findViewById(R.id.courseNameEditText);
        EditText ratingEditText = findViewById(R.id.ratingEditText);
        Button submitRatingButton = findViewById(R.id.submitRatingButton);

        // Handle the "Submit Rating" button click
        submitRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input from EditText fields
                String courseName = courseNameEditText.getText().toString();
                String ratingStr = ratingEditText.getText().toString();

                if (!courseName.isEmpty() && !ratingStr.isEmpty()) {
                    // Parse the rating as a float
                    float rating = Float.parseFloat(ratingStr);

                    // Create a new CompletedCourse instance and add it to the list
                    CompletedCourse completedCourse = new CompletedCourse(courseName, rating, true);
                    completedCoursesList.add(completedCourse);

                    // Notify the adapter that the data set has changed
                    completedCourseAdapter.notifyDataSetChanged();

                    // Clear the input fields
                    courseNameEditText.setText("");
                    ratingEditText.setText("");
                }
            }
        });


        // Set the adapter for the RecyclerView


        Button set_new_goal= findViewById(R.id.setNewGoalButton);

        set_new_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewGoal dialog = new NewGoal();
                dialog.show(getSupportFragmentManager(), "NewGoalDialog");

            }

        });


// Now, 'goals' contains all the goals for the specified user.

    }

    private List<Goal> getInProgressGoalsForUser(String username) {
        List<Goal> inProgressGoals = new ArrayList<>();
        List<Goal> allGoals = getGoalsForUser(username);

        for (Goal goal : allGoals) {
            if ("In Progress".equals(goal.getStatus())) {
                inProgressGoals.add(goal);
            }
        }

        return inProgressGoals;
    }


    private List<Goal> getGoalsForUser(String username) {
        List<Goal> goals = new ArrayList<>();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this); // Replace 'context' with your app's context
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "id",
                "user_name",
                "course_name",
                "instructor_name",
                "goal_text",
                "no_of_sessions",
                "status"
        };

        String selection = "user_name = ?";
        String[] selectionArgs = { username };

        Cursor cursor = db.query("Goal", projection, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                Goal goal = new Goal();
                goal.setId(cursor.getInt(cursor.getColumnIndex("id")));
                goal.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
                goal.setCourseName(cursor.getString(cursor.getColumnIndex("course_name")));
                goal.setInstructorName(cursor.getString(cursor.getColumnIndex("instructor_name")));
                goal.setGoalText(cursor.getString(cursor.getColumnIndex("goal_text")));
                goal.setNoOfSessions(cursor.getInt(cursor.getColumnIndex("no_of_sessions")));
                goal.setStatus(cursor.getString(cursor.getColumnIndex("status")));

                goals.add(goal);
                Log.i(TAG,"from db goal"+ " "+ goal.getId());
            }
            cursor.close();
        }
        return goals;
    }
    @Override
    public void onNewGoalDialogPositiveClick(String courseName, int sessions, String instructorName) {
        // Handle the input data, e.g., save it to your database or perform other actions
        Log.i(TAG, "print course work" + courseName);
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

// Ensure that db is not null and is writable

        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String username = preferences.getString("username", null);
        String user_name = null;
        if (username != null) {
            // You have the username, and you can use it in your app
            // For example, set it in a TextView
            Log.i(TAG,"username"+username);
            ContentValues values = new ContentValues();
            values.put("user_name", username); // Set the user's ID
            values.put("course_name", courseName);
            values.put("no_of_sessions", sessions);
            values.put("instructor_name", instructorName);
            values.put("status", "new");

            long goalId = db.insert("Goal", null, values);
            if (goalId != -1) {
                Log.i(TAG,"inserted to goal db");
            } else {
                // There was an error saving the goal
                // Handle the error as needed
            }
        } else {
            // Username is not available (e.g., user is not logged in)
        }

        // You can set the goal text as needed

// Insert the goal into the "Goal" table




// Don't forget to close the database when you're done
        db.close();

    }
}