package com.example.learnoverse;// NotificationUtils.java

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NotificationUtils {

    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";

    public static void insertNewNotification(String email, String notificationText, InsertNotificationCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                  //  Class.forName("com.mysql.cj.jdbc.Driver");

                    try (Connection connection = DriverManager.getConnection(url, username, password)) {
                        String query = "INSERT INTO Notifications (emailid, notification_text) VALUES (?, ?)";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                            preparedStatement.setString(1, email);
                            preparedStatement.setString(2, notificationText);
                            preparedStatement.executeUpdate();
                        }
                    }

                    if (callback != null) {
                        callback.onNotificationInserted(true);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onNotificationInserted(false);
                    }
                }
            }
        }).start();
    }

    public interface InsertNotificationCallback {
        void onNotificationInserted(boolean success);
    }
}
