package com.example.picker.Models;

import com.google.gson.annotations.SerializedName;

public class ModelVideo {
    @SerializedName("featured")
    private String videoUri;
    @SerializedName("title")
    private String videoName;

    public ModelVideo(String videoUri, String videoName) {
        this.videoUri = videoUri;
        this.videoName = videoName;
    }

    public ModelVideo() {
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
}
