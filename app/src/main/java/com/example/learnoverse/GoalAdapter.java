package com.example.learnoverse;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.appcompat.app.WindowDecorActionBar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnoverse.R;

import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private List<Goal> goals;

    public GoalAdapter(List<Goal> goals) {
        this.goals = goals;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_list_item, parent, false);
        return new GoalViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);

//        holder.goalTextView.setText(goal.getGoalText());
        holder.courseNameTextView.setText("Course Name: " + goal.getCourseName());
        holder.instructorNameTextView.setText("Instructor: " + goal.getInstructorName());
        holder.sessionsTextView.setText("Sessions: " + goal.getNoOfSessions());
        holder.status.setText("Status: " + goal.getStatus());

        // Check the status and display the button if it's "New"
        if ("new".equals(goal.getStatus())) {
            holder.enrollButton.setVisibility(View.VISIBLE);

            // Set up a click listener for the "Enroll for Course" button


            holder.enrollButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Change the status to "In Progress" when the button is clicked
                    goal.setStatus("In Progress");

                    MyDatabaseHelper dbHelper = new MyDatabaseHelper(holder.itemView.getContext());
//                    dbHelper.updateGoalStatus(goal.getId(), "In Progress");

                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put("status", "In Progress");
                    String whereClause = "id = ?";
                    Log.d("GoalAdapter", "Not Updating  " + goal.getId());
                    String[] whereArgs = {String.valueOf(goal.getId())};

                    long updatedRow = db.update("Goal", values,whereClause, whereArgs);

                    db.close();
                    if(updatedRow==-1){
                        Log.d("GoalAdapter", "Not Updating  " + goal.getStatus());

                    }
                    else{
                        Log.d("GoalAdapter", "Updating for goalId: " + goal.getStatus());

                    }

                    // You can update the status in your database here
                    // For now, notify the adapter to refresh the view
                    notifyDataSetChanged();
                }
            });
        } else {
            // If the status is not "New," hide the button
            holder.enrollButton.setVisibility(View.GONE);
            Log.d("GoalAdapter", "Updating status to 'In Progress' for goalId: " + goal.getStatus());

        holder.status.setText("Status: " + goal.getStatus());
        }
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView status;
        TextView goalTextView;
        TextView courseNameTextView;
        TextView instructorNameTextView;
        TextView sessionsTextView;
        Button enrollButton;

        public GoalViewHolder(View itemView) {
            super(itemView);
//            goalTextView = itemView.findViewById(R.id.goalTextView);
            courseNameTextView = itemView.findViewById(R.id.courseNameTextView);
            instructorNameTextView = itemView.findViewById(R.id.instructorNameTextView);
            sessionsTextView = itemView.findViewById(R.id.sessionsTextView);
            enrollButton = itemView.findViewById(R.id.enrollButton);
            status =itemView.findViewById(R.id.status);
        }
    }


}
