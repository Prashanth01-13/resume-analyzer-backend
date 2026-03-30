package com.example.resumeanalyzer.model;

public class MatchResponse {

    private String jobTitle;
    private double score;

    public MatchResponse(String jobTitle, double score) {
        this.jobTitle = jobTitle;
        this.score = score;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public double getScore() {
        return score;
    }
}