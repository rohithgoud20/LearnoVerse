package com.example.learnoverse;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomePage extends AppCompatActivity {
    private EditText editTextSearch;
    private ImageView buttonSearch;
    private ImageView buttonProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        editTextSearch =findViewById(R.id.searchbar);
        buttonSearch = findViewById(R.id.butsearch);
        buttonProfile = findViewById(R.id.butprofile);
        ImageView buttonProgress = findViewById(R.id.progress_button);


        editTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCourseMenu();
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



        // Other logic and components for the home activity
    }


    private void navigateToMainActivity() {
        Intent intent = new Intent(HomePage.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void showPopupMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_profile) {
                    // Handle profile option
                    return true;
                } else if (item.getItemId() == R.id.menu_logout) {
                    navigateToMainActivity();
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();


        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editTextSearch.getText().toString().trim();
                // Implement your search logic here
                setupSearchBar();
            }
        });

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
                "Introduction to Machine Learning",
                "Mobile App Development",
                "Graphic Design Fundamentals",
                "Photography Basics",
                "Music Theory Essentials"
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
        // Implement the logic to redirect to the selected course activity
        Intent intent;
//        switch (course) {
//            case "Introduction to Programming":
//                intent = new Intent(HomePage.this, IntroductionToProgrammingActivity.class);
//                break;
//            case "Web Development Basics":
//                intent = new Intent(HomePage.this, WebDevelopmentBasicsActivity.class);
//                break;
//            // Add cases for other courses here
//
//            default:
//                // If the selected course does not match any case, log an error message
//                Log.e("REDIRECT_ERROR", "Course not found: " + course);
//                return;
//        }
//        startActivity(intent);
    }



}


