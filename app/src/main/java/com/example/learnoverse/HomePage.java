package com.example.learnoverse;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomePage extends AppCompatActivity {
    private EditText editTextSearch;
    private boolean hasNotifications = true;
    private ImageView buttonSearch;
    private static final int PICK_IMAGE = 100;
    private ImageView buttonProfile,butmat;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private ImageView calenbut;
    List<ImageItem> images = new ArrayList<>();

    private RecyclerView videoRecyclerView;
    private List<VideoItem> videoItems;
    private WebView displayVideo;
    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";
    public static final String TABLE_NAME = "learnerprofilestbs";
    public String firstname="";
    public ImageView vid1,vid2,vid3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ImageView profileIcon = findViewById(R.id.butprofile);
        TextView welcomemsg = findViewById(R.id.welcometextView);

       vid1=findViewById(R.id.video1);
       vid2=findViewById(R.id.video2);
       vid3=findViewById(R.id.video3);

        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String emailId = preferences.getString("login_email_id", "");
        try {
            fetchname(new OnFetchNameCallback() {
                @Override
                public void onNameFetched(String name) {
                    // This method is called when the name is fetched
                    firstname = name;
                    welcomemsg.setText("Welcome " + firstname);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        buttonProfile = findViewById(R.id.butprofile);
        calenbut=findViewById(R.id.button2);
        butmat=findViewById(R.id.button1);
        ImageView buttonProgress = findViewById(R.id.progress_button);
        recyclerView = findViewById(R.id.imageRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Add images and titles to the list
        images.add(new ImageItem(R.drawable.cooking, "Cooking"));

        images.add(new ImageItem(R.drawable.physics, "Physics"));
        images.add(new ImageItem(R.drawable.biology, "Biology"));
        images.add(new ImageItem(R.drawable.maths, "Maths"));
        images.add(new ImageItem(R.drawable.coding, "Coding"));
        images.add(new ImageItem(R.drawable.photography, "Photography"));

        adapter = new ImageAdapter(images);

        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ImageItem item) {
                // Handle the item click here
                // You can use the 'item' object to get data related to the clicked item
                String title = item.getTitle();
                Log.i(TAG,"clicked " + title);

                Intent intent= new Intent(HomePage.this,SelectedCourseView.class);
                intent.putExtra("course_name", title);
                startActivity(intent);

            }
        });

        vid1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideo("https://www.youtube.com/watch?v=V7z7BAZdt2M");
            }
        });
        vid2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideo("https://www.youtube.com/watch?v=mRMmlo_Uqcs");
            }
        });
        vid3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideo("https://www.youtube.com/watch?v=HPsazrVSjl8");
            }
        });

        ImageView notificationDot = findViewById(R.id.butnotification); // Change to your actual ID

        if (hasNotifications) {
            notificationDot.setVisibility(View.VISIBLE);
        } else {
            notificationDot.setVisibility(View.INVISIBLE);
        }


        notificationDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement the logic to open notifications here
                openNotifications();
            }
        });



        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });


        buttonProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Progress.class);
                startActivity(intent);
            }
        });
        calenbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomePage.this,CalendarActivity.class);
                startActivity(intent);
            }
        });
        butmat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, StudentMaterials.class);
                startActivity(intent);
            }
        });



        // Other logic and components for the home activity

    }

    private void openVideo(String yourVideo) {


        // Open the YouTube app or a web browser to play the video
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(yourVideo));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("force_fullscreen", true);
        intent.setPackage("com.google.android.youtube");

        try {
            startActivity(intent);
        } catch (Exception e) {
            // If YouTube app is not installed, open the video in a web browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(yourVideo));
            startActivity(browserIntent);
        }
    }



    public void fetchname(OnFetchNameCallback callback) throws SQLException {
        new Thread(() -> {
            // Do your work

            String fetchedName = "";

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                String emailId = preferences.getString("login_email_id", "");

                ResultSet rs = statement.executeQuery("SELECT first_name FROM learnerprofilestbs WHERE email='" + emailId + "'");

                if (rs.next()) {
                    fetchedName = rs.getString("first_name");
                } else {
                    Log.d("fetchname", "No records found for email ID: " + emailId);
                }

                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            callback.onNameFetched(fetchedName);
        }).start();
    }

    // Define a callback interface
    public interface OnFetchNameCallback {
        void onNameFetched(String name);
    }
    public String getFirstName(String username) {

        String firstname =null;
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {"first_name"}; // Assuming you have a constant for the column name

        String selection =  "username = ?";
        String[] selectionArgs = {username};


        return firstname;
    }



    private void navigateToMainActivity() {
        Intent intent = new Intent(HomePage.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void navigateToprofilepage() {
        Intent intent = new Intent(HomePage.this, profilepage.class);
        startActivity(intent);
    }

    private void openNotifications() {

        Intent intent = new Intent(HomePage.this, NotificationsActivity.class);
        startActivity(intent);
    }


    private void showPopupMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_profile) {
                    navigateToprofilepage();
                    return true;
                }else if (item.getItemId() == R.id.menu_help) {
                    showHelpDialog();
                    return true;
                }
                else if (item.getItemId() == R.id.menu_logout) {
                    navigateToMainActivity();
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();




    }
    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help Menu");
        builder.setMessage("Author:Group 5\nVersion: version 1.0\nInstructions:1.If you're a new user, sign up for an account using your email \n 2.If you're an existing user, log in using your registered credentials\n" +
                "3.Navigate through different subjects or categories by swiping or tapping on the respective options.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Perform any action on OK click if needed
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void setupSearchBar() {
        editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.d("FOCUS_CHANGE", "Search bar has gained focus");
                    showCourseMenu();
                }
            }
        });
    }


    private void showCourseMenu() {
        PopupMenu popupMenu = new PopupMenu(this, editTextSearch);
        List<String> availableCourses = Arrays.asList(
                "Introduction to Programming",
                "Web Development Basics",
                "Data Science Fundamentals",
                "Introduction to Machine Learning"
        );

        for (String course : availableCourses) {
            popupMenu.getMenu().add(course);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the selected course here
                String selectedCourse = item.getTitle().toString();
                // Redirect to the corresponding course activity or perform necessary action
                redirectToCourse(selectedCourse);
                return true;
            }
        });
        popupMenu.show();
    }

    private void redirectToCourse(String course) {

    }



}


