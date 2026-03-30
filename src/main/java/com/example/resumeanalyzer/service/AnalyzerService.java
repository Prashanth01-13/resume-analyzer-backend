package com.example.resumeanalyzer.service;

import com.example.resumeanalyzer.model.Job;
import com.example.resumeanalyzer.model.MatchResponse;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

@Service
public class AnalyzerService {

    private final List<Job> jobs = new ArrayList<>();
    private final List<String> knownSkills = Arrays.asList(
            "java", "spring", "hibernate", "html", "css", "javascript", "sql",
            "react", "node", "python", "machine learning", "data", "rest api",
            "docker", "aws", "microservices", "kafka"
    );
    private final Tika tika = new Tika();

    public AnalyzerService() {

        jobs.add(new Job("Java Developer",
                Arrays.asList("java", "spring", "hibernate", "sql")));

        jobs.add(new Job("Frontend Developer",
                Arrays.asList("react", "javascript", "css", "html")));

        jobs.add(new Job("Data Scientist",
                Arrays.asList("python", "machine learning", "data", "sql")));

        jobs.add(new Job("Full Stack Developer",
                Arrays.asList("java", "spring", "react", "javascript", "sql")));

        jobs.add(new Job("Backend Developer",
                Arrays.asList("java", "spring", "hibernate", "rest api")));

        jobs.add(new Job("Software Engineer",
                Arrays.asList("java", "javascript", "sql", "docker")));
    }

    public AnalysisResponse analyzeResume(MultipartFile file) {
        String resumeText = extractResumeText(file);
        String normalizedText = resumeText.toLowerCase(Locale.ROOT);

        List<String> detectedSkills = detectSkills(normalizedText);

        List<MatchResponse> results = new ArrayList<>();
        for (Job job : jobs) {
            int matchCount = 0;

            for (String skill : job.getSkills()) {
                if (normalizedText.contains(skill.toLowerCase(Locale.ROOT))) {
                    matchCount++;
                }
            }

            double score = ((double) matchCount / job.getSkills().size()) * 100;
            results.add(new MatchResponse(job.getTitle(), score));
        }

        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        double atsScore = results.isEmpty() ? 0 : results.get(0).getScore();
        List<String> missingSkills = getMissingSkillsForTopRole(results, detectedSkills);
        List<String> suggestions = buildSuggestions(missingSkills);

        return new AnalysisResponse(atsScore, detectedSkills, missingSkills, suggestions, results);
    }

    private String extractResumeText(MultipartFile file) {
        try {
            String parsedText = tika.parseToString(file.getInputStream());
            if (parsedText != null && !parsedText.isBlank()) {
                return parsedText;
            }
        } catch (Exception ignored) {
            // Fallback to raw text decoding below.
        }

        try {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
    }

    private List<String> detectSkills(String normalizedText) {
        List<String> detected = new ArrayList<>();

        for (String skill : knownSkills) {
            if (normalizedText.contains(skill.toLowerCase(Locale.ROOT))) {
                detected.add(skill);
            }
        }

        return detected;
    }

    private List<String> getMissingSkillsForTopRole(List<MatchResponse> results, List<String> detectedSkills) {
        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        String topRole = results.get(0).getJobTitle();

        for (Job job : jobs) {
            if (job.getTitle().equals(topRole)) {
                List<String> missing = new ArrayList<>();
                for (String skill : job.getSkills()) {
                    if (!detectedSkills.contains(skill)) {
                        missing.add(skill);
                    }
                }
                return missing;
            }
        }

        return Collections.emptyList();
    }

    private List<String> buildSuggestions(List<String> missingSkills) {
        if (missingSkills.isEmpty()) {
            return Arrays.asList(
                    "Resume already aligns well with your top role.",
                    "Add quantified achievements to improve recruiter confidence."
            );
        }

        List<String> suggestions = new ArrayList<>();
        int limit = Math.min(3, missingSkills.size());

        for (int i = 0; i < limit; i++) {
            String skill = missingSkills.get(i);
            suggestions.add("Add " + skill + " to your summary, projects, or experience.");
        }

        suggestions.add("Mention measurable outcomes such as performance gain, users impacted, or time saved.");
        return suggestions;
    }

    public static class AnalysisResponse {
        private final double atsScore;
        private final List<String> skills;
        private final List<String> missingSkills;
        private final List<String> suggestions;
        private final List<MatchResponse> matches;

        public AnalysisResponse(double atsScore,
                                List<String> skills,
                                List<String> missingSkills,
                                List<String> suggestions,
                                List<MatchResponse> matches) {
            this.atsScore = atsScore;
            this.skills = skills;
            this.missingSkills = missingSkills;
            this.suggestions = suggestions;
            this.matches = matches;
        }

        public double getAtsScore() {
            return atsScore;
        }

        public List<String> getSkills() {
            return skills;
        }

        public List<String> getMissingSkills() {
            return missingSkills;
        }

        public List<String> getSuggestions() {
            return suggestions;
        }

        public List<MatchResponse> getMatches() {
            return matches;
        }
    }
}