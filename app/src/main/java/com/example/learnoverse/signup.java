package com.example.learnoverse;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

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
            }
        });
    }

    public void Insertdata(String n, String em, String p, String u) {
        new Thread(() -> {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                // add to RDS DB:
                statement.execute("INSERT INTO " + TABLE_NAME + "(name, emailid, password, usertype) VALUES('" + n + "', '" + em + "', '" + p + "', '" + u + "')");

                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
