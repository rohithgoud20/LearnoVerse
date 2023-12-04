package com.example.learnoverse;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;
   // private final ArrayList<Session> sessions;
    private boolean[] selectedDates;
    private final Context context;
    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";
    public static final String TABLE_NAME = "signup";
    private ArrayList<LocalDate> daysWithSessions;

    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener, ArrayList<LocalDate> daysWithSessions, Context context) {
        this.days = days;
        this.onItemListener = onItemListener;
        this.daysWithSessions = daysWithSessions;
        this.context = context;
        this.selectedDates = new boolean[days.size()];
    }


    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(days.size() > 15) //month view
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        else // week view
            layoutParams.height = (int) parent.getHeight();

        return new CalendarViewHolder(view, onItemListener, days);
    }

    public void setSelected(int position, boolean isSelected) {
        selectedDates[position] = isSelected;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        final LocalDate date = days.get(position);
        if (date == null)
            holder.dayOfMonth.setText("");
        else {
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if (date.equals(CalendarUtils.selectedDate) || daysWithSessions.contains(date))
                holder.parentView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary1));
            else
                holder.parentView.setBackgroundColor(Color.TRANSPARENT);
        }


        // Check if the date has a session
        boolean hasSession = hasSessionForDate(date);

        // Highlight the date if it has a session
        if (hasSession) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary1));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }




    }

    @Override
    public int getItemCount()
    {
        return days.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, LocalDate date);
    }

    private boolean hasSessionForDate(LocalDate date) {
        AtomicBoolean hasSession = new AtomicBoolean(false);

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                String formattedDate = date.toString();

                // Construct your SQL query to check if there is a session for the specific date
                String query = "SELECT COUNT(*) FROM SessionsForCourse WHERE start_date = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, formattedDate);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            int sessionCount = resultSet.getInt(1);
                            hasSession.set(sessionCount > 0);
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the exception as needed
            }
        }).start();

        return hasSession.get();
    }

}