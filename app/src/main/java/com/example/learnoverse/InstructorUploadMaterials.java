package com.example.learnoverse;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class InstructorUploadMaterials extends AppCompatActivity {

    private List<MaterialItem> uploadedMaterials;
    private MaterialListAdapter adapter;
    public static final String DATABASE_NAME = "learnoverse";
    public static final String url = "jdbc:mysql://database-1.cue4ta1kd8o8.eu-north-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    public static final String username = "admin", password = "learnoverse";
    public static final String TABLE_NAME = "Materialsup";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_upload_materials);
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String emailid = preferences.getString("login_email_id", "");
        // Initialize views
        EditText materialNameEditText = findViewById(R.id.editTextMaterialName);
        EditText pdfLinkEditText = findViewById(R.id.editTextPdfLink);
        Button uploadButton = findViewById(R.id.buttonUpload);
        ListView uploadedMaterialsListView = findViewById(R.id.listViewUploadedMaterials);


        uploadedMaterials = new ArrayList<>();
        adapter = new MaterialListAdapter(this, uploadedMaterials);
        uploadedMaterialsListView.setAdapter(adapter);

        // Fetch materials from the database and update the list
        fetchMaterials(emailid);

        // Set an onClickListener for the Upload button
        uploadButton.setOnClickListener(v -> {
            // Get material name and PDF link
            String materialName = materialNameEditText.getText().toString().trim();
            String pdfLink = pdfLinkEditText.getText().toString().trim();

            // Validate inputs
            if (!materialName.isEmpty() && !pdfLink.isEmpty()) {
                // Upload material details to the database
                uploadMaterial(materialName, pdfLink,emailid);

                // Add the material to the list
                MaterialItem materialItem = new MaterialItem(materialName, pdfLink);
                uploadedMaterials.add(0, materialItem); // Add at the beginning of the list
                adapter.notifyDataSetChanged();

                // Clear the input fields
                materialNameEditText.getText().clear();
                pdfLinkEditText.getText().clear();

                // Display a success message or perform additional actions
                Toast.makeText(this, materialName + " successfully uploaded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter material name and PDF link", Toast.LENGTH_SHORT).show();
            }
        });

        // Set an onItemClickListener for the ListView
        uploadedMaterialsListView.setOnItemClickListener((parent, view, position, id) -> {
            // Handle item click (e.g., open material link)
            MaterialItem materialItem = uploadedMaterials.get(position);
            String pdfLink = materialItem.getPdfLink();
            openPdfLink(pdfLink);
            Toast.makeText(this, "Open material link: " + materialItem.getPdfLink(), Toast.LENGTH_SHORT).show();
        });

        // Set an onItemLongClickListener for the ListView
        uploadedMaterialsListView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Handle long press (e.g., show delete option)
            showDeleteOption(position);
            return true;
        });
    }

    private void openPdfLink(String pdfLink) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(pdfLink));
        startActivity(intent);
    }

    private void showDeleteOption(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String emailid = preferences.getString("login_email_id", "");
        builder.setTitle("Delete Material")
                .setMessage("Are you sure you want to delete this material?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Delete the material from the database
                    deleteMaterial(uploadedMaterials.get(position).getMaterialName(),emailid);

                    // Remove the material from the list
                    uploadedMaterials.remove(position);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Function to upload material details to the database
    private void uploadMaterial(String materialName, String pdfLink, String email) {
        new Thread(() -> {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                // add to RDS DB:
                statement.execute("INSERT INTO " + TABLE_NAME + "(materialTitle , pdfLink, instructorEmail) VALUES('" + materialName + "', '" + pdfLink + "', '" + email + "')");
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Function to fetch materials from the database
    private void fetchMaterials(String email) {
        new Thread(() -> {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                // Fetch materials from the database based on instructorEmail
                String query = "SELECT * FROM " + TABLE_NAME + " WHERE instructorEmail = '" + email + "'";
                ResultSet resultSet = statement.executeQuery(query);

                // Clear the current list
                uploadedMaterials.clear();

                // Iterate through the result set and add materials to the list
                while (resultSet.next()) {
                    String materialName = resultSet.getString("materialTitle");
                    String pdfLink = resultSet.getString("pdfLink");

                    MaterialItem materialItem = new MaterialItem(materialName, pdfLink);
                    uploadedMaterials.add(materialItem);
                }

                // Update the UI on the main thread
                runOnUiThread(() -> adapter.notifyDataSetChanged());

                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Function to delete material from the database
    private void deleteMaterial(String materialName,String email) {
        new Thread(() -> {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                // Delete material from the database based on materialTitle and instructorEmail
                String query = "DELETE FROM " + TABLE_NAME + " WHERE materialTitle = '" + materialName + "' AND instructorEmail = '" + email + "'";
                int rowsAffected = statement.executeUpdate(query);

                // Check if the deletion was successful
                if (rowsAffected > 0) {
                    Toast.makeText(this, "successfully deleted material", Toast.LENGTH_SHORT).show();
                    // Material deleted successfully, update the UI or perform additional actions
                    runOnUiThread(() -> {
                        // Find the material in the list and remove it
                        MaterialItem materialToRemove = null;
                        for (MaterialItem materialItem : uploadedMaterials) {
                            if (materialItem.getMaterialName().equals(materialName)) {
                                materialToRemove = materialItem;
                                break;
                            }
                        }

                        // Remove the material from the list
                        if (materialToRemove != null) {
                            uploadedMaterials.remove(materialToRemove);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    // Deletion failed, handle accordingly (e.g., show an error message)
                    runOnUiThread(() -> {
                        // Display an error message
                        Toast.makeText(this, "Failed to delete material", Toast.LENGTH_SHORT).show();
                    });
                }

                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
class MaterialItem {
    private String materialName;
    private String pdfLink;

    public MaterialItem(String materialName, String pdfLink) {
        this.materialName = materialName;
        this.pdfLink = pdfLink;
    }

    public String getMaterialName() {
        return materialName;
    }

    public String getPdfLink() {
        return pdfLink;
    }
}
class MaterialListAdapter extends BaseAdapter {

    private Context context;
    private List<MaterialItem> materialList;

    public MaterialListAdapter(Context context, List<MaterialItem> materialList) {
        this.context = context;
        this.materialList = materialList;
    }

    @Override
    public int getCount() {
        return materialList.size();
    }

    @Override
    public Object getItem(int position) {
        return materialList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.material_list_item, null);
        }

        TextView materialNameTextView = convertView.findViewById(R.id.materialNameTextView);
        TextView pdfLinkTextView = convertView.findViewById(R.id.pdfLinkTextView);

        MaterialItem materialItem = materialList.get(position);
        materialNameTextView.setText(materialItem.getMaterialName());
        pdfLinkTextView.setText(materialItem.getPdfLink());

        return convertView;
    }
}