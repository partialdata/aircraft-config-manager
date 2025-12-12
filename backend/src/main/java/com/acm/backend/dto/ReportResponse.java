package com.acm.backend.dto;

import java.time.Instant;
import java.util.Map;

public class ReportResponse {
    private String id;
    private Map<String, Object> metadata;
    private ValidationResult validation;
    private ValidationResult analyzer;
    private Instant generatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public ValidationResult getValidation() {
        return validation;
    }

    public void setValidation(ValidationResult validation) {
        this.validation = validation;
    }

    public ValidationResult getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(ValidationResult analyzer) {
        this.analyzer = analyzer;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }
}
