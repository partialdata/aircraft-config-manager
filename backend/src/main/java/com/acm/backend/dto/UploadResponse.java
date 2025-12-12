package com.acm.backend.dto;

public class UploadResponse {
    private String id;
    private String message;
    private ValidationResult validation;
    private ValidationResult analyzer;

    public UploadResponse() {
    }

    public UploadResponse(String id, String message, ValidationResult validation, ValidationResult analyzer) {
        this.id = id;
        this.message = message;
        this.validation = validation;
        this.analyzer = analyzer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
