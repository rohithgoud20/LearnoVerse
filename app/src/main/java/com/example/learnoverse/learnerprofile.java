package com.example.learnoverse;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class learnerprofile extends AppCompatActivity {

    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username= "admin";
    public static final String password = "learnoverse";
    public static final String TABLE_NAME = "learnerprofilestbs";

    private Button dateOfBirthButton;
    private EditText dateOfBirthEditText;
    private Button submitButton;
    private int year, month, day;
    private EditText firstNameEditText, lastNameEditText, emailEditText,
            phoneNumberEditText, qualificationEditText, interestsEditText;

    private RadioGroup genderRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learnerprofile);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        dateOfBirthButton = findViewById(R.id.dateOfBirthButton);
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText);
        submitButton = findViewById(R.id.continueButton);
        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        emailEditText = findViewById(R.id.email);
        phoneNumberEditText = findViewById(R.id.phone_number);
        qualificationEditText = findViewById(R.id.qualification);
        interestsEditText = findViewById(R.id.interests);
        emailEditText.setText(email);

        genderRadioGroup = findViewById(R.id.gender);

        dateOfBirthButton.setOnClickListener(v -> showDialog(999));
//        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
//        String emai = preferences.getString("login_email_id", "");

        submitButton.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String dateOfBirth = dateOfBirthEditText.getText().toString();
            String phoneNumber = phoneNumberEditText.getText().toString();
            String qualification = qualificationEditText.getText().toString();
            String interests = interestsEditText.getText().toString();

            int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedGenderRadioButton = findViewById(selectedGenderId);
            String gender = selectedGenderRadioButton.getText().toString();

            insertLearnerData(email, firstName, lastName, dateOfBirth, gender, phoneNumber, qualification, interests);
            Intent newIntent = new Intent(learnerprofile.this, MainActivity.class);
            startActivity(newIntent);
        });

    }

    private void insertLearnerData(String email, String firstName, String lastName, String dateOfBirth, String gender, String phoneNumber, String qualification, String interests) {
        new Thread(() -> {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                // Construct the SQL query (Note: This is more vulnerable to SQL injection)
                String query = "INSERT INTO " + TABLE_NAME +
                        " (email, first_name, last_name, date_of_birth, gender, phone_number, highest_qualification, interests) " +
                        "VALUES ('" + email + "', '" + firstName + "', '" + lastName + "', '" + dateOfBirth + "', '" +
                        gender + "', '" + phoneNumber + "', '" + qualification + "', '" + interests + "')";

                int rowsAffected = statement.executeUpdate(query);

                if (rowsAffected > 0) {
                    System.out.println("Data inserted successfully.");
                } else {
                    System.out.println("Failed to insert data.");
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

    private final DatePickerDialog.OnDateSetListener myDateListener = (view, year, month, day) -> {
        String selectedDate = year + "-" + (month + 1) + "-" + day;
        dateOfBirthEditText.setText(selectedDate);
    };
}