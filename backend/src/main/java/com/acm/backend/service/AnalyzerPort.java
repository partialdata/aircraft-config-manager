package com.acm.backend.service;

import com.acm.backend.dto.ValidationResult;
import com.fasterxml.jackson.databind.JsonNode;

public interface AnalyzerPort {
    ValidationResult analyze(JsonNode node);
}
