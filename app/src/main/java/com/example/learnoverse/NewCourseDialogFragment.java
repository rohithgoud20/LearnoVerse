package com.example.learnoverse;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.lang.reflect.Field;

public class NewCourseDialogFragment extends DialogFragment {

    private EditText sessionsEditText;
    private LinearLayout sessionsContainer;
    private Button nextButton;
    private Button submitButton;
    private EditText courseNameEditText;
    private EditText priceEditText;
    private EditText meetingLinkEditText;
    private EditText courseDescriptionEditText;
    private List<Sessions> sessionsList;
    private NewCourseDialogListener listener;

    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";
    public static final String TABLE_NAME = "CoursesOffered";

    public interface NewCourseDialogListener {
        void onNewCourseSubmit(String courseName, List<String> sessions, double price, String meetingLink, String courseDescription);
        void onCoursesUpdated();
    }


    @NonNull
    @Override
    public AlertDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_new_course_dialog, null);
        initViews(view);
        sessionsList = new ArrayList<>();

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Offer New Course")
                .setNegativeButton("Cancel", null)
                .create();
    }

    private void initViews(View view) {
        sessionsEditText = view.findViewById(R.id.sessionsEditText);
        sessionsContainer = view.findViewById(R.id.sessionsContainer);
        nextButton = view.findViewById(R.id.nextButton);
        submitButton = view.findViewById(R.id.submitButton);
        courseNameEditText = view.findViewById(R.id.courseNameEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        meetingLinkEditText = view.findViewById(R.id.meetingLinkEditText);
        courseDescriptionEditText = view.findViewById(R.id.courseDescriptionEditText);

        nextButton.setOnClickListener(v -> {
            showDatePicker(view);
            // Hide the nextButton after it's clicked
            nextButton.setVisibility(View.GONE);
        });
        submitButton.setOnClickListener(v -> onSubmitClicked());
    }

    private void showDatePicker(View view) {
        View datePickerView = LayoutInflater.from(requireContext()).inflate(R.layout.session_dropdown_item, null);
        FrameLayout datePickerContainer = view.findViewById(R.id.datePickerContainer);
        datePickerContainer.removeAllViews();
        datePickerContainer.addView(datePickerView);

        DatePicker datePicker = datePickerView.findViewById(R.id.datePicker);
        EditText sessionNameEditText = datePickerView.findViewById(R.id.sessionNameEditText);
        EditText sessionTimeEditText = datePickerView.findViewById(R.id.sessionTimeEditText);
        Button okButton = datePickerView.findViewById(R.id.datePickerOkButton);

        // Set min date to the current date
        datePicker.setMinDate(System.currentTimeMillis() - 1000);

        okButton.setOnClickListener(v -> {
            String selectedDate = getFormattedDate(datePicker);

            // Check if the selected date is not in the past
            if (isDateInPast(datePicker)) {
                Toast.makeText(requireContext(), "Please choose a future date", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if there is already a session for the selected date and time
            if (isSessionAlreadyExists(selectedDate, sessionTimeEditText.getText().toString())) {
                Toast.makeText(requireContext(), "Session already exists for this date and time", Toast.LENGTH_SHORT).show();
                return;
            }

            // Enable/disable submit button based on the number of sessions and selected dates
            Log.d("TAG", "sessions size" + sessionsList.size() + Integer.parseInt(sessionsEditText.getText().toString()));
            submitButton.setEnabled(sessionsList.size() + 1 == Integer.parseInt(sessionsEditText.getText().toString()));

            // Display selected date in green
            highlightSelectedDate(datePicker);

            // Display selected date
            String formattedDate = getFormattedDate(datePicker);
            String sessionDetails = String.format("Session Date: %s\nSession Name: %s\nSession Time: %s",
                    formattedDate,
                    sessionNameEditText.getText().toString(),
                    sessionTimeEditText.getText().toString());
            sessionsContainer.addView(createSessionView(sessionDetails));

            // Add the session to the sessionsList
            sessionsList.add(new Sessions(selectedDate, sessionTimeEditText.getText().toString(), sessionNameEditText.getText().toString()));
        });

        showDatePickerDialog(datePicker);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NewCourseDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement NewCourseDialogListener");
        }
    }

    private boolean isSessionAlreadyExists(String selectedDate, String selectedTime) {
        for (Sessions session : sessionsList) {
            if (session.getDate().equals(selectedDate) && session.getTime().equals(selectedTime)) {
                return true; // Session already exists for the selected date and time
            }
        }
        return false; // No session found for the selected date and time
    }

    private void highlightSelectedDate(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        // Reset the background color for all days
        resetDayPickerBackgroundColor(datePicker);

        // Highlight the selected date
        highlightDay(datePicker, day, month, year);
    }

    private void resetDayPickerBackgroundColor(DatePicker datePicker) {
        try {
            Field ll = datePicker.getClass().getDeclaredField("mDayPicker");
            ll.setAccessible(true);
            Object llObj = ll.get(datePicker);
            Field colorField = llObj.getClass().getDeclaredField("mSelectedColor");
            colorField.setAccessible(true);
            colorField.setInt(llObj, Color.TRANSPARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void highlightDay(DatePicker datePicker, int day, int month, int year) {
        try {
            Field ll = datePicker.getClass().getDeclaredField("mDayPicker");
            ll.setAccessible(true);
            Object llObj = ll.get(datePicker);
            Field[] fields = llObj.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mSelectedDay".equals(field.getName())) {
                    field.setAccessible(true);
                    int[][] mSelectedDay = (int[][]) field.get(llObj);
                    mSelectedDay[0] = new int[]{year, month, day};
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isDateInPast(DatePicker datePicker) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

        Calendar currentDate = Calendar.getInstance();

        return selectedDate.before(currentDate);
    }

    private String getFormattedDate(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1; // Month is zero-based
        int year = datePicker.getYear();

        return String.format(Locale.getDefault(), "%02d-%02d-%d", day, month, year);
    }

    private void showDatePickerDialog(DatePicker datePicker) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // No need to do anything here; the logic is handled in the OK button click
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Set min date to current date
        datePickerDialog.show();
    }

    private View createSessionView(String sessionDetails) {
        View sessionView = LayoutInflater.from(requireContext()).inflate(R.layout.session_details_item, sessionsContainer, false);
        TextView sessionDateTextView = sessionView.findViewById(R.id.sessionDateTextView);
        TextView sessionNameTextView = sessionView.findViewById(R.id.sessionNameTextView);
        TextView sessionTimeTextView = sessionView.findViewById(R.id.sessionTimeTextView);

        // Split the session details and set them to respective TextViews
        String[] detailsArray = sessionDetails.split("\n");
        sessionDateTextView.setText(detailsArray[0]);
        sessionNameTextView.setText(detailsArray[1]);
        sessionTimeTextView.setText(detailsArray[2]);

        return sessionView;
    }

    private void onSubmitClicked() {
        // ... existing code ...
        List<Course> courses = new ArrayList<>();
        // Example: Print sessionsList details
        for (Sessions session : sessionsList) {
            System.out.println("Session Date: " + session.getDate() + ", Session Time: " + session.getTime() + ", Session Name: " + session.getSessionName());
        }
        SharedPreferences preferences = requireContext().getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        int instructorId = preferences.getInt("userid", 0); // 0 is the default value if "user_id" is not found
        Log.d(TAG,"instrcutor id"+ instructorId);

        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement insertCourseStatement = connection.prepareStatement(
                         "INSERT INTO CoursesOffered (course_name, course_description, instructor_id, meeting_link, no_of_sessions, rating, price) VALUES (?, ?, ?, ?, ?, ?, ?)",
                         PreparedStatement.RETURN_GENERATED_KEYS)) {
                // Set the parameters using PreparedStatement to prevent SQL injection

                insertCourseStatement.setString(1, courseNameEditText.getText().toString());
                insertCourseStatement.setString(2, courseDescriptionEditText.getText().toString());
                insertCourseStatement.setInt(3, instructorId); // Replace with the actual instructor ID
                insertCourseStatement.setString(4, meetingLinkEditText.getText().toString());
                insertCourseStatement.setInt(5, sessionsList.size()); // Assuming sessionsList contains the sessions count
                insertCourseStatement.setDouble(6, 2.0); // Set the initial rating
                insertCourseStatement.setDouble(7, Double.parseDouble(priceEditText.getText().toString())); // Replace with the actual price

                // Execute the insert statement
                int rowsInserted = insertCourseStatement.executeUpdate();

                if (rowsInserted > 0) {
                    // Insert successful, you can display a success message or navigate to another screen

                    ResultSet generatedKeys = insertCourseStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int courseId = generatedKeys.getInt(1);

                        String insertSessionQuery = "INSERT INTO SessionsForCourse (course_id, session_name, start_date, start_time) VALUES (?, ?, ?, ?)";

                        try (PreparedStatement insertSessionStatement = connection.prepareStatement(insertSessionQuery)) {
                            for (Sessions session : sessionsList) {
                                insertSessionStatement.setInt(1, courseId);
                                insertSessionStatement.setString(2, session.getSessionName());
                                insertSessionStatement.setString(3, session.getDate());
                                insertSessionStatement.setString(4, session.getTime());

                                // Execute the query for each session
                                insertSessionStatement.executeUpdate();
//                                Course course = new Course(courseNameEditText.getText().toString(),
//                                        courseDescriptionEditText.getText().toString(),
//                                        sessionsList.size(), Double.parseDouble(priceEditText.getText().toString()),
//                                        meetingLinkEditText.getText().toString());
//                                courses.add(course);
                            }
                        }
                    }
                    new android.os.Handler(requireContext().getMainLooper()).post(() -> {
                        Toast.makeText(requireContext(), "Course and sessions inserted successfully", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onCoursesUpdated();
                        }
                    });
                } else {
                    // Insert failed, you can display an error message
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the SQL exception, you can display an error message
                new android.os.Handler(requireContext().getMainLooper()).post(() -> {
                    Toast.makeText(requireContext(), "Course and sessions inserted failed", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();

        // ... existing code ...
    }


}
