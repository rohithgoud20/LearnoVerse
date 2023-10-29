package com.example.learnoverse;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private Context context;
    private List<VideoItem> videoItems;

    public VideoAdapter(Context context, List<VideoItem> videoItems) {
        this.context = context;
        this.videoItems = videoItems;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_recommendation_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        VideoItem videoItem = videoItems.get(position);
        holder.videoTitle.setText(videoItem.getVideoTitle());

        // Set the video URL to the VideoView
        Uri videoUri = Uri.parse(videoItem.getVideoUrl());
        holder.videoView.setVideoURI(videoUri);
        holder.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Play the video when the item is clicked
                holder.videoView.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        TextView videoTitle;

        VideoViewHolder(View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            videoTitle = itemView.findViewById(R.id.videoTitle);
        }
    }
}
