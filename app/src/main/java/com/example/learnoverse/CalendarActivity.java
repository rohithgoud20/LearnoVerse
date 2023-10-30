package com.example.learnoverse;

import android.content.Context;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import android.util.AttributeSet;
import java.util.List;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private TextView dueDetailsTextView;

    private CustomCalendarView customCalendarView;


    // Dummy list of DueItems
    private List<DueItem> dueItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Initialize the CalendarView and TextView
        calendarView = findViewById(R.id.calendar);
        dueDetailsTextView = findViewById(R.id.dueDetailsTextVw);

        customCalendarView = findViewById(R.id.customCalendarView);

        // Dummy data for DueItems
        dueItems = new ArrayList<DueItem>();
        dueItems.add(new DueItem("Course 1", "Instructor 1", "2023-11-15"));
        dueItems.add(new DueItem("Course 2", "Instructor 2", "2023-11-20"));

        // Set a date change listener for the CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            displayDueDetails(selectedDate);
        });
    }

    private void displayDueDetails(String selectedDate) {
        StringBuilder details = new StringBuilder("Dues on " + selectedDate + ":\n\n");
        for (DueItem item : dueItems) {
            if (item.getDueDate().equals(selectedDate)) {
                details.append("Course: ").append(item.getCourseName()).append("\n");
                details.append("Instructor: ").append(item.getInstructorName()).append("\n\n");
            }
        }
        dueDetailsTextView.setText(details.toString());
    }

}

