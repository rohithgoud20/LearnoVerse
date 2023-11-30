package com.example.learnoverse;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.database.Cursor;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String username = preferences.getString("username", null);

        // Initialize UI components
//        profileImage = findViewById(R.id.profileImage);
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
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Check if the username is not null
        if (username != null) {
            // Use the username to query the learner data from the database


            String[] projection = {
                    "first_name",
                    "last_name",
                    "date_of_birth",
                    "gender",
                    "email",
                    "phone_number",
                    "highest_qualification",
                    "interests",
                    "profile_image"
            };

            String selection = "username = ?";
            String[] selectionArgs = {username};

            Cursor cursor = db.query("learner", projection, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                // Retrieve learner data from the cursor and set it to the UI components
                profileName.setText(cursor.getString(cursor.getColumnIndexOrThrow("first_name")) + " " + cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
                dobText.setText(cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth")));
                mobile.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone_number")));
//                country.setText(cursor.getString(cursor.getColumnIndexOrThrow("country")));
                qualification.setText(cursor.getString(cursor.getColumnIndexOrThrow("highest_qualification")));
                interests.setText(cursor.getString(cursor.getColumnIndexOrThrow("interests")));
//                languages.setText(cursor.getString(cursor.getColumnIndexOrThrow("languages_known")));

//                SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                String image_type = preferences.getString("image_type", null);
                Log.d("ImageViewClick", image_type);

                int columnIndex = cursor.getColumnIndex("profile_image");

                if (columnIndex != -1) {
                    String imageType = cursor.getString(columnIndex);

                    if (imageType != null) {
                        if ("Uri".equals(imageType)) {
                            Log.d("ImageViewClick", "Uri retrieved");
                            String uriString = cursor.getString(columnIndex);
                            Uri profileImageUri = Uri.parse(uriString);
                            profileImage.setImageURI(profileImageUri);
                        } else if ("Bitmap".equals(imageType)) {
                            Log.d("ImageViewClick", "Bitmap retrieved");
                            String base64Image = cursor.getString(columnIndex);
                            byte[] byteArray = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap profileImageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                            profileImage.setImageBitmap(profileImageBitmap);
                        }
                    } else {
                        // Handle the case where the data is null
                        // You can set a default image or display an error message
                    }
                } else {
                    // Handle the case where the column doesn't exist in the cursor
                }



            }


            cursor.close();
            db.close();
        }

        saveButton = findViewById(R.id.saveButton);

        // Initially, set the EditText fields as non-editable
//        setEditTextsEditable(false);

        // Load user profile data from SharedPreferences
//        loadUserProfileData();

        // Add click listeners for the "Edit" icons
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
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile(username);
            }
        });

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

    private void saveProfile(String username) {
        // Retrieve the edited data from the EditText fields
        profileImage = findViewById(R.id.profileImage);
        String editedDob = dobText.getText().toString();
        String editedMobile = mobile.getText().toString();
        String editedCountry = country.getText().toString();
        String editedQualification = qualification.getText().toString();
        String editedInterests = interests.getText().toString();
        String editedLanguages = languages.getText().toString();

        // Update the database with the edited data
        // You should have a method in your database helper class to perform the update.
        // This is a simplified example, and you need to adapt it to your actual database structure.
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String profile_image = preferences.getString("profile_image", null);
        ContentValues values = new ContentValues();
        values.put("date_of_birth", editedDob);
        values.put("phone_number", editedMobile);
        values.put("country", editedCountry);
        values.put("highest_qualification", editedQualification);
        values.put("interests", editedInterests);
        values.put("languages", editedLanguages);
        values.put("profile_image",profile_image);
        String selection =  "username = ?";
        String[] selectionArgs = {username};
        long goalId =  db.update("learner", values, "username = ?", selectionArgs);
        if (goalId != -1) {
            Log.i(TAG,"Updated the profile");
            Toast.makeText(this, "Updated the profile", Toast.LENGTH_SHORT).show();


        } else {
            // There was an error saving the goal
            // Handle the error as needed
        }

//        db.update(UserProfileDatabaseHelper.TABLE_USER_PROFILES, values, selection, selectionArgs);
        db.close();

        // Save the changes to SharedPreferences (optional)
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("dob", editedDob);
//        editor.putString("mobile", editedMobile);
//        editor.putString("country", editedCountry);
//        editor.putString("qualification", editedQualification);
//        editor.putString("interests", editedInterests);
//        editor.putString("languages", editedLanguages);
//        editor.apply();

        // Set the EditText fields as non-editable
//        setEditTextsEditable(false);
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

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                // Handle the gallery selection and set the image to the ImageView.
                Uri selectedImage = data.getData();
                profileImage.setImageURI(selectedImage);
                SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("profile_image",selectedImage.toString());

                editor.putString("image_type","Uri");
                editor.apply();


            } else if (requestCode == REQUEST_CAMERA) {
                // Handle the camera capture and set the image to the ImageView.
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profileImage.setImageBitmap(photo);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // Convert the byte array to a Base64-encoded string
                String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("profile_image",encodedImage);
                editor.putString("image_type","Bitmap");
                editor.apply();


            }
        }
    }
}