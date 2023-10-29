package com.example.learnoverse;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomePage extends AppCompatActivity {
    private EditText editTextSearch;
    private boolean hasNotifications = true;
    private ImageView buttonSearch;
    private static final int PICK_IMAGE = 100;
    private ImageView buttonProfile;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private List<Integer> images = Arrays.asList(
            R.drawable.img, R.drawable.img, R.drawable.img, R.drawable.img,R.drawable.img,R.drawable.img);
    private RecyclerView videoRecyclerView;
    private VideoAdapter videoAdapter;
    private WebView displayVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ImageView profileIcon = findViewById(R.id.butprofile);
        editTextSearch =findViewById(R.id.searchbar);
        buttonSearch = findViewById(R.id.butsearch);
        buttonProfile = findViewById(R.id.butprofile);
        recyclerView = findViewById(R.id.imageRecyclerView);
        LinearLayoutManager imlayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(imlayoutManager);
        ImageAdapter adapter = new ImageAdapter(images,this);
        recyclerView.setAdapter(adapter);
        ImageView notificationDot = findViewById(R.id.butnotification); // Change to your actual ID

        if (hasNotifications) {
            notificationDot.setVisibility(View.VISIBLE);
        } else {
            notificationDot.setVisibility(View.INVISIBLE);
        }

        RecyclerView videoRecyclerView = findViewById(R.id.videoRecyclerView);

        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<String> videoUrls = Arrays.asList(
                "android.resource://" + getPackageName() + "/" + R.raw.video1,
                "android.resource://" + getPackageName() + "/" + R.raw.video1,
                "android.resource://" + getPackageName() + "/" + R.raw.video1
        );
        VideoAdapter videoAdapter = new VideoAdapter(videoUrls);
        videoRecyclerView.setAdapter(videoAdapter);

        notificationDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement the logic to open notifications here
                openNotifications();
            }
        });



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

    }
    private List<VideoItem> getVideoItems() {
        List<VideoItem> videoItems = new ArrayList<>();
     //   videoItems.add(new VideoItem("Video 1 Title", "Video 1 Description", R.raw.video1));
     //   videoItems.add(new VideoItem("Video 2 Title", "Video 2 Description", R.raw.video2));
        return videoItems;
    }


    private void navigateToMainActivity() {
        Intent intent = new Intent(HomePage.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
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


