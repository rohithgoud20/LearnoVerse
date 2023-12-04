package com.example.learnoverse;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnrollmentDetailsActivity extends AppCompatActivity {

    private Spinner sessionsSpinner;
    private TextView startDateTextView;
    private TextView startTimeTextView;
    private EditText userCommentEditText;
    private Button makePaymentButton;
    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";

    String SECRET_KEY = "sk_test_51OIHPrIo6vEXNiPYlrs3Tf1h99LZ94cJrkiKeSH0BpXL0TMcCFTIEIBROFQnB17GCDWOP88uodBDOzu33nnS9LQl00btPIR2M5";
    String PUBLISH_KEY = "pk_test_51OIHPrIo6vEXNiPY827X5UIp7JOvSJsMKicZyGid5Rqy1rhij2D7xyEZhzZg1iY5HF7ZR92p2lr1O5v7N3kbmk4g00abTRMmkf";
    PaymentSheet paymentsheet;

    String customerID;
    String Ephericalkey;
    String ClientSecret;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment_details);

        // Initialize UI components
        TextView instuctorNameTextView = findViewById(R.id.instructorNameTextView);
        TextView noOfSessionsTextView = findViewById(R.id.noOfSessionsTextView);
        RatingBar ratingTextView = findViewById(R.id.ratingBar);
        TextView desTextView = findViewById(R.id.descriptionTextView);
        sessionsSpinner = findViewById(R.id.sessionsSpinner);
        startDateTextView = findViewById(R.id.startDateTextView);
        startTimeTextView = findViewById(R.id.startTimeTextView);
        userCommentEditText = findViewById(R.id.userCommentEditText);
        makePaymentButton = findViewById(R.id.makePaymentButton);
        EditText learner_comment = findViewById(R.id.userCommentEditText);

        // Get details passed from the previous activity
        String instructorName = getIntent().getStringExtra("instructorName");
        String noOfSessions = getIntent().getStringExtra("noOfSessions");
        Float rating = getIntent().getFloatExtra("rating", 0);
        String description = getIntent().getStringExtra("description");

        instuctorNameTextView.setText(instructorName);
        noOfSessionsTextView.setText(noOfSessions + " sessions");
        ratingTextView.setRating(rating);
        desTextView.setText(description);
        int courseId = getIntent().getIntExtra("courseId", -1);

        // Fetch the initial session details (start date and time) for the first session
        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement sessionStatement = connection.prepareStatement(
                         "SELECT session_name, start_date, start_time FROM SessionsForCourse WHERE course_id = ?")) {

                // Set the parameter using PreparedStatement to prevent SQL injection
                sessionStatement.setInt(1, courseId);

                try (ResultSet sessionResultSet = sessionStatement.executeQuery()) {
                    if (sessionResultSet.next()) {
                        String initialSessionName = sessionResultSet.getString("session_name");
                        String initialStartDate = sessionResultSet.getString("start_date");
                        String initialStartTime = sessionResultSet.getString("start_time");

                        // Update the UI components with initial values
                        runOnUiThread(() -> {
                            startDateTextView.setText("Start Date: " + initialStartDate);
                            startTimeTextView.setText("Start Time: " + initialStartTime);
                        });

                        // Fetch session details based on courseId
                        List<String> sessionNames = new ArrayList<>();
                        do {
                            sessionNames.add(sessionResultSet.getString("session_name"));
                        } while (sessionResultSet.next());

                        // Populate the sessions spinner with session names
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(EnrollmentDetailsActivity.this,
                                    android.R.layout.simple_spinner_item, sessionNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            sessionsSpinner.setAdapter(adapter);

                            // Set the default selection to the initial session
                            int initialSessionIndex = sessionNames.indexOf(initialSessionName);
                            sessionsSpinner.setSelection(initialSessionIndex);
                        });

                        // Set a listener for session selection
                        sessionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                // Fetch and display start date and start time based on the selected session
                                String selectedSessionName = sessionNames.get(position);
                                updateSessionDetails(courseId, selectedSessionName);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parentView) {
                                // Do nothing
                            }
                        });
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }).start();

        // Set a listener for the "Make Payment" button click
        makePaymentButton.setOnClickListener(v -> {
            // TODO: Implement logic for making payment and handle user comments
            PaymentFlow();
            // check if payment is done
            // Insert details into the RegisteredCourses table
            SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
            String login_email_id = preferences.getString("login_email_id", "");
            String name = preferences.getString("name", "");

            // If the key "username" is not found, the default value (empty string "") will be returned.
            // Now you can use the 'username' variable as needed.
            // Assuming 'courseId' and 'userName' are available

            checkUserRegistration(courseId, login_email_id, isRegistered -> {
                // Use the result (isRegistered) here
                runOnUiThread(() -> {
                    if (isRegistered) {
                        Toast.makeText(EnrollmentDetailsActivity.this, "You have already enrolled for this course", Toast.LENGTH_SHORT).show();
                    } else {
                        new Thread(() -> {
                            try (Connection connection = DriverManager.getConnection(url, username, password);
                                 PreparedStatement insertStatement = connection.prepareStatement(
                                         "INSERT INTO RegisteredCourses (selected_course, user_name, status, learner_comment, ispayment_done) VALUES (?, ?, ?, ?,?)")) {

                                // Set the parameters using PreparedStatement to prevent SQL injection
                                insertStatement.setInt(1, courseId); // Assuming courseId is the selected_course
                                insertStatement.setString(2, login_email_id);
                                insertStatement.setString(3, "Enrolled");
                                insertStatement.setString(4, learner_comment.getText().toString()); // Assuming "Enrolled" is the initial status
                                insertStatement.setBoolean(5, true); // Assuming payment is done

                                // Execute the insert statement
                                int rowsInserted = insertStatement.executeUpdate();

                                if (rowsInserted > 0) {
                                    String instructor_email = null;
                                    String  course_name = null;

                                    try (Statement statement = connection.createStatement()) {
                                        String query = "SELECT * FROM CoursesOffered WHERE course_id='" + courseId + "'";
                                        try (ResultSet resultSet = statement.executeQuery(query)) {
                                            if (resultSet.next()) {
                                                Integer instructor_id = resultSet.getInt("instructor_id");
                                                course_name = resultSet.getString("course_name");
                                                String query2 = "SELECT * FROM signup WHERE id='" + instructor_id + "'";
                                                try (ResultSet resultSet2 = statement.executeQuery(query2)) {
                                                    if (resultSet2.next()) {
                                                        instructor_email = resultSet2.getString("emailid");
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // ... rest of your code

                                    String notificationText = name+" Registered for your Course "+course_name;
                                    NotificationUtils.insertNewNotification(instructor_email, notificationText, new NotificationUtils.InsertNotificationCallback() {
                                        @Override
                                        public void onNotificationInserted(boolean success) {
                                            if (success) {
                                                Log.d(TAG, "NOTIFICATION INSERTED");
                                                // Handle successful insertion
                                            } else {
                                                Log.d(TAG, "NOTIFICATION FAILED");
                                                // Handle insertion failure
                                            }
                                        }
                                    });

                                    // Insert successful, you can display a success message or navigate to another screen
                                    runOnUiThread(() -> {
                                        // TODO: Implement logic for success, e.g., display a success message or navigate to another screen
                                        Toast.makeText(EnrollmentDetailsActivity.this, "Enrolled for the course successfully", Toast.LENGTH_SHORT).show();

                                    });
                                } else {
                                    // Insert failed, you can display an error message
                                    runOnUiThread(() -> {
                                        Toast.makeText(EnrollmentDetailsActivity.this, "Failed to enroll", Toast.LENGTH_SHORT).show();

                                        // TODO: Implement logic for failure, e.g., display an error message
                                    });
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                // Handle the SQL exception, you can display an error message
                                runOnUiThread(() -> {
                                    // TODO: Implement logic for failure, e.g., display an error message
                                });
                            }
                        }).start();
                    }
                });
            });
        });




        PaymentConfiguration.init(this, PUBLISH_KEY);
        paymentsheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject object = new JSONObject(response);

                            customerID = object.getString("id");
                            Toast.makeText(EnrollmentDetailsActivity.this, customerID, Toast.LENGTH_SHORT).show();
                            Log.i("customerid", "customer id retrieved.");

                            getEphericalKey(customerID);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Check for 401 Unauthorized error
                if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                    // Handle unauthorized error
                    Toast.makeText(EnrollmentDetailsActivity.this, "Unauthorized request", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle other errors
                    Toast.makeText(EnrollmentDetailsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(EnrollmentDetailsActivity.this);
        requestQueue.add(stringRequest);



        // TODO: Populate other UI components with the fetched details
    }

    public void checkUserRegistration(int courseId, String userName, RegistrationCallback callback) {
        new Thread(() -> {
            boolean isRegistered = false;

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(
                         "SELECT * FROM RegisteredCourses WHERE selected_course = ? AND user_name = ?")) {

                statement.setInt(1, courseId);
                statement.setString(2, userName);

                try (ResultSet resultSet = statement.executeQuery()) {
                    isRegistered = resultSet.next();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Invoke the callback with the result
            callback.onRegistrationChecked(isRegistered);
        }).start();
    }

    public interface RegistrationCallback {
        void onRegistrationChecked(boolean isRegistered);
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {

        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "payment success", Toast.LENGTH_SHORT).show();
        }

    }

    private void getEphericalKey(String customerID) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            Ephericalkey = object.getString("id");
                            Toast.makeText(EnrollmentDetailsActivity.this, Ephericalkey, Toast.LENGTH_SHORT).show();

                            getClientSecret(customerID, Ephericalkey);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                header.put("Stripe-Version", "2023-10-16");
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(EnrollmentDetailsActivity.this);
        requestQueue.add(stringRequest);


    }

    private void getClientSecret(String customerID, String ephericalkey) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");
                            Toast.makeText(EnrollmentDetailsActivity.this, ClientSecret, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                params.put("amount", "1000" + "00");
                params.put("currency", "cad");
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(EnrollmentDetailsActivity.this);
        requestQueue.add(stringRequest);


    }

    private void PaymentFlow() {
        if (customerID != null) {
            paymentsheet.presentWithPaymentIntent(
                    ClientSecret,
                    new PaymentSheet.Configuration("LernoVerse",
                            new PaymentSheet.CustomerConfiguration(
                                    customerID,
                                    Ephericalkey
                            )
                    )
            );
        } else {
            // Handle the case where customerID is null
            Toast.makeText(this, "Customer ID is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSessionDetails(int courseId, String selectedSessionName) {
        // Fetch and display start date and start time based on the selected session
        new Thread(() -> {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement sessionDetailsStatement = connection.prepareStatement(
                         "SELECT start_date, start_time FROM SessionsForCourse WHERE course_id = ? AND session_name = ?")) {

                // Set the parameters using PreparedStatement to prevent SQL injection
                sessionDetailsStatement.setInt(1, courseId);
                sessionDetailsStatement.setString(2, selectedSessionName);

                try (ResultSet sessionDetailsResultSet = sessionDetailsStatement.executeQuery()) {
                    if (sessionDetailsResultSet.next()) {
                        String startDate = sessionDetailsResultSet.getString("start_date");
                        String startTime = sessionDetailsResultSet.getString("start_time");

                        // Update the UI components with session details
                        runOnUiThread(() -> {
                            startDateTextView.setText("Start Date: " + startDate);
                            startTimeTextView.setText("Start Time: " + startTime);
                        });
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
