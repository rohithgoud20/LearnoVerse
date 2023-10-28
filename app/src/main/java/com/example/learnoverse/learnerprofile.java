package com.example.learnoverse;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class learnerprofile extends AppCompatActivity {

    private int year, month, day;
    private Button dateOfBirthButton;
    private EditText dateOfBirthEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learnerprofile);

        dateOfBirthButton = findViewById(R.id.dateOfBirthButton); // Make sure to use the correct ID
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText); // Use the correct ID for the EditText

        dateOfBirthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999); // Show the date picker dialog
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Process the selected date
            String selectedDate = year + "-" + (month + 1) + "-" + day; // Format the date as needed
            dateOfBirthEditText.setText(selectedDate); // Update the date in the EditText
        }
    };
}