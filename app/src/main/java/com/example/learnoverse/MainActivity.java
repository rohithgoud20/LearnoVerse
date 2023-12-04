package com.example.learnoverse;

import static android.content.ContentValues.TAG;

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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
    private Button makepayment;
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




        RequestQueue queue = Volley.newRequestQueue(this);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                //validate
                //check db and validate
                String user = editText_userId.getText().toString();
                String password = editText_password.getText().toString();
                checkLogin(user, password);
//                Intent intent = new Intent(MainActivity.this,HomePage.class);
//                startActivity(intent);
//                if (loginResult == LoginResult.SUCCESS) {
//                    printToast("Login success");
//
//                    // Retrieve user type from SharedPreferences
//                    SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
//                    String userType = preferences.getString("usertype", "defaultUserType");
//
//                    // Use userType as needed
//                    if ("learner".equals(userType.toLowerCase())) {
//                        Intent intent = new Intent(MainActivity.this, HomePage.class);
//                        startActivity(intent);
//                        // Handle learner specific actions
//                    } else if ("instructor".equals(userType.toLowerCase())) {
//                        // Handle instructor specific actions
//                        Intent intent = new Intent(MainActivity.this, InstructorHomePage.class);
//                        startActivity(intent);
//                    }
//
//
//                } else if (loginResult == LoginResult.INVALID_USERNAME) {
//                    printToast("Invalid username");
//                } else if (loginResult == LoginResult.INVALID_PASSWORD) {
//                    printToast("Wrong password");
//                }
//
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
                ResultSet resultSet = statement.executeQuery("SELECT id, password, salt,name, usertype FROM " + TABLE_NAME + " WHERE emailid='" + enteredEmail + "'");

                if (resultSet.next()) {
                    // User found, compare passwords
                    String storedPassword = resultSet.getString("password");
                    String usertype = resultSet.getString("usertype");
                    Integer userid = resultSet.getInt("id");
                    String saltString = resultSet.getString("salt");
                    String name = resultSet.getString("name");
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
                            Log.d(TAG, "ENTERED EMAIL"+enteredEmail);
                            Toast.makeText(MainActivity.this, "email id "+ enteredEmail, Toast.LENGTH_SHORT).show();
                            editor.putString("usertype", usertype);
                            editor.putInt("userid", userid);
                            editor.putString("name",name);
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

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public LoginResult loginUser(String username, String password) {
//        if (!isValidUsername(username)) {
//            return LoginResult.INVALID_USERNAME;
//        }
//
//        if (!isValidPassword(password)) {
//            return LoginResult.INVALID_PASSWORD;
//        }
//
//        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
//        // Query the database for the user's record based on the username
//        String[] columns = {"password", "salt", "usertype"};
//        String selection = "email = ?";
//        String[] selectionArgs = {username};
//
//        Cursor cursor = db.query("login", columns, selection, selectionArgs, null, null, null);
//
//        if (cursor.moveToFirst() && cursor != null) {
//
//            String hashedPasswordFromDatabase = cursor.getString(cursor.getColumnIndex("password"));
//
//            // Convert the salt to a Base64-encoded string for storage
//            String userType = cursor.getString(cursor.getColumnIndex("usertype"));
//
//            String saltString = cursor.getString(cursor.getColumnIndex("salt"));
//            byte[] salt = Base64.getDecoder().decode(saltString);
//            if (verifyPassword(password, hashedPasswordFromDatabase, salt)) {
//                // Password matches
//                cursor.close();
//                db.close();
//                SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putString("username", username);
//                editor.putString("usertype", userType);
//                editor.apply();
//
//                return LoginResult.SUCCESS;
//            } else {
//                // Invalid password
//                cursor.close();
//                db.close();
//                return LoginResult.INVALID_PASSWORD;
//            }
//        } else {
//            // Invalid username
//            cursor.close();
//            db.close();
//            return LoginResult.INVALID_USERNAME;
//        }
//    }

//    private boolean isValidUsername(String username) {
//        // Define your username validation logic
//        return username != null && !username.isEmpty();
//    }

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