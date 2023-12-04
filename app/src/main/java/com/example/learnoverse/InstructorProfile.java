package com.example.learnoverse;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

// Import statements...

public class InstructorProfile extends AppCompatActivity {

    private int year, month, day;
    private Button dateOfBirthButton;
    private EditText dateOfBirthEditText, firstNameEditText, lastNameEditText, emailEditText, qualificationEditText, expert, exprerience;
    private Button subbut;
    private RadioGroup genderRadioGroup;
    private Spinner availabilitySpinner;
    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin";
    public static final String password = "learnoverse";
    public static final String TABLE_NAME = "instructorprofiles";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_profile);
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        firstNameEditText = findViewById(R.id.firstname);
        lastNameEditText = findViewById(R.id.lastn);
        dateOfBirthButton = findViewById(R.id.dateOfBirthButton);
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText);
        emailEditText = findViewById(R.id.emailI);
        qualificationEditText = findViewById(R.id.qInstr);
        availabilitySpinner = findViewById(R.id.availabilitySpinner); // Correct ID
        expert = findViewById(R.id.expert);
        exprerience = findViewById(R.id.exp);
        subbut = findViewById(R.id.conButton);
        emailEditText.setText(email);

        // Find the RadioGroup in your layout and assign it to genderRadioGroup
        genderRadioGroup = findViewById(R.id.lge); // Correct ID

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.availability_options,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availabilitySpinner.setAdapter(adapter);

        dateOfBirthButton.setOnClickListener(v -> showDialog(999));

        subbut.setOnClickListener(v -> {
            String firstName = getStringFromEditText(firstNameEditText);
            String lastName = getStringFromEditText(lastNameEditText);
            String dateOfBirth = getStringFromEditText(dateOfBirthEditText);
            String emailid = getStringFromEditText(emailEditText);
            String qualification = getStringFromEditText(qualificationEditText);
            String availability = availabilitySpinner.getSelectedItem().toString();
            String expertValue = getStringFromEditText(expert);
            String experience = getStringFromEditText(exprerience);

            // Check if genderRadioGroup is not null before proceeding
            if (genderRadioGroup != null) {
                int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedGenderRadioButton = findViewById(selectedGenderId);
                String gender = (selectedGenderRadioButton != null) ? selectedGenderRadioButton.getText().toString() : "";

                insertInstrucData(emailid, firstName, lastName, dateOfBirth, gender, qualification, availability, expertValue, experience);

                Intent newIntent = new Intent(InstructorProfile.this, MainActivity.class);
                startActivity(newIntent);
            } else {
                Log.e("InstructorProfile", "genderRadioGroup is null");
            }
        });
    }

    private void insertInstrucData(String emaili, String firstName, String lastName, String dateOfBirth, String gender, String qualification, String availability, String expert, String experience) {
        new Thread(() -> {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                // Construct the SQL query (Note: This is more vulnerable to SQL injection)
                String query = "INSERT INTO " + TABLE_NAME +
                        " (email, first_name, last_name, date_of_birth, gender, availability, highest_qualification, expertise, experience) " +
                        "VALUES ('" + emaili + "', '" + firstName + "', '" + lastName + "', '" + dateOfBirth + "', '" +
                        gender + "', '" + availability + "', '" + qualification + "', '" + expert + "','" + experience + "')";

                int rowsAffected = statement.executeUpdate(query);

                if (rowsAffected > 0) {
                    Log.i("InstructorProfile", "Data inserted successfully.");
                } else {
                    Log.e("InstructorProfile", "Failed to insert data.");
                }

                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = (view, year, month, day) -> {
        // Process the selected date
        String selectedDate = year + "-" + (month + 1) + "-" + day; // Format the date as needed
        dateOfBirthEditText.setText(selectedDate); // Update the date in the EditText
    };

    private String getStringFromEditText(EditText editText) {
        return editText.getText().toString().trim();
    }
}
