package com.example.learnoverse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class MainActivity extends AppCompatActivity {
    private Button buttonLogin;

    private EditText editText_userId, editText_password;
    private TextView buttonSignUp;

    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";
    public static final String TABLE_NAME = "signup";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonLogin = findViewById(R.id.loginButton);
        buttonSignUp = findViewById(R.id.signupButton);
        editText_userId = findViewById(R.id.userID_edittext);
        editText_password = findViewById(R.id.password_edittext);



        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                String user = editText_userId.getText().toString();
                String password = editText_password.getText().toString();
                checkLogin(user, password);

            }
        });





        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, signup.class);
                startActivity(intent);
            }
        });


    }






    public void printToast(CharSequence message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();

    }

    public enum LoginResult {
        SUCCESS,
        INVALID_USERNAME,
        INVALID_PASSWORD,
        DATABASE_ERROR
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkLogin(String enteredEmail, String enteredPassword) {
        new Thread(() -> {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                // Retrieve user data based on the entered email
                ResultSet resultSet = statement.executeQuery("SELECT id, password, salt, usertype FROM " + TABLE_NAME + " WHERE emailid='" + enteredEmail + "'");

                if (resultSet.next()) {
                    // User found, compare passwords
                    String storedPassword = resultSet.getString("password");
                    String usertype = resultSet.getString("usertype");
                    Integer userid = resultSet.getInt("id");
                    String saltString = resultSet.getString("salt");
                    Log.d("Login", "Entered Password: " + enteredPassword);
                    Log.d("Login", "Stored Password: " + storedPassword);
                    Log.d("Login", "Salt String: " + saltString);
                    byte[] salt = (saltString != null) ? Base64.getDecoder().decode(saltString) : new byte[0];
                    Log.d("Login", "Decoded Salt: " + Base64.getEncoder().encodeToString(salt));

                    if (verifyPassword(enteredPassword, storedPassword, saltString)) {
                        // Successful login
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            // Redirect to another activity (e.g., HomeActivity) after successful login
                            SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("login_email_id", enteredEmail);
                            editor.putString("usertype", usertype);
                            editor.putInt("userid", userid);
                            editor.apply();


                            if ("learner".equals(usertype.toLowerCase())) {
                                Intent intent = new Intent(MainActivity.this, HomePage.class);
                                startActivity(intent);
                                // Handle learner specific actions
                            } else if ("instructor".equals(usertype.toLowerCase())) {
                                // Handle instructor specific actions
                                Intent intent = new Intent(MainActivity.this, InstructorHomePage.class);
                                startActivity(intent);
                            }

//                            Intent intent = new Intent(MainActivity.this, HomePage.class);
//                            startActivity(intent);
                            finish(); // Close the login activity
                        });
                    } else {
                        // Incorrect password
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    // User not found
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show());
                }

                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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
    public static boolean verifyPassword(String providedPassword, String storedPassword, String saltString) {
        byte[] storedSalt = (saltString != null) ? Base64.getDecoder().decode(saltString) : new byte[0];
        byte[] hashedProvidedPassword = hashPassword(providedPassword, storedSalt).getBytes();


        Log.d("VerifyPassword", "Hashed Provided Password: " + hashedProvidedPassword);
        Log.d("VerifyPassword", "Stored Password: " + storedPassword);
        Log.d("VerifyPassword", "Stored Password: " + storedSalt);
        BCrypt.Result result = BCrypt.verifyer().verify(providedPassword.toCharArray(), storedPassword);

        return result.verified;
    }




}