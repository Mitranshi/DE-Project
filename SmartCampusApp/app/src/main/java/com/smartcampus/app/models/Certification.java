package com.smartcampus.app.models;

import com.google.gson.annotations.SerializedName;

public class Certification {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("issuingOrganization")
    private String issuingOrganization;
    @SerializedName("issueDate")
    private String issueDate;
    @SerializedName("credentialUrl")
    private String credentialUrl;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIssuingOrganization() {
        return issuingOrganization;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getCredentialUrl() {
        return credentialUrl;
    }
}
