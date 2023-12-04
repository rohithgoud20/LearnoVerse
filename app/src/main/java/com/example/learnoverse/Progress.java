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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Progress extends AppCompatActivity {

    // Replace these values with your actual database credentials
    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";
    public static final String TABLE_NAME = "CoursesOffered";
    public static final String TABLE_NAME_REGISTERED_COURSES = "RegisteredCourses";
    public static final String TABLE_NAME_COURSES_OFFERED = "CoursesOffered";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_page);

        // Example: Replace "user@example.com" with the actual user's email id
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        int  userid= preferences.getInt("userid", 0);

        Log.d(TAG, "email id"+userid);

        // Run database operations in a new thread
        new Thread(() -> {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                // Retrieve user data based on the entered email
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + "signup" + " WHERE id='" + userid + "'");
                String login_email_id = null;
                if (resultSet.next()) {
                     login_email_id = resultSet.getString("emailid");
                     Log.d(TAG,"email id from db"+ login_email_id);
                    // Toast.makeText(Progress.this, "email id "+ login_email_id, Toast.LENGTH_SHORT).show();
                   // Toast.makeText(EnrollmentDetailsActivity.this, "Enrolled for the course successfully", Toast.LENGTH_SHORT).show();

                }
                // Fetch registered courses for the user
                List<RegisteredCourseInfo> registeredCourses = getRegisteredCourses(connection, login_email_id);

                // Update UI on the main thread
                runOnUiThread(() -> {
                    // Dynamically add course cards
                    for (RegisteredCourseInfo courseInfo : registeredCourses) {
                        addCourseCard(courseInfo);
                    }
                });

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

    private List<RegisteredCourseInfo> getRegisteredCourses(Connection connection, String userEmail) throws SQLException {
        List<RegisteredCourseInfo> registeredCourses = new ArrayList<>();

        // Fetch registered courses for the user from RegisteredCourses table
        String queryRegisteredCourses = "SELECT * FROM " + TABLE_NAME_REGISTERED_COURSES + " WHERE user_name = ?";
        Log.d(TAG, "email id" + userEmail);

        try (PreparedStatement statementRegisteredCourses = connection.prepareStatement(queryRegisteredCourses)) {
            statementRegisteredCourses.setString(1, userEmail);
            ResultSet resultSetRegisteredCourses = statementRegisteredCourses.executeQuery();
            Log.d(TAG, "email id" + resultSetRegisteredCourses);

            while (resultSetRegisteredCourses.next()) {
                try {
                    int selectedCourseId = resultSetRegisteredCourses.getInt("selected_course");
                    String status = resultSetRegisteredCourses.getString("status");
                    boolean isPaymentDone = resultSetRegisteredCourses.getBoolean("ispayment_done");

                    // Fetch course details from CoursesOffered table
                    String queryCourseDetails = "SELECT * FROM " + TABLE_NAME_COURSES_OFFERED + " WHERE course_id = ?";
                    try (PreparedStatement statementCourseDetails = connection.prepareStatement(queryCourseDetails)) {
                        statementCourseDetails.setInt(1, selectedCourseId);
                        ResultSet resultSetCourseDetails = statementCourseDetails.executeQuery();

                        if (resultSetCourseDetails.next()) {

                            String courseName = resultSetCourseDetails.getString("course_name");
                            int instructorId = resultSetCourseDetails.getInt("instructor_id");
                           // int courseId = resultSetCourseDetails.getInt("course_id");


                            // Fetch instructor details in the main thread before starting a new thread
                            String instructorName = getInstructorName(connection, instructorId);

                            float rating = resultSetCourseDetails.getFloat("rating");
                            int noOfSessions = resultSetCourseDetails.getInt("no_of_sessions");
                            String description = resultSetCourseDetails.getString("course_description");

                            // Create a RegisteredCourseInfo object and add to the list
                            RegisteredCourseInfo courseInfo = new RegisteredCourseInfo(
                                    selectedCourseId, courseName, instructorName, rating, noOfSessions, description,
                                    status, isPaymentDone);
                            Log.d(TAG, "registered courses " + courseName + instructorName);
                            registeredCourses.add(courseInfo);
                        }
                    }
                } catch (SQLException e) {
                    // Log the specific row causing the issue and continue with the next row
                    Log.e(TAG, "Error processing result set row: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing query for registered courses: " + e.getMessage());
        }

        return registeredCourses;
    }

    private String getInstructorName(Connection connection, int instructorId) throws SQLException {
        String instructorName = null;

        String query = "SELECT id, password, salt, name, usertype FROM " + "signup" + " WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, instructorId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                instructorName = resultSet.getString("name");
            }
        }

        return instructorName;
    }


//    private String getInstructorName(int instructorId) {
//        // Implement logic to fetch instructor name based on instructorId
//        // You can execute a query or use any other method to get the instructor name
//
//        new Thread(() -> {
//            try {
//                Connection connection = DriverManager.getConnection(url, username, password);
//                Statement statement = connection.createStatement();
//
//                // Retrieve user data based on the entered email
//                ResultSet resultSet = statement.executeQuery("SELECT id, password, salt,name, usertype FROM " + TABLE_NAME + " WHERE id='" + instructorId + "'");
//
//                if (resultSet.next()) {
//                    // User found, compare passwords
//                   String  name = resultSet.getString("name");
//
//
//                }
//            }catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
//        return name;
//    }

    private void addCourseCard(RegisteredCourseInfo courseInfo) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.progress_list_item, null);

        TextView courseNameTextView = cardView.findViewById(R.id.courseNameTextView);
        TextView instructorNameTextView = cardView.findViewById(R.id.instructorNameTextView);
        TextView ratingTextView = cardView.findViewById(R.id.ratingTextView);
        TextView noOfSessionsTextView = cardView.findViewById(R.id.noOfSessionsTextView);
        TextView descriptionTextView = cardView.findViewById(R.id.descriptionTextView);
        //ProgressBar progressBar = cardView.findViewById(R.id.progressBar);
        ProgressBar horizontalProgressBar = cardView.findViewById(R.id.horizontalProgressBar);

// Set the progress value (you can replace 50 with your desired value)

        ImageView courseIconImageView = cardView.findViewById(R.id.courseIconImageView);
        //
        // Set course details
        courseNameTextView.setText(courseInfo.getCourseName());
        String lowercaseCourseName = courseInfo.getCourseName().toLowerCase();
        int resourceId = getResources().getIdentifier(
                lowercaseCourseName,
                "drawable",
                getPackageName()
        );
        if (resourceId != 0) {
            courseIconImageView.setImageResource(resourceId);
        } else {
            // Use a default icon or handle the case when the resource is not found
            courseIconImageView.setImageResource(R.drawable.coding);
        }

        new Thread(() -> {
            try {
                //int numberOfSessions = 0;int attendedSessionsCount=0;
                // Establish the database connection
                Connection connection = DriverManager.getConnection(url, username, password);

                // Get the number of sessions for a given course from CoursesOffered
                String query1 = "SELECT no_of_sessions FROM CoursesOffered WHERE course_id = ?";
               // int courseId = 1; // Replace with the actual course ID you want to query
                try (PreparedStatement preparedStatement1 = connection.prepareStatement(query1)) {
                    preparedStatement1.setInt(1, courseInfo.getSelectedCourseId());

                    try (ResultSet resultSet1 = preparedStatement1.executeQuery()) {
                        if (resultSet1.next()) {
                             int numberOfSessions = resultSet1.getInt("no_of_sessions");
                            System.out.println("Number of Sessions for Course " + courseInfo.getSelectedCourseId() + ": " + numberOfSessions);
                            String query2 = "SELECT COUNT(sessionid) AS attended_sessions_count FROM attendance WHERE courseid = ? AND attended_lecture = 1";
                            try (PreparedStatement preparedStatement2 = connection.prepareStatement(query2)) {
                                preparedStatement2.setInt(1, courseInfo.getSelectedCourseId());

                                try (ResultSet resultSet2 = preparedStatement2.executeQuery()) {
                                    if (resultSet2.next()) {
                                        int attendedSessionsCount = resultSet2.getInt("attended_sessions_count");
                                        System.out.println("Attended Sessions Count for Course " + courseInfo.getSelectedCourseId() + ": " + attendedSessionsCount);
                                        if(attendedSessionsCount!=0){
                                            horizontalProgressBar.setProgress(attendedSessionsCount*100/numberOfSessions);
                                        }
                                        else{
                                            horizontalProgressBar.setProgress(0);
                                        }

                                    }
                                }
                            }

                        }
                    }
                }

                // Get the count of attended sessions for a given course from attendance


            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();


        instructorNameTextView.setText("Instructor: " + courseInfo.getInstructorName());
        ratingTextView.setText("Rating: " + courseInfo.getRating());
        noOfSessionsTextView.setText("Sessions: " + courseInfo.getNoOfSessions());
        descriptionTextView.setText(courseInfo.getDescription());
        //progressBar.setProgress(courseInfo.getProgress());

        LinearLayout cardsContainer = findViewById(R.id.cardsContainer);

        // Add the inflated view to the cards container
        cardsContainer.addView(cardView);
    }

}
