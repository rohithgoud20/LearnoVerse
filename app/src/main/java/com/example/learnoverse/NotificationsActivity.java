package com.example.learnoverse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize the clear all button
        Button clearAllButton = findViewById(R.id.clearAllButton);

        // Set an OnClickListener for the clear all button
        clearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement the logic to clear all notifications
                clearAllNotifications();
            }
        });
    }

    // Method to clear all notifications
    private void clearAllNotifications() {

        Toast.makeText(this, "Notifications cleared", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(NotificationsActivity.this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
