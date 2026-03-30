package com.example.resumeanalyzer.model;

import java.util.List;

public class Job {

    private String title;
    private List<String> skills;

    public Job(String title, List<String> skills) {
        this.title = title;
        this.skills = skills;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getSkills() {
        return skills;
    }
}