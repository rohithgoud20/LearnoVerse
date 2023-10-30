package com.example.learnoverse;

public class ImageItem {
    private int imageResId;
    private String title;

    public ImageItem(int imageResId, String title) {
        this.imageResId = imageResId;
        this.title = title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }
}
