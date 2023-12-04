package com.example.learnoverse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentMaterials extends AppCompatActivity {

    private List<MaterialItem> materials;
    private MaterialListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_materials);

        materials = new ArrayList<>();

        ListView listViewMaterials = findViewById(R.id.listViewMaterials);

        // Initialize the adapter
        adapter = new MaterialListAdapter(this, materials);

        // Set the adapter for the ListView
        listViewMaterials.setAdapter(adapter);

        // Fetch materials from the database in a background thread
        fetchMaterials();
        // Set an onItemClickListener for the ListView
        listViewMaterials.setOnItemClickListener((parent, view, position, id) -> {
            // Handle item click (e.g., open material link)
            MaterialItem materialItem = materials.get(position);
            String pdfLink = materialItem.getPdfLink();
            openPdfLink(pdfLink);
        });
    }

    private void openPdfLink(String pdfLink) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(pdfLink));
        startActivity(intent);
    }

    private void fetchMaterials() {        new Thread(() -> {
        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(
                    InstructorUploadMaterials.url,
                    InstructorUploadMaterials.username,
                    InstructorUploadMaterials.password
            );

            // Create a SQL statement
            Statement statement = connection.createStatement();

            // Execute a query to fetch materials
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + InstructorUploadMaterials.TABLE_NAME);

            // Process the result set and populate the materials list
            while (resultSet.next()) {
                String materialName = resultSet.getString("materialTitle");
                String pdfLink = resultSet.getString("pdfLink");

                // Create a MaterialItem and add it to the list
                MaterialItem materialItem = new MaterialItem(materialName, pdfLink);
                materials.add(materialItem);
            }

            // Close resources
            resultSet.close();
            statement.close();
            connection.close();

            // Notify the adapter on the UI thread that the data has changed
            runOnUiThread(() -> adapter.notifyDataSetChanged());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();

    }
}
