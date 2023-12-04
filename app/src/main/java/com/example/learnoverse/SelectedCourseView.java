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

import androidx.annotation.Nullable;
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
    private static final int ENROLLMENT_REQUEST_CODE = 1;
    private int currentPage = 1;
    private static final int ITEMS_PER_PAGE = 7; // Adjust the number of items per page
    private int itemCounter = 0; // Declare itemCounter at the class level
    private RecyclerView mSessionRecyclerView;
    private SessionAdapter mSessionAdapter;
    private boolean isExpanded = false;
    private Button enrollButton;
    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";
    public static final String TABLE_NAME = "CoursesOffered";
    public interface RegistrationCallback {
        void onRegistrationChecked(boolean isRegistered);
    }
    public void checkUserRegistration(int courseId, String userName, RegistrationCallback callback) {
        new Thread(() -> {
            boolean isRegistered = false;

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(
                         "SELECT * FROM RegisteredCourses WHERE selected_course = ? AND user_name = ?")) {

                statement.setInt(1, courseId);
                statement.setString(2, userName);

                try (ResultSet resultSet = statement.executeQuery()) {
                    isRegistered = resultSet.next();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Invoke the callback with the result
            callback.onRegistrationChecked(isRegistered);
        }).start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_course_view);

        Intent intent = getIntent();
        String course_name = intent.getStringExtra("course_name");
        AtomicInteger course_id = new AtomicInteger();
        Log.i(TAG, "selected course name " + course_name);

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(
                         "SELECT course_id, instructor_id, no_of_sessions, rating, course_description,price FROM CoursesOffered WHERE course_name = ?")) {

                statement.setString(1, course_name);
                Log.d(TAG, "db query " + "SELECT course_id,instructor_id,rating,course_description,price FROM CoursesOffered WHERE course_name = " + course_name);

                try (ResultSet resultSet = statement.executeQuery()) {
                    List<View> cardViews = new ArrayList<>();

                    while (resultSet.next()) {
                        int courseId = resultSet.getInt("course_id");
                        int instructor_id = resultSet.getInt("instructor_id");
                        String instructorName = fetchInstructorName(instructor_id);
                        int noOfSessions = resultSet.getInt("instructor_id");
                        float rating = resultSet.getFloat("rating");
                        String course_description = resultSet.getString("course_description");
                        float price = resultSet.getFloat("price");
                        Log.d(TAG, "resultset " + courseId + instructorName);

                        View cardView = LayoutInflater.from(this).inflate(R.layout.courses_cards_layout, null);

                        TextView instructorNameTextView = cardView.findViewById(R.id.instructorNameTextView);
                        TextView ratingTextView = cardView.findViewById(R.id.ratingTextView);
                        TextView noOfSessionsTextView = cardView.findViewById(R.id.noOfSessionsTextView);
                        TextView descriptionTextView = cardView.findViewById(R.id.descriptionTextView);

                        instructorNameTextView.setText(instructorName);
                        ratingTextView.setText("Rating: " + String.valueOf(rating));
                        noOfSessionsTextView.setText("Sessions: " + String.valueOf(noOfSessions));
                        descriptionTextView.setText(course_description);

                        cardViews.add(cardView);

                         enrollButton = cardView.findViewById(R.id.buttonEnroll);
                        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                        String login_email_id = preferences.getString("login_email_id", "");

                        checkUserRegistration(courseId, login_email_id, isRegistered -> {
                            runOnUiThread(() -> {
                                enrollButton.setEnabled(!isRegistered);
                                if(isRegistered){
                                    enrollButton.setText("Enrolled");
                                }


                                enrollButton.setOnClickListener(v -> {
                                    TextView instructorNameTextViewClicked = cardView.findViewById(R.id.instructorNameTextView);
                                    TextView noOfSessionsTextViewClicked = cardView.findViewById(R.id.noOfSessionsTextView);

                                    String instructorNameClicked = instructorNameTextViewClicked.getText().toString();
                                    String noOfSessionsClicked = noOfSessionsTextViewClicked.getText().toString();

                                    Intent intent2 = new Intent(SelectedCourseView.this, EnrollmentDetailsActivity.class);
                                    intent2.putExtra("instructorName", instructorNameClicked);
                                    intent2.putExtra("noOfSessions", noOfSessionsClicked);
                                    intent2.putExtra("courseId", courseId);
                                    intent2.putExtra("rating", rating);
                                    intent2.putExtra("description", course_description);
                                    intent2.putExtra("price", price);
                                    startActivity(intent2);
                                });
                            });
                        });
                    }

                    runOnUiThread(() -> {
                        LinearLayout cardsContainer = findViewById(R.id.cardsContainer);
                        cardsContainer.removeAllViews();

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ENROLLMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Enrollment was successful, update UI or disable the button

                int enrolledCourseId = data.getIntExtra("enrolledCourseId", -1);
                String login_email_id = data.getStringExtra("login_email_id");

                // Check the enrollment status and disable the enroll button if needed
                //checkUserRegistrationStatus(enrolledCourseId,login_email_id, enrollButton);
            }
        }
    }

    private void checkUserRegistrationStatus(int courseId,String login_email_id, Button enrollButton) {
        checkUserRegistration(courseId, login_email_id, isRegistered -> {
            if (isRegistered) {
                disableEnrollButton(enrollButton);
            }
        });
    }

    private void disableEnrollButton(Button enrollButton) {
        enrollButton.setEnabled(false);
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

}
