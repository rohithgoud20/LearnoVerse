package com.example.learnoverse;// InstructorRegisteredStudents.java

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InstructorRegisteredStudents extends AppCompatActivity {

    // Replace these values with your actual database credentials
    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";
    public static final String TABLE_NAME_COURSES = "CoursesOffered";
    public static final String TABLE_NAME_SESSIONS = "RegisteredCourses";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_registered_students);

        // Assuming you have the instructor's ID
       // int instructorId = 1; // Replace with the actual instructor ID
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        int instructorId = preferences.getInt("userid", 0);
        // 0 is the default value if "user_id" is not found
        Log.d(TAG,"instrcutor id"+ instructorId);

        fetchAndDisplayRegisteredStudents(instructorId);
    }

    private void fetchAndDisplayRegisteredStudents(int instructorId) {
        new Thread(() -> {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);

                // Fetch courses offered by the instructor
                String queryCourses = "SELECT * FROM " + TABLE_NAME_COURSES + " WHERE instructor_id = ?";
                try (PreparedStatement statementCourses = connection.prepareStatement(queryCourses)) {
                    statementCourses.setInt(1, instructorId);
                    ResultSet resultSetCourses = statementCourses.executeQuery();

                    while (resultSetCourses.next()) {
                        int courseId = resultSetCourses.getInt("course_id");
                        String courseName = resultSetCourses.getString("course_name");

                        // Fetch students registered for each course
                        List<StudentInfo> studentsList = new ArrayList<>();

                        String querySessions = "SELECT * FROM " + TABLE_NAME_SESSIONS + " WHERE selected_course = ?";
                        try (PreparedStatement statementSessions = connection.prepareStatement(querySessions)) {
                            statementSessions.setInt(1, courseId);
                            ResultSet resultSetSessions = statementSessions.executeQuery();

                            while (resultSetSessions.next()) {
                                String studentName = resultSetSessions.getString("user_name");
                                boolean paymentDone = resultSetSessions.getBoolean("ispayment_done");
                                String learnerComment = resultSetSessions.getString("learner_comment");

                                // Add student information to the list
                                studentsList.add(new StudentInfo(studentName, paymentDone, learnerComment));
                            }
                        }

                        runOnUiThread(() -> {
                            // Dynamically create and add a card for each course with all students
                            addCourseCard(courseId,courseName, studentsList);
                        });
                    }
                }

                // Close resources
                connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void addCourseCard(Integer courseId, String courseName, List<StudentInfo> studentsList) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.activity_instructor_registered_students, null);

        TextView courseNameTextView = cardView.findViewById(R.id.courseNameTextView);
        LinearLayout studentsContainer = cardView.findViewById(R.id.studentsContainer);
        CardView noStudentsTextView = cardView.findViewById(R.id.NoStudents);
        CardView cardviewstudent = cardView.findViewById(R.id.cardviewstudent);
        LinearLayout cardcontainer = cardView.findViewById(R.id.cardsContainer);

        // Set course name
        courseNameTextView.setText(courseName);

        if (studentsList.isEmpty()) {
            // If there are no students, show the "No Students" TextView and hide the studentsContainer
            noStudentsTextView.setVisibility(View.VISIBLE);
            studentsContainer.setVisibility(View.GONE);
        } else {
            // If there are students, hide the "No Students" TextView and show the studentsContainer
            cardviewstudent.setVisibility(View.VISIBLE);
            noStudentsTextView.setVisibility(View.GONE);
            studentsContainer.setVisibility(View.VISIBLE);

            // Add students dynamically
            for (StudentInfo student : studentsList) {
                View studentCard = inflater.inflate(R.layout.student_card_layout, null);

                // Set student information
                TextView studentNameTextView = studentCard.findViewById(R.id.studentNameTextView);
                TextView paymentDoneTextView = studentCard.findViewById(R.id.paymentDoneTextView);
                TextView learnerCommentTextView = studentCard.findViewById(R.id.learnerCommentTextView);
                Button scheduleMeetButton = studentCard.findViewById(R.id.sendMeetLinkButton);
                EditText meetingLinkEditText = studentCard.findViewById(R.id.meetingLinkEditText);

                studentNameTextView.setText("Student: " + student.getName());
                paymentDoneTextView.setText("Payment Done: " + (student.isPaymentDone() ? "Yes" : "No"));
                learnerCommentTextView.setText("Learner Comment: " + student.getLearnerComment());

                // Check if a meeting link is present for the course in the database
//                boolean meetingLinkExists = checkMeetingLinkExists(courseId);

                // Check if a meeting link is present for the course in the database
                new Thread(() -> {
                    try (Connection connection = DriverManager.getConnection(url, username, password)) {
                        String query = "SELECT meeting_link FROM CoursesOffered WHERE course_id = ?";
                        try (PreparedStatement statement = connection.prepareStatement(query)) {
                            statement.setInt(1, courseId);
                            ResultSet resultSet = statement.executeQuery();
                            boolean meetingLinkExists = resultSet.next(); // Move to the first row
                            String meetinglink;

                            if (meetingLinkExists) {
                                meetinglink = resultSet.getString("meeting_link");
                            } else {
                                meetinglink = null;
                            }
                            //String meetinglink = resultSet.next() ? resultSet.getString("meeting_link") : null;
                            Log.d(TAG,"meeting link"+courseId+meetinglink+meetingLinkExists);
                            runOnUiThread(() -> { // Use runOnUiThread to update UI components
                                if (meetingLinkExists) {
                                    // If meeting link exists, display it in the EditText and change button text to "Update Meet Link"
                                    String currentMeetingLink = meetinglink;
                                    meetingLinkEditText.setText(currentMeetingLink);
                                    scheduleMeetButton.setText("Update Meet Link");
                                    // Make the EditText non-editable
                                    meetingLinkEditText.setEnabled(true);
                                } else {
                                    // If no meeting link exists, clear the EditText and change button text to "Send Meet Link"
                                    meetingLinkEditText.setText("");
                                    scheduleMeetButton.setText("Send Meet Link");
                                    // Make the EditText editable
                                    meetingLinkEditText.setEnabled(true);
                                }
                            });
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }).start();



                // Handle the button click based on the button text
                scheduleMeetButton.setOnClickListener(view -> {
                    // Handle the click event here
                    // You can open a new activity, show a dialog, etc.
                    String meetingLink = meetingLinkEditText.getText().toString();

                    Log.d(TAG, "course_id: " + courseId + " meetingLink: " + meetingLink);
                    new Thread(() -> {
                        try (Connection connection = DriverManager.getConnection(url, username, password)) {
                            String updateQuery = "UPDATE CoursesOffered SET meeting_link = ? WHERE course_id = ?";
                            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                                statement.setString(1, meetingLink);
                                statement.setInt(2, courseId);
                                int rowsAffected = statement.executeUpdate();

                                if (rowsAffected > 0) {
                                    // Meeting link updated successfully
                                    Log.d(TAG, "Meeting link updated successfully");
                                    SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                                    String login_email_id = preferences.getString("login_email_id", "");

                                    // Check if the context is valid before showing the Toast
                                    if (InstructorRegisteredStudents.this != null) {
                                        runOnUiThread(() -> Toast.makeText(InstructorRegisteredStudents.this, "Updated the meet link", Toast.LENGTH_SHORT).show());
                                    }

                                    String query2 = "SELECT * FROM RegisteredCourses WHERE selected_course=?";
                                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(query2)) {
                                        preparedStatement2.setInt(1, courseId);
                                        try (ResultSet resultSet2 = preparedStatement2.executeQuery()) {
                                            while (resultSet2.next()) {
                                                String user_name = resultSet2.getString("user_name");
                                                String notificationText = "Instructor updated the schedule link for your registered Course " + courseName;
                                                NotificationUtils.insertNewNotification(user_name, notificationText, new NotificationUtils.InsertNotificationCallback() {
                                                    @Override
                                                    public void onNotificationInserted(boolean success) {
                                                        if (success) {
                                                            Log.d(TAG, "NOTIFICATION INSERTED");
                                                            // Handle successful insertion
                                                        } else {
                                                            Log.d(TAG, "NOTIFICATION FAILED");
                                                            // Handle insertion failure
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    // No rows affected, meeting link update failed
                                    Log.d(TAG, "Failed to update meeting link");

                                    // Check if the context is valid before showing the Toast
                                    if (InstructorRegisteredStudents.this != null) {
                                        runOnUiThread(() -> Toast.makeText(InstructorRegisteredStudents.this, "Failed to update the meet link", Toast.LENGTH_SHORT).show());
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }).start();

                });

                studentsContainer.addView(studentCard);
            }
        }

        // Get reference to the cards container and add the card
        LinearLayout cardsContainer = findViewById(R.id.cardsContainer);
        cardsContainer.addView(cardView);
    }

    // Method to check if a meeting link exists for a given courseId
    // Method to check if a meeting link exists for a given courseId


    private void openMeetingLink(String meetingLink) {
        // Create an Intent to open a web page
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // Set the data of the Intent to the meeting URL
        intent.setData(Uri.parse(meetingLink));

        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(meetingLink));

        // Check if there is an activity that can handle this Intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the activity
            startActivity(intent);
        } else {
            // Provide user feedback if no activity is available to handle the Intent
            Toast.makeText(InstructorRegisteredStudents.this, "No app installed to handle the meeting link", Toast.LENGTH_SHORT).show();
        }
    }

}
