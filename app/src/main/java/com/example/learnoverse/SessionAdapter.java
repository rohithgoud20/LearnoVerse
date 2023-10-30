package com.example.learnoverse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {
    private List<courseSessions> sessions;

    public SessionAdapter(List<courseSessions> sessions) {
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View sessionView = inflater.inflate(R.layout.sessions_view, parent, false);
        return new SessionViewHolder(sessionView);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        courseSessions session = sessions.get(position);

        // Bind session data to the views
        holder.sessionNameTextView.setText(session.getSessionName());
        holder.startDateTextView.setText(session.getStartDate());
        holder.startTimeTextView.setText(session.getStartTime());
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }
    public void updateData(List<courseSessions> sessions) {
        this.sessions = sessions;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    public class SessionViewHolder extends RecyclerView.ViewHolder {
        public TextView sessionNameTextView;
        public TextView startDateTextView;
        public TextView startTimeTextView;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);

            sessionNameTextView = itemView.findViewById(R.id.sessionNameTextView);
            startDateTextView = itemView.findViewById(R.id.dateTextView);
            startTimeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}
