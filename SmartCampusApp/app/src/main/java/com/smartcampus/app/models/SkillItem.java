package com.smartcampus.app.models;

import com.google.gson.annotations.SerializedName;

public class SkillItem {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("category")
    private String category;
    @SerializedName("proficiencyLevel")
    private String proficiencyLevel;

    public SkillItem() {
    }

    public SkillItem(String name, String category, String proficiencyLevel) {
        this.name = name;
        this.category = category;
        this.proficiencyLevel = proficiencyLevel;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }
}
