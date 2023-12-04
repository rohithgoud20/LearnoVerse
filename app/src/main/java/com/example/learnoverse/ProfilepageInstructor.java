package com.example.learnoverse;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProfilepageInstructor extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private ImageView profileImage;

    private TextView profileName;
    private EditText dobText;
    private EditText mobile;
    private EditText gender;
    private EditText qualification;
    private EditText expertise;
    private EditText experience, availb;
    private ImageView editDob, editMobile, editGender, editQualification, editExpertise, editExperience, editavailability;
    private Button saveButton;
    private SharedPreferences preferences;
    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin";
    public static final String password = "learnoverse";
    public static final String TABLE_NAME = "instructorprofiles";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage_instructor);
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String emailId = preferences.getString("login_email_id", "");

        // Initialize UI components
        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        dobText = findViewById(R.id.dobText);
        mobile = findViewById(R.id.mobile);
        gender = findViewById(R.id.gender);
        availb = findViewById(R.id.availability);
        qualification = findViewById(R.id.qualification);
        expertise = findViewById(R.id.expertise);
        experience = findViewById(R.id.exprience);
        editDob = findViewById(R.id.editDob);
        editMobile = findViewById(R.id.editmobile);
        editGender = findViewById(R.id.editgender);
        editQualification = findViewById(R.id.editQualification);
        editExpertise = findViewById(R.id.editExpertise);
        editExperience = findViewById(R.id.editexprience);
        editavailability = findViewById(R.id.editavail);
        saveButton = findViewById(R.id.saveButton);

        try {
            fillData();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set Click Listeners
        editDob.setOnClickListener(v -> {
            Log.d("ImageViewClick", "Edit Date of Birth ImageView clicked.");
            dobText.requestFocus();
        });

        editMobile.setOnClickListener(v -> {
            setEditTextsEditable(true);
            mobile.requestFocus();
        });

        editGender.setOnClickListener(v -> {
            setEditTextsEditable(true);
            gender.requestFocus();
        });

        editQualification.setOnClickListener(v -> {
            setEditTextsEditable(true);
            qualification.requestFocus();
        });

        editExpertise.setOnClickListener(v -> {
            setEditTextsEditable(true);
            expertise.requestFocus();
        });

        editExperience.setOnClickListener(v -> {
            setEditTextsEditable(true);
            experience.requestFocus();
        });
        editavailability.setOnClickListener(v -> {
            setEditTextsEditable(true);
            availb.requestFocus();
        });

        // Set an OnClickListener for the "Save" button
        saveButton.setOnClickListener(v -> {
            // Retrieve values from UI components
            String firstName = profileName.getText().toString();
            String dob = dobText.getText().toString();
            String mobileNumber = mobile.getText().toString();
            String genderValue = gender.getText().toString();
            String qualificationValue = qualification.getText().toString();
            String expertiseValue = expertise.getText().toString();
            String experienceValue = experience.getText().toString();
            String availvalue = availb.getText().toString();
            Drawable drawable = profileImage.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            byte[] profilePicture = convertBitmapToByteArray(bitmap);

            // Validate input before saving
            if (validateInput(firstName, dob, mobileNumber, genderValue, qualificationValue, expertiseValue, experienceValue, availvalue)) {
                // Call saveProfile method
                saveProfile(emailId, firstName, "last_name_placeholder", dob, genderValue, mobileNumber,
                        qualificationValue, expertiseValue, experienceValue, availvalue, profilePicture);
                Toast.makeText(getApplicationContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fillData() throws SQLException {
        new Thread(() -> {
            StringBuilder records = new StringBuilder();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                // Fetch data based on login_email_id
                SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                String emailId = preferences.getString("login_email_id", "");

                // Use a PreparedStatement to prevent SQL injection
                String query = "SELECT * FROM instructorprofiles WHERE email = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, emailId);

                    ResultSet rs = preparedStatement.executeQuery();

                    if (rs.next()) {
                        // Fetch data and set EditText fields
                        String profileN = rs.getString("first_name");
                        String dob = rs.getString("date_of_birth");
                        String mobileNumber = rs.getString("phone_number");
                        String qualifi = rs.getString("highest_qualification");
                        String exp = rs.getString("experience");
                        String gen = rs.getString("gender");
                        String expert = rs.getString("expertise");
                        String ab = rs.getString("availability");
                        byte[] profilePictureData = rs.getBytes("profile_image");
                        if (profilePictureData != null) {
                            Log.d("ProfileImageDataLength", String.valueOf(profilePictureData.length));
                            runOnUiThread(() -> {
                                // Set EditText fields with fetched data
                                profileName.setText(profileN);
                                dobText.setText(dob);
                                mobile.setText(mobileNumber);
                                qualification.setText(qualifi);
                                expertise.setText(expert);
                                experience.setText(exp);
                                gender.setText(gen);
                                availb.setText(ab);
                                try {
                                    // Check if the profilePictureData array is not null and has a length greater than 0
                                    if (profilePictureData.length > 0) {
                                        profileImage.setImageBitmap(BitmapFactory.decodeByteArray(profilePictureData, 0, profilePictureData.length));
                                    } else {
                                        // Handle the case when the array is empty (e.g., display a default image)
                                        // Example: profileImage.setImageResource(R.drawable.default_profile_image);
                                    }
                                } catch (Exception e) {
                                    // Print additional debug information
                                    Log.e("ProfileImageDataError", "Error decoding bitmap: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            runOnUiThread(() -> {
                                // Set EditText fields with fetched data
                                profileName.setText(profileN);
                                dobText.setText(dob);
                                mobile.setText(mobileNumber);
                                qualification.setText(qualifi);
                                expertise.setText(expert);
                                experience.setText(exp);
                                gender.setText(gen);
                                availb.setText(ab);
                                profileImage.setImageResource(R.drawable.avtar);
                                // Print debug information if profilePictureData is null
                                Log.e("ProfileImageDataError", "profilePictureData is null");
                            });
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setEditTextsEditable(boolean editable) {
        dobText.setFocusable(editable);
        mobile.setFocusable(editable);
        gender.setFocusable(editable);
        qualification.setFocusable(editable);
        expertise.setFocusable(editable);
        experience.setFocusable(editable);
    }

    private void saveProfile(String emailid, String firstName, String lastName, String dob, String gender,
                             String phoneNumber, String highestQualification, String expertise, String experience, String ava, byte[] profilePicture) {
        new Thread(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);

                // Use a PreparedStatement to prevent SQL injection
                String query = "UPDATE " + TABLE_NAME + " SET " +
                        "first_name = ?, " +
                        "last_name = ?, " +
                        "date_of_birth = ?, " +
                        "gender = ?, " +
                        "phone_number = ?, " +
                        "highest_qualification = ?, " +
                        "expertise = ?, " +
                        "experience = ?, " +
                        "availability = ?," +
                        "profile_image = ? " +
                        "WHERE email = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    // Set parameters for the query
                    preparedStatement.setString(1, firstName);
                    preparedStatement.setString(2, lastName);
                    preparedStatement.setString(3, dob);
                    preparedStatement.setString(4, gender);
                    preparedStatement.setString(5, phoneNumber);
                    preparedStatement.setString(6, highestQualification);
                    preparedStatement.setString(7, expertise);
                    preparedStatement.setString(8, experience);
                    preparedStatement.setString(9, ava);
                    preparedStatement.setBytes(10, profilePicture);
                    preparedStatement.setString(11, emailid);

                    // Execute the update
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void selectProfilePicture(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Profile Picture")
                .setItems(new CharSequence[]{"Gallery", "Camera"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Gallery
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galleryIntent, REQUEST_GALLERY);
                                break;
                            case 1: // Camera
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, REQUEST_CAMERA);
                                break;
                        }
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String firstName = profileName.getText().toString();
        String dob = dobText.getText().toString();
        String mobileNumber = mobile.getText().toString();
        String genderValue = gender.getText().toString();
        String qualificationValue = qualification.getText().toString();
        String expertiseValue = expertise.getText().toString();
        String experienceValue = experience.getText().toString();
        String availvalue = availb.getText().toString();
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String emailId = preferences.getString("login_email_id", "");

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                // Handle the gallery selection and set the image to the ImageView.
                Uri selectedImage = data.getData();
                profileImage.setImageURI(selectedImage);

                // Convert Uri to byte array
                byte[] profilePicture = convertUriToByteArray(selectedImage);

                // Call saveProfile method to update the database
                saveProfile(emailId, firstName, "last_name_placeholder", dob, genderValue,
                        mobileNumber, qualificationValue, expertiseValue, experienceValue, availvalue, profilePicture);
            } else if (requestCode == REQUEST_CAMERA) {
                // Handle the camera capture and set the image to the ImageView.
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profileImage.setImageBitmap(photo);

                // Convert Bitmap to byte array
                byte[] profilePicture = convertBitmapToByteArray(photo);

                // Call saveProfile method to update the database
                saveProfile(emailId, firstName, "last_name_placeholder", dob, genderValue,
                        mobileNumber, qualificationValue, expertiseValue, experienceValue, availvalue, profilePicture);
            }
        }
    }

    private byte[] convertUriToByteArray(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private boolean validateInput(String firstName, String dob, String mobileNumber, String gender,
                                  String qualification, String expertise, String experience, String avail) {
        if (firstName.isEmpty() || dob.isEmpty() || gender.isEmpty() || qualification.isEmpty() ||
                expertise.isEmpty() || experience.isEmpty() || avail.isEmpty()) {
            showToast("All fields must be filled");
            return false;
        }

        if (!isNumeric(mobileNumber)) {
            showToast("Mobile number should only contain numeric characters");
            return false;
        }

        return true;
    }

    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}