package com.example.learnoverse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LearnoVerse.db";
    private static final int DATABASE_VERSION = 11;

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades here
    }
}
