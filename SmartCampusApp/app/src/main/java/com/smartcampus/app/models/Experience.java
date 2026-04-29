package com.smartcampus.app.models;

import com.google.gson.annotations.SerializedName;

public class Experience {
    @SerializedName("id")
    private int id;
    @SerializedName("companyName")
    private String companyName;
    @SerializedName("role")
    private String role;
    @SerializedName("startDate")
    private String startDate;
    @SerializedName("endDate")
    private String endDate;
    @SerializedName("description")
    private String description;

    public int getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getRole() {
        return role;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }
}
