package com.example.learnoverse;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SelectedCourseView extends AppCompatActivity  {
    private int currentPage = 1;
    private static final int ITEMS_PER_PAGE = 7; // Adjust the number of items per page
    private int itemCounter = 0; // Declare itemCounter at the class level
    private RecyclerView mSessionRecyclerView;
    private SessionAdapter mSessionAdapter;
    private boolean isExpanded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_course_view);

        // Get a reference to the TableLayout
        TableLayout courseDetailsTable = findViewById(R.id.courseDetailsTable);

        // Initialize the database helper
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);

        // Open the database for reading
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Intent intent = getIntent();
        String course_name = intent.getStringExtra("course_name");
        Log.i(TAG, "selected course name " +course_name);


        // The selected course ID (you need to set this based on your logic)
//        int selectedCourseId = Integer.parseInt(courseId); // Replace with the actual selected course ID

        // Define the columns you want to retrieve
        String[] projection = {
                "course_id",
                "instructor_name",
                "no_of_sessions",
                "rating"
        };

        // Define the selection criteria (WHERE clause)
        String selection = "course_name = ?";
        String[] selectionArgs = { course_name };

        // Execute the query
        Cursor cursor = db.query(
                "CoursesOffered", // Table name
                projection,        // Columns to retrieve
                selection,         // WHERE clause
                selectionArgs,     // Selection arguments
                null,              // GROUP BY clause (if any)
                null,              // HAVING clause (if any)
                null               // ORDER BY clause (if any)
        );

        // Iterate through the cursor and populate the table
        while (cursor.moveToNext()) {
            if (itemCounter >= (currentPage - 1) * ITEMS_PER_PAGE) {
                Integer selectedCourseId = cursor.getInt(cursor.getColumnIndexOrThrow("course_id"));
                String instructorName = cursor.getString(cursor.getColumnIndexOrThrow("instructor_name"));
                int noOfSessions = cursor.getInt(cursor.getColumnIndexOrThrow("no_of_sessions"));
                float rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));

                // Create a new TableRow and populate it with course details
                TextView msg = findViewById(R.id.Instructorlist);
                TextView selected_instructor = findViewById(R.id.instructor_name);
                TextView selected_noofsessions = findViewById(R.id.no_of_sessions);
                CardView instructors = findViewById(R.id.instructors);
                msg.setText("Available instructors for "+ course_name);
                TableRow row = new TableRow(this);
                row.setClickable(true);

                TextView courseNameTextView = new TextView(this);
//                courseNameTextView.setText(courseName);

                TextView instructorNameTextView = new TextView(this);
                instructorNameTextView.setText(instructorName);

                TextView noOfSessionsTextView = new TextView(this);
                noOfSessionsTextView.setText(String.valueOf(noOfSessions));
                Button buttonSetAsGoal = findViewById(R.id.buttonSetAsGoal);
                Button buttonEnroll = findViewById(R.id.buttonEnroll);

                TextView ratingTextView = new TextView(this);
                ratingTextView.setText(String.valueOf(rating));

                row.setTag(selectedCourseId);
                // Set the course ID as a tag
                mSessionRecyclerView = findViewById(R.id.sessionsRecyclerView);
                mSessionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mSessionAdapter = new SessionAdapter(new ArrayList<courseSessions>()); // Initialize with an empty list
                mSessionRecyclerView.setAdapter(mSessionAdapter);

                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Update the TextViews with the retrieved data
                        int clickedCourseId = (int) view.getTag();
                        String instructorName = fetchInstructorName(clickedCourseId);
                        int totalSessions = fetchTotalSessions(clickedCourseId);
                        // Update the TextViews within the CardView with the retrieved data
                        selected_instructor.setText("Instructor Name - " + instructorName);
                        selected_noofsessions.setText("Total No. of Sessions - " + totalSessions);

                        // Fetch and update the list of sessions here
                        List<courseSessions> sessions = fetchSessionDetails(clickedCourseId);
                        mSessionAdapter.updateData(sessions);
                        instructors.setVisibility(View.VISIBLE);
                        buttonSetAsGoal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle "Set as Goal" button click
                                // Store the details into the goal database with status as "new"
                                Integer res = storeAsGoal(selectedCourseId);
                                if(res==1){
                                    buttonSetAsGoal.setEnabled(false);
                                }
                            }
                        });

                        buttonEnroll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle "Enroll" button click
                                // Update the status as "in progress" in the goal database
                                int res =updateStatusToInProgress(selectedCourseId);
                                if(res==1){
                                    buttonSetAsGoal.setEnabled(false);
                                }

                            }
                        });
                    }

                });

//                row.addView(courseNameTextView);
                row.addView(instructorNameTextView);
                row.addView(noOfSessionsTextView);
                row.addView(ratingTextView);

                instructorNameTextView.setGravity(Gravity.CENTER);
                noOfSessionsTextView.setGravity(Gravity.CENTER);
                ratingTextView.setGravity(Gravity.CENTER);

                row.setClickable(true);
                int rowCounter =0;
                row.setEnabled(rowCounter % 2 == 0); // Set the enabled state for alternating colors
                row.setBackgroundResource(R.drawable.table_border); // Set the divider background
                courseDetailsTable.addView(row);
                rowCounter++;
            }

            itemCounter++;

            if (itemCounter >= currentPage * ITEMS_PER_PAGE) {
                break;
            }
        }

        // Close the cursor and database
        cursor.close();
        db.close();
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
    private String fetchInstructorName(int courseId) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "instructor_name"
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

        String instructorName = null;
        if (cursor.moveToFirst()) {
            instructorName = cursor.getString(cursor.getColumnIndexOrThrow("instructor_name"));
        }

        cursor.close();
        db.close();
        Log.i(TAG,"instructor name "+ instructorName);

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
