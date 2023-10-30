package com.example.learnoverse;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.widget.Toast;

public class signup extends AppCompatActivity {

    private Spinner spinnerInterests;
    private ArrayAdapter<CharSequence> adapter;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignUp;
    public enum InsertResult {
        SUCCESS,
        EMAIL_EXISTS,
        NULL_VALUES,
        INVALID_EMAIL
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        spinnerInterests = findViewById(R.id.spinnerInterests);
        adapter = ArrayAdapter.createFromResource(this, R.array.interests_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInterests.setAdapter(adapter);
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                // Handle sign-up logic here
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String usertype = spinnerInterests.getSelectedItem().toString();
                SecureRandom secureRandom = new SecureRandom();
                byte[] salt = new byte[16];
                secureRandom.nextBytes(salt);
                // Convert the salt to a Base64-encoded string for storage
                Intent intent = new Intent(signup.this, Profileinput.class);
                startActivity(intent);
                String saltString = Base64.getEncoder().encodeToString(salt);
                String hashedPassword=encryptPassword(password,salt);

                InsertResult insert_action=insertUser(email, hashedPassword, usertype, name, saltString);
                if (insert_action == InsertResult.SUCCESS) {
                    printToast("User registered sucessfully");

                } else if (insert_action == InsertResult.EMAIL_EXISTS) {
                    // Email already exists
                    // Perform actions for email existence
                    printToast("This user already exists");
                } else if (insert_action == InsertResult.NULL_VALUES) {
                    // Null values found
                    // Perform actions for null values
                    printToast("Please enter all values");
                }
                else if (insert_action == InsertResult.INVALID_EMAIL) {
                    // Null values found
                    // Perform actions for null values
                    printToast("Please valid email");
                }
            }
        });
    }

    public void printToast(CharSequence message){
        Toast toast= Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();

    }
    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
   @RequiresApi(api = Build.VERSION_CODES.O)
   public String encryptPassword(String password, byte[] salt){

       int iterations = 10000; // Number of iterations
       int keyLength = 256; // Key length in bits
       KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
       try {
           SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
           byte[] hashedPassword = keyFactory.generateSecret(keySpec).getEncoded();
           String hashedPasswordString = Base64.getEncoder().encodeToString(hashedPassword);
           return hashedPasswordString;
       } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
           e.printStackTrace();
           throw new RuntimeException("Error hashing the password.");
       }


   }

    public boolean isEmailExists(String email) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define the columns to retrieve
        String[] projection = { "email" };

        // Define the selection criteria
        String selection = "email = ?";
        String[] selectionArgs = { email };

        // Query the "Login" table
        Cursor cursor = db.query("login", projection, selection, selectionArgs, null, null, null);

        boolean emailExists = cursor.getCount() > 0; // If count > 0, the email exists

        cursor.close();
        db.close();

        return emailExists;
    }

    public InsertResult insertUser(String email, String password, String usertype, String name, String saltString) {
        if (email == null || password == null || usertype == null || name == null) {
            // Check if any of the input fields are null
            return InsertResult.NULL_VALUES;
        }

        if (isEmailExists(email)) {
            // Check if the email already exists
            return InsertResult.EMAIL_EXISTS;
        }
        if (!isValidEmail(email)) {
            return InsertResult.INVALID_EMAIL;
        }

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        values.put("usertype", usertype);
        values.put("name", name);
        values.put("salt", saltString);

        long newRowId = db.insert("login", null, values);

        db.close();

        return newRowId != -1 ? InsertResult.SUCCESS : InsertResult.NULL_VALUES;// Insert successful if newRowId is not -1
    }

}
