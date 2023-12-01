package com.example.learnoverse;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executors;

public class AWSDBHelper {

    private static final String TAG = "DB";
    private volatile Connection connection;

    public AWSDBHelper(String username, String password) {
        createConnectionInBackground(username, password);
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                Log.d(TAG, "Connection closed");
            } else {
                Log.d(TAG, "Connection is already closed");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Exception while closing the database connection", e);
        }
    }

    public void createConnectionInBackground(String username, String password) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String rdsEndpoint = "database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com";
                String jdbcUrl = "jdbc:mysql://" + rdsEndpoint + ":3306/learnoverse?connectTimeout=10000";
                Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
                setConnection(connection);
                Log.d(TAG, "Connection status: Open");
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "MySQL JDBC Driver not found", e);
            } catch (SQLException e) {
                Log.e(TAG, "Error creating database connection", e);
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error", e);
            }
        });
    }


    private void setConnection(Connection connection) {
        this.connection = connection;
    }

    public InsertResult insertLoginUser(String email, String password, String userType, String name) {
        try {
            if (connection == null || connection.isClosed()) {
                Log.e(TAG, "Connection is null or closed");
                return InsertResult.FAILURE;
            }

            String sql = "INSERT INTO users (name,email, password, usertype, ) VALUES (?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(4, name);
                statement.setString(1, email);
                statement.setString(2, password);
                statement.setString(3, userType);


                int rowsInserted = statement.executeUpdate();

                return (rowsInserted > 0) ? InsertResult.SUCCESS : InsertResult.FAILURE;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error executing SQL query", e);
            return InsertResult.FAILURE;
        }
    }
}
