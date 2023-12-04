package com.example.learnoverse;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;
    private CalendarAdapter calendarAdapter;  // Declare CalendarAdapter variable
    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";
    public static final String TABLE_NAME = "signup";
    public ArrayList<LocalDate> daysWithSessions = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        initWidgets();

        if (CalendarUtils.selectedDate == null) {
            CalendarUtils.selectedDate = LocalDate.now();
        }
        setWeekView();
    }

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        eventListView = findViewById(R.id.eventListView);
    }

    private void setWeekView() {
        if (CalendarUtils.selectedDate != null) {
            ArrayList<LocalDate> days = CalendarUtils.daysInWeekArray(CalendarUtils.selectedDate);

            //fetchSessionsForWeek(days, this::onSessionsFetched);
        }
    }

    private void setWeekView(ArrayList<LocalDate> daysWithSessions) {
        if (CalendarUtils.selectedDate != null) {
            monthYearText.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate));
            ArrayList<LocalDate> days = CalendarUtils.daysInWeekArray(CalendarUtils.selectedDate);

            // Pass the days with sessions to the adapter
            calendarAdapter = new CalendarAdapter(days, this, daysWithSessions, this);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
            calendarRecyclerView.setLayoutManager(layoutManager);
            calendarRecyclerView.setAdapter(calendarAdapter);
            setEventAdapter();
        }
    }

    private void updateUIWithSessions(ArrayList<Session> sessions) {
        // Implement UI updates based on fetched sessions
        // For example, update the calendar cells or display session info
        // This method will be called on the main thread
        // You can update your UI, e.g., highlight dates, display session info, etc.
        // Implement this method based on your UI requirements
        // For example, you can store the fetched sessions in a member variable and use it in onBindViewHolder
        // Or you can have a callback to notify the adapter about the updated sessions
    }


    public void onSessionsFetched(ArrayList<LocalDate> daysWithSessions) {
        runOnUiThread(() -> updateUIWithDaysWithSessions(daysWithSessions));
    }

    private void fetchSessionsForWeek(ArrayList<LocalDate> days) {
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String login_email_id = preferences.getString("login_email_id", "");
        ArrayList<Session> sessions = new ArrayList<>();
        new Thread(() -> {
            try {
                try (Connection connection = DriverManager.getConnection(url, username, password)) {
                    // Step 1: Fetch course IDs for the registered courses of the user
                    String fetchCourseIdsQuery = "SELECT rc.selected_course, co.course_name " +
                            "FROM RegisteredCourses rc " +
                            "JOIN CoursesOffered co ON rc.selected_course = co.course_id " +
                            "WHERE rc.user_name = ?";

                    try (PreparedStatement fetchCourseIdsStatement = connection.prepareStatement(fetchCourseIdsQuery)) {
                        fetchCourseIdsStatement.setString(1, login_email_id);

                        try (ResultSet courseIdsResultSet = fetchCourseIdsStatement.executeQuery()) {
                            while (courseIdsResultSet.next()) {
                                int courseId = courseIdsResultSet.getInt("selected_course");
                                String courseName = courseIdsResultSet.getString("course_name");

                                // Step 2: Fetch session details for each course ID
                                String fetchSessionDetailsQuery = "SELECT session_id,session_name, start_date, start_time FROM SessionsForCourse WHERE course_id = ?";
                                try (PreparedStatement fetchSessionDetailsStatement = connection.prepareStatement(fetchSessionDetailsQuery)) {
                                    fetchSessionDetailsStatement.setInt(1, courseId);

                                    try (ResultSet sessionDetailsResultSet = fetchSessionDetailsStatement.executeQuery()) {
                                        while (sessionDetailsResultSet.next()) {
                                            Integer session_id = sessionDetailsResultSet.getInt("session_id");
                                            String sessionName = sessionDetailsResultSet.getString("session_name");
                                            String startDate = sessionDetailsResultSet.getString("start_date");
                                            String startTime = sessionDetailsResultSet.getString("start_time");
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                            LocalDate start_Date = LocalDate.parse(startDate, formatter);
                                            daysWithSessions.add(start_Date);
                                            Log.d(TAG, "DayswithSessions" + daysWithSessions);

                                            System.out.println("Course ID: " + courseId);
                                            System.out.println("Course Name: " + courseName);
                                            System.out.println("Session Name: " + sessionName);
                                            System.out.println("Session Date: " + startDate);
                                            System.out.println("Session Time: " + startTime);
                                            System.out.println("-----------------------------");
                                            Session session = new Session(session_id,courseId,courseName, sessionName, startDate, startTime);
                                            sessions.add(session);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //callback.onSessionsFetched(daysWithSessions);
        }).start();
    }

    private void updateUIWithDaysWithSessions(ArrayList<LocalDate> daysWithSessions) {
        setWeekView(daysWithSessions);
    }

    public void previousWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    public void nextWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        if (calendarAdapter != null) {
            calendarAdapter.setSelected(position, true); // Set selected state
            setWeekView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setEventAdapter();
    }

    private void setEventAdapter() {
        ArrayList<Event> dailyEvents = Event.eventsForDate(CalendarUtils.selectedDate);
        EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), dailyEvents);
        eventListView.setAdapter(eventAdapter);
    }

    public void newEventAction(View view) {
        startActivity(new Intent(this, EventEditActivity.class));
    }
}
