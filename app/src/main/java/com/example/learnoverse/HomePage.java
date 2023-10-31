package com.example.learnoverse;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.example.learnoverse.ImageAdapter;

public class HomePage extends AppCompatActivity {
    private EditText editTextSearch;
    private boolean hasNotifications = true;
    private ImageView buttonSearch;
    private static final int PICK_IMAGE = 100;
    private ImageView buttonProfile;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private ImageView calenbut;
//    private List<Integer> images = Arrays.asList(
//            R.drawable.cooking, R.drawable.photography, R.drawable.physics, R.drawable.biology,
//            R.drawable.maths,R.drawable.coding);
    List<ImageItem> images = new ArrayList<>();

    private RecyclerView videoRecyclerView;
    private List<VideoItem> videoItems;
    private WebView displayVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ImageView profileIcon = findViewById(R.id.butprofile);
        editTextSearch =findViewById(R.id.searchbar);
        buttonSearch = findViewById(R.id.butsearch);
        buttonProfile = findViewById(R.id.butprofile);
        calenbut=findViewById(R.id.button2);

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
        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        LinearLayoutManager layoutMan = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        videoRecyclerView.setLayoutManager(layoutMan);
        videoItems = new ArrayList<>();
        videoItems.add(new VideoItem("Photography", "Description 1", "https://youtu.be/V7z7BAZdt2M?si=eB8XwdOe2l94sWLX"));
        videoItems.add(new VideoItem("Cooking", "Description 2", "https://youtu.be/V7z7BAZdt2M?si=eB8XwdOe2l94sWLX"));
        videoItems.add(new VideoItem("Maths", "Description 2", "https://youtu.be/V7z7BAZdt2M?si=eB8XwdOe2l94sWLX"));
        videoItems.add(new VideoItem("Coding", "Description 2", "https://youtu.be/V7z7BAZdt2M?si=eB8XwdOe2l94sWLX"));

        VideoAdapter videoAdapter = new VideoAdapter(videoItems,this);
        videoRecyclerView.setAdapter(videoAdapter);


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
        calenbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomePage.this,CalendarActivity.class);
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


        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editTextSearch.getText().toString().trim();
                // Implement your search logic here
                setupSearchBar();
            }
        });

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
        // Implement the logic to redirect to the selected course activity
//        Intent intent;
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


