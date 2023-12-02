package com.example.learnoverse;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SelectedCourseView extends AppCompatActivity  {
    private int currentPage = 1;
    private static final int ITEMS_PER_PAGE = 7; // Adjust the number of items per page
    private int itemCounter = 0; // Declare itemCounter at the class level
    private RecyclerView mSessionRecyclerView;
    private SessionAdapter mSessionAdapter;
    private boolean isExpanded = false;

    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";
    public static final String TABLE_NAME = "CoursesOffered";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_course_view);

        // Get a reference to the TableLayout
        //   TableLayout courseDetailsTable = findViewById(R.id.courseDetailsTable);

        // Initialize the database helper
        Intent intent = getIntent();
        String course_name = intent.getStringExtra("course_name");
        AtomicInteger course_id = new AtomicInteger();
        Log.i(TAG, "selected course name " + course_name);

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(
                         "SELECT course_id, instructor_id, no_of_sessions, rating, course_description FROM CoursesOffered WHERE course_name = ?")) {

                // Set the parameter using PreparedStatement to prevent SQL injection
                statement.setString(1, course_name);
                Log.d(TAG, "db query " + "SELECT course_id, instructor_id, no_of_sessions, rating FROM CoursesOffered WHERE course_name = " + course_name);
                // Execute the query
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<View> cardViews = new ArrayList<>(); // Store card views here

                    while (resultSet.next()) {
                        int courseId = resultSet.getInt("course_id");
                        int instructor_id = resultSet.getInt("instructor_id");
                        String instructorName = fetchInstructorName(instructor_id);
                        int noOfSessions = resultSet.getInt("no_of_sessions");
                        float rating = resultSet.getFloat("rating");
                        String course_description = resultSet.getString("course_description");
                        Log.d(TAG, "resultset " + courseId + instructorName);
                        // Inflate the card layout
                        View cardView = LayoutInflater.from(this).inflate(R.layout.courses_cards_layout, null);

                        // Find views within the card
                        TextView instructorNameTextView = cardView.findViewById(R.id.instructorNameTextView);
                        TextView ratingTextView = cardView.findViewById(R.id.ratingTextView);
                        TextView noOfSessionsTextView = cardView.findViewById(R.id.noOfSessionsTextView);
                        TextView descriptionTextView = cardView.findViewById(R.id.descriptionTextView);

                        // Populate data into card views
                        instructorNameTextView.setText(instructorName);
                        ratingTextView.setText("Rating: " + String.valueOf(rating));
                        noOfSessionsTextView.setText("Sessions: " + String.valueOf(noOfSessions));
                        descriptionTextView.setText(course_description);

                        // Add the card view to the list
                        cardViews.add(cardView);

                        // Set up the Enroll button click listener for this card
                        Button enrollButton = cardView.findViewById(R.id.buttonEnroll);
                        enrollButton.setOnClickListener(v -> {
                            // Extract course details from the clicked card view
                            TextView instructorNameTextViewClicked = cardView.findViewById(R.id.instructorNameTextView);
                            TextView noOfSessionsTextViewClicked = cardView.findViewById(R.id.noOfSessionsTextView);

                            String instructorNameClicked = instructorNameTextViewClicked.getText().toString();
                            String noOfSessionsClicked = noOfSessionsTextViewClicked.getText().toString();

                            // Pass the details to the new activity
                            Intent intent2 = new Intent(SelectedCourseView.this, EnrollmentDetailsActivity.class);
                            intent2.putExtra("instructorName", instructorNameClicked);
                            intent2.putExtra("noOfSessions", noOfSessionsClicked);
                            intent2.putExtra("courseId", courseId); // Pass the courseId for fetching session details
                            intent2.putExtra("rating", rating);
                            intent2.putExtra("description", course_description);
                            startActivity(intent2);
                        });
                    }

                    runOnUiThread(() -> {
                        // Add card views to the cards container
                        LinearLayout cardsContainer = findViewById(R.id.cardsContainer);
                        cardsContainer.removeAllViews(); // Clear existing views

                        for (View cardView : cardViews) {
                            cardsContainer.addView(cardView);
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();


    }

    private int storeAsGoal(int courseId) {

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] projection = {
                "no_of_sessions",
                "instructor_name",
                "course_name"
        };

        String selection = "course_id = ?";
        String[] selectionArgs = { String.valueOf(courseId) };

        Cursor cursor = db.query(
                "CoursesOffered",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {

            int no_of_sessions = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("no_of_sessions")));
            String instructor_name = cursor.getString(cursor.getColumnIndexOrThrow("instructor_name"));
            String course_name = cursor.getString(cursor.getColumnIndexOrThrow("course_name"));

            SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
            String username = preferences.getString("username", null);
            String user_name = null;
            if (username != null) {
                // You have the username, and you can use it in your app
                // For example, set it in a TextView
                Log.i(TAG,"username"+username);
                ContentValues values = new ContentValues();
                values.put("user_name", username); // Set the user's ID
                values.put("course_name", course_name);
                values.put("no_of_sessions", no_of_sessions);
                values.put("instructor_name", instructor_name);
                values.put("status", "new");

                long goalId = db.insert("Goal", null, values);
                if (goalId != -1) {
                    Log.i(TAG,"inserted to goal db");
                    Toast.makeText(this,"Added as Goal",Toast.LENGTH_SHORT).show();
                    return 1;

                } else {
                    // There was an error saving the goal
                    // Handle the error as needed
                    return 0;
                }
            } else {
                return 0;
                // Username is not available (e.g., user is not logged in)
            }

        }


        db.close();
        return 1;
    }

    // Method to update status to "in progress" in the goal database
    private int updateStatusToInProgress(int courseId) {
        // Implement your database operations to update the status to "in progress"
        // You can use your MyDatabaseHelper for this purpose
        // Example:
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String[] projection = {
                "no_of_sessions",
                "instructor_name",
                "course_name"
        };

        String selection = "course_id = ?";
        String[] selectionArgs = {String.valueOf(courseId)};

        Cursor cursor = db.query(
                "CoursesOffered",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {

            int no_of_sessions = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("no_of_sessions")));
            String instructor_name = cursor.getString(cursor.getColumnIndexOrThrow("instructor_name"));
            String course_name = cursor.getString(cursor.getColumnIndexOrThrow("course_name"));

            SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
            String username = preferences.getString("username", null);
            String user_name = null;
            if (username != null) {
                Cursor cursor2 = db.query(
                        "Goal",
                        null, // Columns (null means all columns)
                        "course_name = ? AND instructor_name = ? AND no_of_sessions = ?",
                        new String[]{course_name, instructor_name, String.valueOf(no_of_sessions)},
                        null, // Group by
                        null, // Having
                        null  // Order by
                );

                if (cursor.getCount() > 0) {
                    // If a record with the same details exists, update its status
                    values.put("status", "In Progress");
                    db.update("Goal", values, "course_name = ? AND instructor_name = ? AND no_of_sessions = ?",
                            new String[]{course_name, instructor_name, String.valueOf((no_of_sessions))});
                    Toast.makeText(this,"Enrolled for the course",Toast.LENGTH_SHORT).show();
                return 1;
                } else {
                    // If no matching record exists, create a new record
                    values.put("user_name", username); // Set the user_name accordingly
                    values.put("course_name", course_name);
                    values.put("instructor_name", instructor_name);// Set the goal text accordingly
                    values.put("no_of_sessions", no_of_sessions);
                    values.put("status", "In Progress");

                    db.insert("Goal", null, values);
                    return  1;
                }
            }


            cursor.close();
            mSessionAdapter.notifyDataSetChanged();

            db.close();

        } return 1;

    }

    // Method to fetch instructor name based on course ID
    private String fetchInstructorName(int instructor_id) {
        String instructorName = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, name, usertype FROM signup WHERE id = ?")) {

            // Set the parameter using PreparedStatement to prevent SQL injection
            statement.setInt(1, instructor_id);

            // Execute the query
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    instructorName = resultSet.getString("name");

                    Log.i(TAG, "instructor name " + instructorName);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return instructorName;
    }

    // Method to fetch total sessions based on course ID
    private int fetchTotalSessions(int courseId) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "no_of_sessions"
        };

        String selection = "course_id = ?";
        String[] selectionArgs = { String.valueOf(courseId) };

        Cursor cursor = db.query(
                "CoursesOffered",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int totalSessions = 0;
        if (cursor.moveToFirst()) {
            totalSessions = cursor.getInt(cursor.getColumnIndexOrThrow("no_of_sessions"));
        }

        cursor.close();
        db.close();

        return totalSessions;
    }

    // Method to fetch session details based on course ID
    private List<courseSessions> fetchSessionDetails(int courseId) {
        List<courseSessions> sessions = new ArrayList<>();

        // Initialize your database helper and open the database
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define the columns you want to retrieve from the SessionForCourse table
        String[] projection = {
                "session_name",
                "start_date",
                "start_time"
        };

        // Define the selection criteria (WHERE clause)
        String selection = "course_id = ?";
        String[] selectionArgs = { String.valueOf(courseId) };

        // Execute the query
        Cursor cursor = db.query(
                "SessionForCourse", // Table name
                projection,        // Columns to retrieve
                selection,         // WHERE clause
                selectionArgs,     // Selection arguments
                null,              // GROUP BY clause (if any)
                null,              // HAVING clause (if any)
                null               // ORDER BY clause (if any)
        );

        // Iterate through the cursor and add session details to the list
        while (cursor.moveToNext()) {

            String sessionName = cursor.getString(cursor.getColumnIndexOrThrow("session_name"));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date"));
            String startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));

            // Create a Session object and add it to the list
            courseSessions session = new courseSessions(sessionName, startDate, startTime);
            sessions.add(session);
        }

        // Close the cursor and database
        cursor.close();
        db.close();

        return sessions;
    }

    // Method to display session details in a dialog



    public void onPreviousPageClick(View view) {
        if (currentPage > 1) {
            currentPage--;
            recreate(); // Reload the activity to show the previous page
        }
    }

    public void onNextPageClick(View view) {
        // Check if there are more items to display
        if (itemCounter >= currentPage * ITEMS_PER_PAGE) {
            currentPage++;
            recreate(); // Reload the activity to show the next page
        }
    }


}
