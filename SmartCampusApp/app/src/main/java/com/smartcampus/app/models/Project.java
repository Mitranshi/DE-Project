package com.smartcampus.app.models;

import com.google.gson.annotations.SerializedName;

public class Project {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("techStack")
    private String techStack;
    @SerializedName("projectUrl")
    private String projectUrl;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTechStack() {
        return techStack;
    }

    public String getProjectUrl() {
        return projectUrl;
    }
}
