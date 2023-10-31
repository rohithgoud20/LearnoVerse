package com.example.learnoverse;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class VideoViewHolder extends RecyclerView.ViewHolder {
    public ImageView videoThumbnailImageView;
    public TextView videoTitleTextView;
    public TextView videoDescriptionTextView;

    public VideoViewHolder(View itemView) {
        super(itemView);
        videoThumbnailImageView = itemView.findViewById(R.id.videoThumbnailImageView);
        videoTitleTextView = itemView.findViewById(R.id.videoTitleTextView);
     //   videoDescriptionTextView = itemView.findViewById(R.id.videoDescriptionTextView);
    }
}
