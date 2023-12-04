package com.example.learnoverse;// NotificationsActivity.java

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class NotificationsActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> notificationList;
    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        listView = findViewById(R.id.listView);
        notificationList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationList);
        listView.setAdapter(adapter);

        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String login_email_id = preferences.getString("login_email_id", "");
        fetchAndDisplayNotifications(login_email_id);
        Button clearAllButton = findViewById(R.id.clearButton);

        // Set an OnClickListener for the clear all button
        clearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement the logic to clear all notifications
                clearAllNotifications();
            }
        });
    }
    private void clearAllNotifications() {

        Toast.makeText(this, "Notifications cleared", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(NotificationsActivity.this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void fetchAndDisplayNotifications(String email) {
        notificationList.clear();

        new Thread(() -> {
            try {
                try (Connection connection = DriverManager.getConnection(url, username, password)) {
                    String query = "SELECT notification_text FROM Notifications WHERE emailid = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, email);

                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                            while (resultSet.next()) {
                                String notificationText = resultSet.getString("notification_text");
                                notificationList.add(notificationText);
                            }
                        }
                    }
                }

                // Reverse the order of the notificationList
                Collections.reverse(notificationList);

                // Update the adapter on the UI thread
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the exception if needed
            }
        }).start();
    }


}
