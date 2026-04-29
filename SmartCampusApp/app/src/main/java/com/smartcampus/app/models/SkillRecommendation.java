package com.smartcampus.app.models;

import com.google.gson.annotations.SerializedName;

public class SkillRecommendation {
    @SerializedName("skillName")
    private String skillName;
    @SerializedName("demandScore")
    private float demandScore;
    @SerializedName("reason")
    private String reason;
    @SerializedName("region")
    private String region;

    public String getSkillName() {
        return skillName;
    }

    public float getDemandScore() {
        return demandScore;
    }

    public String getReason() {
        return reason;
    }

    public String getRegion() {
        return region;
    }
}
