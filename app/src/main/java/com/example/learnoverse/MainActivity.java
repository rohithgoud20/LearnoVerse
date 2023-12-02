package com.example.learnoverse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class MainActivity extends AppCompatActivity {
    private Button buttonLogin;
    private Button makepayment;
    private EditText editText_userId, editText_password;
    private TextView buttonSignUp;
    String SECRET_KEY = "sk_test_51OIHPrIo6vEXNiPYlrs3Tf1h99LZ94cJrkiKeSH0BpXL0TMcCFTIEIBROFQnB17GCDWOP88uodBDOzu33nnS9LQl00btPIR2M5";
    String PUBLISH_KEY = "pk_test_51OIHPrIo6vEXNiPY827X5UIp7JOvSJsMKicZyGid5Rqy1rhij2D7xyEZhzZg1iY5HF7ZR92p2lr1O5v7N3kbmk4g00abTRMmkf";
    PaymentSheet paymentsheet;
    String customerID;
    String Ephericalkey;
    String ClientSecret;
    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";
    public static final String TABLE_NAME = "signup";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonLogin = findViewById(R.id.loginButton);
        buttonSignUp = findViewById(R.id.signupButton);
        editText_userId = findViewById(R.id.userID_edittext);
        editText_password = findViewById(R.id.password_edittext);




        RequestQueue queue = Volley.newRequestQueue(this);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                //validate
                //check db and validate
                String user = editText_userId.getText().toString();
                String password = editText_password.getText().toString();
                checkLogin(user, password);
//                Intent intent = new Intent(MainActivity.this,HomePage.class);
//                startActivity(intent);
//                if (loginResult == LoginResult.SUCCESS) {
//                    printToast("Login success");
//
//                    // Retrieve user type from SharedPreferences
//                    SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
//                    String userType = preferences.getString("usertype", "defaultUserType");
//
//                    // Use userType as needed
//                    if ("learner".equals(userType.toLowerCase())) {
//                        Intent intent = new Intent(MainActivity.this, HomePage.class);
//                        startActivity(intent);
//                        // Handle learner specific actions
//                    } else if ("instructor".equals(userType.toLowerCase())) {
//                        // Handle instructor specific actions
//                        Intent intent = new Intent(MainActivity.this, InstructorHomePage.class);
//                        startActivity(intent);
//                    }
//
//
//                } else if (loginResult == LoginResult.INVALID_USERNAME) {
//                    printToast("Invalid username");
//                } else if (loginResult == LoginResult.INVALID_PASSWORD) {
//                    printToast("Wrong password");
//                }
//
            }
        });

        makepayment = findViewById(R.id.makepayment);
        makepayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentFlow();
            }
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
                            Toast.makeText(MainActivity.this, customerID, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, "Unauthorized request", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle other errors
                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, signup.class);
                startActivity(intent);
            }
        });


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
                            Toast.makeText(MainActivity.this, Ephericalkey, Toast.LENGTH_SHORT).show();

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

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
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
                            Toast.makeText(MainActivity.this, ClientSecret, Toast.LENGTH_SHORT).show();

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

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
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


    public void printToast(CharSequence message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();

    }

    public enum LoginResult {
        SUCCESS,
        INVALID_USERNAME,
        INVALID_PASSWORD,
        DATABASE_ERROR
    }
    private void checkLogin(String enteredEmail, String enteredPassword) {
        new Thread(() -> {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                // Retrieve user data based on the entered email
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE emailid='" + enteredEmail + "'");

                if (resultSet.next()) {
                    // User found, compare passwords
                    String storedPassword = resultSet.getString("password");
                    String usertype = resultSet.getString("usertype");
                    Integer userid = resultSet.getInt("id");

                    if (enteredPassword.equals(storedPassword)) {
                        // Successful login
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            // Redirect to another activity (e.g., HomeActivity) after successful login
                            SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("login_email_id", enteredEmail);
                            editor.putString("usertype", usertype);
                            editor.putInt("userid", userid);
                            editor.apply();

                            if ("learner".equals(usertype.toLowerCase())) {
                                Intent intent = new Intent(MainActivity.this, HomePage.class);
                                startActivity(intent);
                                // Handle learner specific actions
                            } else if ("instructor".equals(usertype.toLowerCase())) {
                                // Handle instructor specific actions
                                Intent intent = new Intent(MainActivity.this, InstructorHomePage.class);
                                startActivity(intent);
                            }

//                            Intent intent = new Intent(MainActivity.this, HomePage.class);
//                            startActivity(intent);
                            finish(); // Close the login activity
                        });
                    } else {
                        // Incorrect password
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    // User not found
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show());
                }

                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public LoginResult loginUser(String username, String password) {
//        if (!isValidUsername(username)) {
//            return LoginResult.INVALID_USERNAME;
//        }
//
//        if (!isValidPassword(password)) {
//            return LoginResult.INVALID_PASSWORD;
//        }
//
//        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
//        // Query the database for the user's record based on the username
//        String[] columns = {"password", "salt", "usertype"};
//        String selection = "email = ?";
//        String[] selectionArgs = {username};
//
//        Cursor cursor = db.query("login", columns, selection, selectionArgs, null, null, null);
//
//        if (cursor.moveToFirst() && cursor != null) {
//
//            String hashedPasswordFromDatabase = cursor.getString(cursor.getColumnIndex("password"));
//
//            // Convert the salt to a Base64-encoded string for storage
//            String userType = cursor.getString(cursor.getColumnIndex("usertype"));
//
//            String saltString = cursor.getString(cursor.getColumnIndex("salt"));
//            byte[] salt = Base64.getDecoder().decode(saltString);
//            if (verifyPassword(password, hashedPasswordFromDatabase, salt)) {
//                // Password matches
//                cursor.close();
//                db.close();
//                SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putString("username", username);
//                editor.putString("usertype", userType);
//                editor.apply();
//
//                return LoginResult.SUCCESS;
//            } else {
//                // Invalid password
//                cursor.close();
//                db.close();
//                return LoginResult.INVALID_PASSWORD;
//            }
//        } else {
//            // Invalid username
//            cursor.close();
//            db.close();
//            return LoginResult.INVALID_USERNAME;
//        }
//    }

//    private boolean isValidUsername(String username) {
//        // Define your username validation logic
//        return username != null && !username.isEmpty();
//    }

    private boolean isValidPassword(String password) {
        // Define your password validation logic
        return password != null; // Example: Minimum length of 8 characters
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String hashPassword(String password, byte[] salt) {
        int iterations = 10000;
        int keyLength = 256;

        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hashedPassword = keyFactory.generateSecret(keySpec).getEncoded();
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RuntimeException("Error hashing the password.");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean verifyPassword(String providedPassword, String storedPassword, byte[] salt) {
        String hashedProvidedPassword = hashPassword(providedPassword, salt);
        return hashedProvidedPassword.equals(storedPassword);
    }
}