package com.example.resumeanalyzer.controller;

import com.example.resumeanalyzer.service.AnalyzerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ResumeController {

    @Autowired
    private AnalyzerService analyzerService;

    // ✅ Test API
    @GetMapping("/")
    public String home() {
        return "Backend is running successfully!";
    }

    // ✅ FILE UPLOAD API
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AnalyzerService.AnalysisResponse uploadResume(@RequestParam MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please upload a non-empty resume file.");
        }

        return analyzerService.analyzeResume(file);
    }
}