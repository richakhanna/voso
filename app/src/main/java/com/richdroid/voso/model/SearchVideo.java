package com.richdroid.voso.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by richa.khanna on 9/10/16.
 */
public class SearchVideo {

    @SerializedName("video_url")
    private String videoUrl;
    @SerializedName("thumbnail_url")
    private String thumbnailUrl;
    private String title;
    private String description;

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
