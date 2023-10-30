package com.example.learnoverse;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.learnoverse.R;
import java.util.List;

public class CompletedCourseAdapter extends RecyclerView.Adapter<CompletedCourseAdapter.CompletedCourseViewHolder> {
    private List<CompletedCourse> completedCourses;

    public CompletedCourseAdapter(List<CompletedCourse> completedCourses) {
        this.completedCourses = completedCourses;
    }

    @NonNull
    @Override
    public CompletedCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_course_list_item, parent, false);
        return new CompletedCourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedCourseViewHolder holder, int position) {
        CompletedCourse course = completedCourses.get(position);
        holder.courseNameTextView.setText(course.getCourseName());

        if (course.isRated()) {
            // Course is rated, show the rating and hide rating elements
            holder.ratingBar.setRating(course.getRating());
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.rateCourseButton.setVisibility(View.GONE);
        } else {
            // Course is unrated, hide the rating and show rating elements
            holder.ratingBar.setVisibility(View.GONE);
            holder.rateCourseButton.setVisibility(View.VISIBLE);
        }

        // Handle the "Rate Course" button click
        holder.rateCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show rating elements for this course
                holder.ratingBar.setVisibility(View.VISIBLE);
                holder.rateCourseButton.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return completedCourses.size();
    }

    public class CompletedCourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseNameTextView;
        RatingBar ratingBar;
        Button rateCourseButton;

        public CompletedCourseViewHolder(View itemView) {
            super(itemView);
            courseNameTextView = itemView.findViewById(R.id.courseNameTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            rateCourseButton = itemView.findViewById(R.id.rateCourseButton);
        }
    }
}
