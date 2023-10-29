package com.example.learnoverse;
public class VideoItem {
    private String videoTitle;
    private String videoUrl;
    private int videoResource;

    public VideoItem(String videoTitle, String videoUrl,  int videoResource) {
        this.videoTitle = videoTitle;
        this.videoUrl = videoUrl;
        this.videoResource=videoResource;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getVideoUrl() {

        return videoUrl;
    }
}
