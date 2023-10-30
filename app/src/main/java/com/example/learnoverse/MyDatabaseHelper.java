package com.example.learnoverse;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LearnoVerse.db";
    private static final int DATABASE_VERSION = 14;

    // Constructor
    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create your database tables here
        String createTableQuery = "CREATE TABLE IF NOT EXISTS YourTable (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)";
        db.execSQL(createTableQuery);
        String createTableQuery2 = "CREATE TABLE IF NOT EXISTS login (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE NOT NULL ,salt TEXT NOT NULL, password  TEXT NOT NULL, usertype TEXT NOT NULL,name TEXT NOT NULL)";
        db.execSQL(createTableQuery2);
        String createGoalTableQuery = "CREATE TABLE IF NOT EXISTS Goal (id INTEGER PRIMARY KEY AUTOINCREMENT, user_name STRING, course_name TEXT, instructor_name TEXT, goal_text TEXT, no_of_sessions INTEGER, status TEXT,  FOREIGN KEY (user_name) REFERENCES login(email))";
        db.execSQL(createGoalTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades here
    }
    public void updateGoalStatus(int goalId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);

        String whereClause = "id = ?";
        String[] whereArgs = { String.valueOf(goalId) };

        db.update("Goal", values, whereClause, whereArgs);
        Log.i(TAG,"updated status");
        db.close();
    }
}
