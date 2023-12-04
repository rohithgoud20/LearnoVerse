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

public class profilepage extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private ImageView profileImage;

    private TextView profileName;
    private EditText dobText;
    private EditText mobile;
    private EditText country;
    private EditText qualification;
    private EditText interests;
    private EditText languages;
    private ImageView editDob, editMobile, editCountry, editQualification, editInterests, editLanguages;
    private Button saveButton;
    private SharedPreferences preferences;
    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username= "admin";
    public static final String password = "learnoverse";
    public static final String TABLE_NAME = "learnerprofilestbs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String emailId = preferences.getString("login_email_id", "");

        // Initialize UI components
        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        dobText = findViewById(R.id.dobText);
        mobile = findViewById(R.id.mobile);
        country = findViewById(R.id.country);
        qualification = findViewById(R.id.qualification);
        interests = findViewById(R.id.interests);
        languages = findViewById(R.id.languages);
        profileImage = findViewById(R.id.profileImage);
        editDob = findViewById(R.id.editDob);
        editMobile = findViewById(R.id.editmobile);
        editCountry = findViewById(R.id.editCountry);
        editQualification = findViewById(R.id.editQualification);
        editInterests = findViewById(R.id.editInterests);
        editLanguages = findViewById(R.id.editLanguages);


        saveButton = findViewById(R.id.saveButton);


        try {
            filldata();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        editDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable editing for Date of Birth
                Log.d("ImageViewClick", "Edit Date of Birth ImageView clicked.");
//                setEditTextsEditable(true);

                dobText.requestFocus();
            }
        });

        editMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable editing for Mobile Number
                setEditTextsEditable(true);
                mobile.requestFocus();
            }
        });

        editCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable editing for Country
                setEditTextsEditable(true);
                country.requestFocus();
            }
        });

        editQualification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable editing for Highest Qualification
                setEditTextsEditable(true);
                qualification.requestFocus();
            }
        });

        editInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable editing for Interests
                setEditTextsEditable(true);
                interests.requestFocus();
            }
        });

        editLanguages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable editing for Languages Known
                setEditTextsEditable(true);
                languages.requestFocus();
            }
        });

        // Set an OnClickListener for the "Save" button
        // Set an OnClickListener for the "Save" button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve values from UI components
                String firstName = profileName.getText().toString();
                String dob = dobText.getText().toString();
                String mobileNumber = mobile.getText().toString();
                String countryValue = country.getText().toString();
                String qualificationValue = qualification.getText().toString();
                String interestsValue = interests.getText().toString();
                String languagesValue = languages.getText().toString();
                Drawable drawable = profileImage.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                byte[] profilePicture = convertBitmapToByteArray(bitmap);
                // Call saveProfile method
                saveProfile(emailId, firstName, "last_name_placeholder", dob, languagesValue, countryValue, "gender_placeholder", mobileNumber, qualificationValue, interestsValue,profilePicture);
                Toast.makeText(getApplicationContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }


        public void filldata() throws SQLException {
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
                String query = "SELECT * FROM learnerprofilestbs WHERE email = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, emailId);

                    ResultSet rs = preparedStatement.executeQuery();

                    if (rs.next()) {
                        // Fetch data and set EditText fields
                        String profileN = rs.getString("first_name");
                        String dob = rs.getString("date_of_birth");
                        String mobileNumber = rs.getString("phone_number");
                        String coun = rs.getString("country");
                        String qualifi = rs.getString("highest_qualification");
                        String ints = rs.getString("interests");
                        String lang = rs.getString("languages");
                        byte[] profilePictureData=rs.getBytes("profile_image");
                        Log.d("ProfileImageDataLength", String.valueOf(profilePictureData.length));
                        // Update UI on the main thread
                        runOnUiThread(() -> {
                            // Set EditText fields with fetched data
                            profileName.setText(profileN);
                            dobText.setText(dob);
                            mobile.setText(mobileNumber);
                            country.setText(coun);
                            qualification.setText(qualifi);
                            interests.setText(ints);
                            languages.setText(lang);
                            profileImage.setImageBitmap(BitmapFactory.decodeByteArray(profilePictureData, 0, profilePictureData.length));

                        });
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
        country.setFocusable(editable);
        qualification.setFocusable(editable);
        interests.setFocusable(editable);
        languages.setFocusable(editable);
    }

    private void loadUserProfileData() {
        // Load user profile data from SharedPreferences
        preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        dobText.setText(preferences.getString("dob", ""));
        mobile.setText(preferences.getString("mobile", ""));
        country.setText(preferences.getString("country", ""));
        qualification.setText(preferences.getString("qualification", ""));
        interests.setText(preferences.getString("interests", ""));
        languages.setText(preferences.getString("languages", ""));
    }

    public static void saveProfile(String emailid, String firstName, String lastName, String dob, String languages, String country,
                                   String gender, String phoneNumber, String highestQualification, String interests,byte[] profilePicture) {
        new Thread(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);

                // Use a PreparedStatement to prevent SQL injection
                String query = "UPDATE " + TABLE_NAME + " SET " +
                        "first_name = ?, " +
                        "last_name = ?, " +
                        "date_of_birth = ?, " +
                        "languages = ?, " +
                        "country = ?, " +
                        "gender = ?, " +
                        "phone_number = ?, " +
                        "highest_qualification = ?, " +
                        "interests = ? ," +
                        "profile_image = ? " +
                        "WHERE email = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    // Set parameters for the query
                    preparedStatement.setString(1, firstName);
                    preparedStatement.setString(2, lastName);
                    preparedStatement.setString(3, dob);
                    preparedStatement.setString(4, languages);
                    preparedStatement.setString(5, country);
                    preparedStatement.setString(6, gender);
                    preparedStatement.setString(7, phoneNumber);
                    preparedStatement.setString(8, highestQualification);
                    preparedStatement.setString(9, interests);
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
        // Create a dialog or an Intent to allow the user to choose a profile picture.
        // You can implement your own dialog or open the gallery/camera using an Intent.
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
                saveProfile(emailId, firstName, "last_name_placeholder", dob, languages.getText().toString(),
                        country.getText().toString(), "gender_placeholder", mobile.getText().toString(),
                        qualification.getText().toString(), interests.getText().toString(), profilePicture);
            } else if (requestCode == REQUEST_CAMERA) {
                // Handle the camera capture and set the image to the ImageView.
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profileImage.setImageBitmap(photo);

                // Convert Bitmap to byte array
                byte[] profilePicture = convertBitmapToByteArray(photo);

                // Call saveProfile method to update the database
                saveProfile(emailId, firstName, "last_name_placeholder", dob, languages.getText().toString(),
                        country.getText().toString(), "gender_placeholder", mobile.getText().toString(),
                        qualification.getText().toString(), interests.getText().toString(), profilePicture);
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

}