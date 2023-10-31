package com.example.learnoverse;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LearnoVerse.db";
    private static final int DATABASE_VERSION = 20;

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
        String createCoursesTableQuery = "CREATE TABLE IF NOT EXISTS CoursesOffered (course_id INTEGER PRIMARY KEY AUTOINCREMENT, course_name TEXT NOT NULL, instructor_name TEXT NOT NULL, no_of_sessions INTEGER, rating REAL)";
        db.execSQL(createCoursesTableQuery);

        // Create SessionForCourse table
        String createSessionTableQuery = "CREATE TABLE IF NOT EXISTS SessionForCourse (session_id INTEGER PRIMARY KEY AUTOINCREMENT, course_id INTEGER, session_name TEXT NOT NULL, start_date TEXT, start_time TEXT, FOREIGN KEY (course_id) REFERENCES CoursesOffered(course_id))";
        db.execSQL(createSessionTableQuery);

        String createLearnerTableQuery = "CREATE TABLE IF NOT EXISTS learner (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "first_name TEXT, " +
                "last_name TEXT, " +
                "profile_image TEXT, " +
                "date_of_birth TEXT, " +
                "languages TEXT, " +
                "country TEXT, " +
                "gender TEXT, " +
                "email TEXT, " +
                "phone_number TEXT, " +
                "highest_qualification TEXT, " +
                "interests TEXT," +
                "FOREIGN KEY (username) REFERENCES login(email)" +
                ");";
        db.execSQL(createLearnerTableQuery);

        String insertCoursesData = "INSERT INTO CoursesOffered (course_name, instructor_name, no_of_sessions, rating) VALUES " +
                "('Cooking', 'John Doe', 10, 4.5), " +
                "('Cooking', 'chandu', 15, 4), " +
                "('Cooking', 'Max', 12, 3.5), " +
                "('Photography', 'Jane Smith', 8, 4.0), " +
                "('Physics', 'lahari', 12, 4.2), " +
                "('Maths', 'chandana', 12, 4.2), " +
                "('Coding', 'yagna', 12, 4.2), " +
                "('Biology','jaipreet', 10, 4.4)";

        db.execSQL(insertCoursesData);


// Insert sample data into SessionForCourse table
        String insertSessionData = "INSERT INTO SessionForCourse (course_id, session_name, start_date, start_time) VALUES " +
                "(1, 'Session 1', '2023-10-01', '09:00 AM'), " +
                "(1, 'Session 2', '2023-10-08', '09:00 AM'), " +
                "(2, 'Session 1', '2023-10-02', '10:00 AM'), " +
                "(2, 'Session 2', '2023-10-09', '10:00 AM'), " +
                "(3, 'Session 1', '2023-10-03', '11:00 AM'), " +
                "(3, 'Session 2', '2023-10-10', '11:00 AM'), " +
                "(4, 'Session 1', '2023-10-04', '12:00 PM'), " +
                "(4, 'Session 1', '2023-10-04', '12:00 PM'), " +
                "(5, 'Session 1', '2023-10-05', '12:00 PM'), " +
                "(5, 'Session 1', '2023-10-05', '12:00 PM'), " +
                "(6, 'Session 1', '2023-10-06', '12:00 PM'), " +
                "(6, 'Session 1', '2023-10-06', '12:00 PM') " ;

        db.execSQL(insertSessionData);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades here
    }

}
