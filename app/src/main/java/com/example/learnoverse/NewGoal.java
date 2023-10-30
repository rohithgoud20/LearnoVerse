package com.example.learnoverse;

import android.app.Dialog;
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
        View view = inflater.inflate(R.layout.dialog_new_goal, null);

        courseNameEditText = view.findViewById(R.id.editTextCourseName);
        sessionsEditText = view.findViewById(R.id.editTextSessions);
        instructorNameEditText = view.findViewById(R.id.editTextInstructorName);

        builder.setView(view)
                .setTitle("Set Up New Goal")
                .setPositiveButton("Save", (dialog, id) -> {
                    String courseName = courseNameEditText.getText().toString();
                    int sessions = Integer.parseInt(sessionsEditText.getText().toString());
                    String instructorName = instructorNameEditText.getText().toString();

                    NewGoalDialogListener listener = (NewGoalDialogListener) getActivity();
                    listener.onNewGoalDialogPositiveClick(courseName, sessions, instructorName);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }
}
