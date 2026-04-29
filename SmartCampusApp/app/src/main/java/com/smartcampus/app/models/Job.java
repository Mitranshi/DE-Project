package com.smartcampus.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Job {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("companyName")
    private String companyName;
    @SerializedName("location")
    private String location;
    @SerializedName("description")
    private String description;
    @SerializedName("requiredSkills")
    private List<String> requiredSkills;
    @SerializedName("salaryPackage")
    private String salaryPackage;
    @SerializedName("jobType")
    private String jobType;
    @SerializedName("applicationDeadline")
    private String applicationDeadline;
    @SerializedName("isActive")
    private boolean isActive;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public String getSalaryPackage() {
        return salaryPackage;
    }

    public String getJobType() {
        return jobType;
    }

    public String getApplicationDeadline() {
        return applicationDeadline;
    }

    public boolean isActive() {
        return isActive;
    }
}
