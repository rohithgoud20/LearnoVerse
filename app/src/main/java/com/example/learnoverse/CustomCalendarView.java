package com.example.learnoverse;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.CalendarView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomCalendarView extends GridView {

    private List<Calendar> datesWithDues;

    public CustomCalendarView(Context context) {
        super(context);
        init();
    }

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Initialize the list of dates with dues
        datesWithDues = new ArrayList<>();

        customizeCalendarDates();
    }

    private void customizeCalendarDates() {
        // Iterate through the list of datesWithDues
        for (Calendar date : datesWithDues) {
            int year = date.get(Calendar.YEAR);
            int month = date.get(Calendar.MONTH);
            int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);

            // Calculate the date in milliseconds
            date.set(year, month, dayOfMonth, 0, 0);
            long dateInMillis = date.getTimeInMillis();

            // Set the background color for the specific date
            setDateBackgroundColor(dateInMillis, Color.RED);
        }
    }

    private void setDateBackgroundColor(long dateInMillis, int color) {

    }
}
