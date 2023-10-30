package com.example.learnoverse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder> {

    private List<Goal> inProgressGoals;

    public ProgramAdapter(List<Goal> inProgressGoals) {
        this.inProgressGoals = inProgressGoals;
    }

    @NonNull
    @Override
    public ProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_list_item, parent, false);
        return new ProgramViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramViewHolder holder, int position) {
        Goal inProgressGoal = inProgressGoals.get(position);

        holder.courseNameTextView.setText( inProgressGoal.getCourseName());
       // holder.instructorNameTextView.setText("Instructor: " + inProgressGoal.getInstructorName());

        // Calculate and set progress for the progress bar
        int completedSessions = calculateCompletedSessions(inProgressGoal);
        int totalSessions = inProgressGoal.getNoOfSessions();
        int progress = (int) ((completedSessions * 1.0 / totalSessions) * 100);

        holder.progressBar.setProgress(progress);
    }

    @Override
    public int getItemCount() {
        return inProgressGoals.size();
    }

    public class ProgramViewHolder extends RecyclerView.ViewHolder {
//        TextView status;
        TextView courseNameTextView;
//        TextView instructorNameTextView;
//        TextView sessionsTextView;
        ProgressBar progressBar;

        public ProgramViewHolder(View itemView) {
            super(itemView);
            courseNameTextView = itemView.findViewById(R.id.courseNameTextView);
            //instructorNameTextView = itemView.findViewById(R.id.instructorNameTextView);
//            sessionsTextView = itemView.findViewById(R.id.sessionsTextView);
//            status = itemView.findViewById(R.id.status);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private int calculateCompletedSessions(Goal goal) {
        // You need to implement a method to calculate the completed sessions for a goal.
        // You can fetch this data from your database or another source.
        // For this example, I'm returning 5 as a placeholder.
        return 5;
    }
}
