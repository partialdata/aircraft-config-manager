package com.acm.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private List<String> warnings = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    public ValidationResult addWarning(String warning) {
        warnings.add(warning);
        return this;
    }

    public ValidationResult addError(String error) {
        errors.add(error);
        return this;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
