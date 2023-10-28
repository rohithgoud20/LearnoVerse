package com.example.learnoverse;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.SecureRandom;
//import java.util.Base64;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity {
    private Button buttonSignUp,buttonLogin;
    private EditText editText_userId,editText_password;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonLogin = findViewById(R.id.loginButton) ;
        buttonSignUp=findViewById(R.id.signupButton);
        editText_userId = findViewById(R.id.userID_edittext);
        editText_password = findViewById(R.id.password_edittext);

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                //validate
               //check db and validate
                String user = editText_userId.getText().toString();
                String password = editText_password.getText().toString();
                LoginResult loginResult=loginUser(user,password);
                if (loginResult == LoginResult.SUCCESS) {
                    printToast("Login success");
                }
                else if (loginResult == LoginResult.INVALID_USERNAME) {
                        printToast("Invalid username");
                    }
                    else if (loginResult == LoginResult.INVALID_PASSWORD) {
                            printToast("Wrong password");
                        }

            }
        });
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,signup.class);
                startActivity(intent);
            }
        });


    }
    public void printToast(CharSequence message){
        Toast toast= Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();

    }
    public enum LoginResult {
        SUCCESS,
        INVALID_USERNAME,
        INVALID_PASSWORD,
        DATABASE_ERROR
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LoginResult loginUser(String username, String password) {
        if (!isValidUsername(username)) {
            return LoginResult.INVALID_USERNAME;
        }

        if (!isValidPassword(password)) {
            return LoginResult.INVALID_PASSWORD;
        }

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query the database for the user's record based on the username
        String[] columns = { "password","salt"};
        String selection = "email = ?";
        String[] selectionArgs =  {username};

        Cursor cursor = db.query("login", columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst() && cursor!=null) {

            String hashedPasswordFromDatabase = cursor.getString(cursor.getColumnIndex("password"));

            // Convert the salt to a Base64-encoded string for storage

            String saltString = cursor.getString(cursor.getColumnIndex("salt"));
            byte[] salt = Base64.getDecoder().decode(saltString);
            if (verifyPassword(password, hashedPasswordFromDatabase, salt)) {
                // Password matches
                cursor.close();
                db.close();
                return LoginResult.SUCCESS;
            } else {
                // Invalid password
                cursor.close();
                db.close();
                return LoginResult.INVALID_PASSWORD;
            }
        } else {
            // Invalid username
            cursor.close();
            db.close();
            return LoginResult.INVALID_USERNAME;
        }
    }

    private boolean isValidUsername(String username) {
        // Define your username validation logic
        return username != null && !username.isEmpty();
    }

    private boolean isValidPassword(String password) {
        // Define your password validation logic
        return password != null; // Example: Minimum length of 8 characters
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String hashPassword(String password, byte[] salt) {
        int iterations = 10000;
        int keyLength = 256;

        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hashedPassword = keyFactory.generateSecret(keySpec).getEncoded();
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RuntimeException("Error hashing the password.");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean verifyPassword(String providedPassword, String storedPassword, byte[] salt) {
        String hashedProvidedPassword = hashPassword(providedPassword, salt);
        return hashedProvidedPassword.equals(storedPassword);
    }

}