package com.smartcampus.app.models;

import java.util.List;

public class ExternalJob {
    private String jobId;
    private String title;
    private String companyName;
    private String companyLogo;
    private String location;
    private String description;
    private List<String> skills;
    private String apiSource;
    private String applyLink;
    private boolean isRemote;
    private String postedAt;
    private Double minSalary;
    private Double maxSalary;
    private String salaryCurrency;
    private String jobType;

    // Getters
    public String getJobId() {
        return jobId;
    }

    public String getTitle() {
        return title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getSkills() {
        return skills;
    }

    public String getApiSource() {
        return apiSource;
    }

    public String getApplyLink() {
        return applyLink;
    }

    public boolean isRemote() {
        return isRemote;
    }

    public String getPostedAt() {
        return postedAt;
    }

    public Double getMinSalary() {
        return minSalary;
    }

    public Double getMaxSalary() {
        return maxSalary;
    }

    public String getSalaryCurrency() {
        return salaryCurrency;
    }

    public String getJobType() {
        return jobType;
    }
}
