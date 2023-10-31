package com.example.learnoverse;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.database.Cursor;

public class learnerprofile extends AppCompatActivity {

    private int year, month, day;
    private Button dateOfBirthButton;
    private EditText dateOfBirthEditText;
    private Button submitbut;

    private EditText firstNameEditText, lastNameEditText, emailEditText,
            phoneNumberEditText, qualificationEditText, interestsEditText;

    private RadioGroup genderRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learnerprofile);
        Intent intent = getIntent();
        String user_name = intent.getStringExtra("user_name");

        dateOfBirthButton = findViewById(R.id.dateOfBirthButton); // Make sure to use the correct ID
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText); // Use the correct ID for the EditText
        submitbut=findViewById(R.id.continueButton);
        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        emailEditText = findViewById(R.id.email);
        phoneNumberEditText = findViewById(R.id.phone_number);
        qualificationEditText = findViewById(R.id.qualification);
        interestsEditText = findViewById(R.id.interests);
        emailEditText.setText(user_name);

        genderRadioGroup = findViewById(R.id.gender);
        dateOfBirthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999); // Show the date picker dialog
            }
        });
        submitbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String dateOfBirth = dateOfBirthEditText.getText().toString();
//                String email = emailEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();
                String qualification = qualificationEditText.getText().toString();
                String interests = interestsEditText.getText().toString();

                int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedGenderRadioButton = findViewById(selectedGenderId);
                String gender = selectedGenderRadioButton.getText().toString();


                MyDatabaseHelper dbHelper = new MyDatabaseHelper(learnerprofile.this);

                // Get a writable database
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Create a ContentValues object to hold the data
                ContentValues values = new ContentValues();
                values.put("first_name", firstName);
                values.put("last_name", lastName);
                values.put("date_of_birth", dateOfBirth);
                values.put("gender", gender);
                values.put("username", user_name);
                values.put("email", user_name);
                values.put("phone_number", phoneNumber);
                values.put("highest_qualification", qualification);
                values.put("interests", interests);

                // Insert the data into the "learner" table
                long newRowId = db.insert("learner", null, values);

                // Close the database
                db.close();

                // Check if the insertion was successful
                if (newRowId != -1) {
                    Toast.makeText(learnerprofile.this, "Saved your profile! Please Login", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(learnerprofile.this, "Data insertion failed", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(learnerprofile.this, MainActivity.class);
                startActivity(intent);

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