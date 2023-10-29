package com.example.learnoverse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private List<String> videoUrls;

    public VideoAdapter(List<String> videoUrls) {
        this.videoUrls = videoUrls;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        String videoUrl = videoUrls.get(position);
        // Set the video URL to the VideoView or VideoPlayer here
    }

    @Override
    public int getItemCount() {
        return videoUrls.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView videoTitle;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize your views here
        }
    }
}
