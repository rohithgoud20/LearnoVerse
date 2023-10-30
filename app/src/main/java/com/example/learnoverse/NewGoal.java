package com.example.learnoverse;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class NewGoal extends DialogFragment {

    private EditText courseNameEditText;
    private EditText sessionsEditText;
    private EditText instructorNameEditText;

    public interface NewGoalDialogListener {
        void onNewGoalDialogPositiveClick(String courseName, int sessions, String instructorName);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());

        // Open the database for reading
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        View view = inflater.inflate(R.layout.dialog_new_goal, null);

        courseNameEditText = view.findViewById(R.id.editTextCourseName);
//        sessionsEditText = view.findViewById(R.id.editTextSessions);
//        instructorNameEditText = view.findViewById(R.id.editTextInstructorName);

        builder.setView(view)
                .setTitle("Set Up New Goal")
                .setPositiveButton("Save", (dialog, id) -> {
                    String courseName = courseNameEditText.getText().toString();
                    Intent intent = new Intent(requireContext(), SelectedCourseView.class);
                        intent.putExtra("course_name", courseName);
                        startActivity(intent);
//                    String[] projection = {
//                            "course_id",
//                            "instructor_name",
//                            "no_of_sessions",
//                            "rating"
//                    };
//
//                    // Define the selection criteria (WHERE clause)
//                    String selection = "course_name = ?";
//                    String[] selectionArgs = {courseName};
//
//                    // Execute the query
//                    Cursor cursor = db.query(
//                            "CoursesOffered", // Table name
//                            projection,        // Columns to retrieve
//                            selection,         // WHERE clause
//                            selectionArgs,     // Selection arguments
//                            null,              // GROUP BY clause (if any)
//                            null,              // HAVING clause (if any)
//                            null               // ORDER BY clause (if any)
//                    );
//
//                    // Iterate through the cursor and populate the table
//                    while (cursor.moveToNext()) {
//                        String course_id = cursor.getString(cursor.getColumnIndexOrThrow("course_id"));

//
//                    }
//                    int sessions = Integer.parseInt(sessionsEditText.getText().toString());
//                    String instructorName = instructorNameEditText.getText().toString();
//
//                    NewGoalDialogListener listener = (NewGoalDialogListener) getActivity();
//                    listener.onNewGoalDialogPositiveClick(courseName, sessions, instructorName);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }
}
