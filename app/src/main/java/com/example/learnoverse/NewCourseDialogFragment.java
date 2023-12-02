package com.example.learnoverse;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewCourseDialogFragment extends DialogFragment {

    private EditText sessionsEditText;
    private LinearLayout sessionsContainer;
    private LinearLayout page2Container;
    private Button nextButton;
    private Button submitButton;
    private TextView selectedDateTextView;
    private int selectedSession = 0;
    private List<String> selectedDates = new ArrayList<>();

    @NonNull
    @Override
    public AlertDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_new_course_dialog, null);
        initViews(view);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Offer New Course")
                .setNegativeButton("Cancel", null)
                .create();
    }

    private void initViews(View view) {
        sessionsEditText = view.findViewById(R.id.sessionsEditText);
        sessionsContainer = view.findViewById(R.id.sessionsContainer);
        page2Container = view.findViewById(R.id.page2Container);
        nextButton = view.findViewById(R.id.nextButton);
        submitButton = view.findViewById(R.id.submitButton);
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView);

        nextButton.setOnClickListener(v -> showPage2());
        submitButton.setOnClickListener(v -> onSubmitClicked());
    }

    private void showPage2() {
        String sessionsStr = sessionsEditText.getText().toString();
        if (TextUtils.isEmpty(sessionsStr)) {
            sessionsEditText.setError("Enter the number of sessions");
            return;
        }

        int numSessions = Integer.parseInt(sessionsStr);
        if (numSessions <= 0) {
            sessionsEditText.setError("Number of sessions must be greater than 0");
            return;
        }

        // Hide page 1, show page 2
        page2Container.setVisibility(View.VISIBLE);

        // Initialize date picker
        initDatePicker();

        // Initialize session input views
        initSessionInputViews(numSessions);
    }

    private void initDatePicker() {
        View datePickerView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_session_input, null);
        FrameLayout datePickerContainer = view.findViewById(R.id.datePickerContainer);
        datePickerContainer.removeAllViews();
        datePickerContainer.addView(datePickerView);

        DatePicker sessionDatePicker = datePickerView.findViewById(R.id.sessionDatePicker);
        Button okButton = datePickerView.findViewById(R.id.datePickerOkButton);

        okButton.setOnClickListener(v -> {
            selectedDates.add(getFormattedDate(sessionDatePicker));

            // Display selected date
            selectedDateTextView.setText(getString(R.string.selected_date, selectedDates.size(), selectedDates.get(selectedDates.size() - 1)));

            // Create session input views dynamically based on the selected date
            createSessionInputViews(selectedDates.size());

            // Enable/disable submit button based on the number of sessions and selected dates
            submitButton.setEnabled(selectedDates.size() == Integer.parseInt(sessionsEditText.getText().toString()));
        });
    }

    private void createSessionInputViews(int sessionNumber) {
        View sessionView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_session_input, sessionsContainer, false);
        TextView sessionNumberTextView = sessionView.findViewById(R.id.sessionNumberTextView);
        sessionNumberTextView.setText(getString(R.string.session_number, sessionNumber));

        sessionsContainer.addView(sessionView);
    }

    private String getFormattedDate(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1; // Month is 0-indexed
        int year = datePicker.getYear();

        return String.format("%02d-%02d-%04d", day, month, year);
    }

    private void onSubmitClicked() {
        // Implement your logic to handle the submission of course details, including session information
        // Retrieve data from input fields and proceed accordingly

        String courseName = ((EditText) getView().findViewById(R.id.courseNameEditText)).getText().toString();
        double price = Double.parseDouble(((EditText) getView().findViewById(R.id.priceEditText)).getText().toString());
        String meetingLink = ((EditText) getView().findViewById(R.id.meetingLinkEditText)).getText().toString();
        String courseDescription = ((EditText) getView().findViewById(R.id.courseDescriptionEditText)).getText().toString();

        List<String> sessionsList = new ArrayList<>();
        for (int i = 1; i <= sessionsContainer.getChildCount(); i++) {
            View sessionView = sessionsContainer.getChildAt(i - 1);
            EditText sessionNameEditText = sessionView.findViewById(R.id.sessionNameEditText);
            EditText sessionTimeEditText = sessionView.findViewById(R.id.sessionTimeEditText);

            String sessionName = sessionNameEditText.getText().toString();
            String sessionTime = sessionTimeEditText.getText().toString();

            sessionsList.add(getString(R.string.session_details, i, sessionName, sessionTime));
        }

        // Handle your data as needed

        dismiss();
    }
}
