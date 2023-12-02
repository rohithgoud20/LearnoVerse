package com.example.learnoverse;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InstructorCourses extends AppCompatActivity {

    // Replace these values with your actual database credentials
    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";
    public static final String TABLE_NAME = "CoursesOffered";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_courses);

        // Fetch courses and display on the UI
        getCoursesForEmailAndDisplay();

        Button newCourseButton = findViewById(R.id.newCourseButton);
        newCourseButton.setOnClickListener(v -> showNewCourseDialog());
    }

    private void showNewCourseDialog() {
        NewCourseDialogFragment dialogFragment = new NewCourseDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "NewCourseDialog");
    }


    private void getCoursesForEmailAndDisplay() {
        List<Course> courses = new ArrayList<>();

        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        Integer userid = preferences.getInt("userid", 0);

        new Thread(() -> {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                // Retrieve courses data based on the entered email
                String query = "SELECT * FROM " + "CoursesOffered" + " WHERE instructor_id='" + userid + "'";
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    // Replace these column names with your actual database column names
                    String name = resultSet.getString("course_name");
                    String description = resultSet.getString("course_description");
                    int sessions = resultSet.getInt("no_of_sessions");
                    double price = resultSet.getDouble("price");
                    String meetingLink = resultSet.getString("meeting_link");
                    // Create Course object and add to the list
                    Log.d(TAG, "DB QUERY" + name + description);
                    Course course = new Course(name, description, sessions, price, meetingLink);
                    courses.add(course);
                }

                // Close resources
                resultSet.close();
                statement.close();
                connection.close();

                // After fetching data, update the UI on the main thread
                runOnUiThread(() -> displayCourses(courses));

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private void displayCourses(List<Course> courses) {
        // Get references to UI elements
        LinearLayout coursesContainer = findViewById(R.id.coursesContainer);

        for (Course course : courses) {
            View courseCard = LayoutInflater.from(this).inflate(R.layout.instructor_course_cards_layout, null);
            TextView courseNameTextView = courseCard.findViewById(R.id.courseNameTextView);
            TextView courseDescriptionTextView = courseCard.findViewById(R.id.courseDescriptionTextView);
            TextView sessionsTextView = courseCard.findViewById(R.id.sessionsTextView);
            TextView priceTextView = courseCard.findViewById(R.id.priceTextView);
            TextView meetingLinkTextView = courseCard.findViewById(R.id.meetingLinkTextView);

            // Set data dynamically
            courseNameTextView.setText(course.getName());
            courseDescriptionTextView.setText(course.getDescription());
            sessionsTextView.setText("Sessions: " + course.getSessions());
            priceTextView.setText("Price: $" + course.getPrice());
            meetingLinkTextView.setText("Meeting Link: " + course.getMeetingLink());

            // Add the course card to the container
            coursesContainer.addView(courseCard);
        }
    }
}
