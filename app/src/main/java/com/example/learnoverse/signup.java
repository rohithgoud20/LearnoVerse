package com.example.learnoverse;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Base64;

import at.favre.lib.crypto.bcrypt.BCrypt;





public class signup extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonl;
    private Button buttonI;

    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";
    public static final String TABLE_NAME = "signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonl = findViewById(R.id.learnerButton);
        buttonI = findViewById(R.id.instructorButton);

        buttonl.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String pass = editTextPassword.getText().toString();

                // Handle learner sign-up logic here
                Insertdata(name, email, pass, "learner");
                Intent intent = new Intent(signup.this, learnerprofile.class);
                intent.putExtra("email", email);
                startActivity(intent);
                //  startActivity(new Intent(signup.this,learnerprofile.class));
            }
        });

        buttonI.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String pass = editTextPassword.getText().toString();

                // Handle instructor sign-up logic here

                Insertdata(name, email, pass, "instructor");
                Intent intent = new Intent(signup.this, InstructorProfile.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Insertdata(String n, String em, String p, String u) {
        new Thread(() -> {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();
                byte[] salt = generateSalt();
                String hashedPassword = hashPassword(p, salt);
                Log.d("Insertdata", "Generated Salt: " + Base64.getEncoder().encodeToString(salt));
                Log.d("Insertdata", "Hashed Password: " + hashedPassword);

                // add to RDS DB:
                statement.execute("INSERT INTO " + TABLE_NAME + "(name, emailid, password, salt, usertype) VALUES('" + n + "', '" + em + "', '" + hashedPassword + "', '" + Base64.getEncoder().encodeToString(salt) + "', '" + u + "')");
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
    private String hashPassword(String password, byte[] salt) {
        // Define the work factor (logarithmic cost factor)
        int workFactor = 12; // You can adjust this according to your needs

        // Hash the password using BCrypt
        String hashedPassword = BCrypt.withDefaults().hashToString(workFactor, password.toCharArray());

        return hashedPassword;
    }


}