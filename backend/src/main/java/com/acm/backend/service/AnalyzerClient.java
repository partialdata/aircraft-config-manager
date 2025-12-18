package com.acm.backend.service;

import com.acm.backend.dto.AnalyzerResponse;
import com.acm.backend.dto.ValidationResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class AnalyzerClient implements AnalyzerPort {
    private static final Logger log = LoggerFactory.getLogger(AnalyzerClient.class);

    private final RestTemplate restTemplate;
    private final String analyzerUrl;

    public AnalyzerClient(RestTemplate restTemplate,
                          @Value("${analyzer.url:http://analyzer:8000/analyze}") String analyzerUrl) {
        this.restTemplate = restTemplate;
        this.analyzerUrl = analyzerUrl;
    }

    public ValidationResult analyze(JsonNode node) {
        ValidationResult result = new ValidationResult();
        try {
            HttpEntity<JsonNode> entity = new HttpEntity<>(node);
            ResponseEntity<AnalyzerResponse> response = restTemplate.exchange(
                    URI.create(analyzerUrl),
                    HttpMethod.POST,
                    entity,
                    AnalyzerResponse.class
            );
            AnalyzerResponse body = response.getBody();
            if (body != null) {
                body.getWarnings().forEach(result::addWarning);
                body.getErrors().forEach(result::addError);
            }
        } catch (Exception ex) {
            log.warn("Analyzer call failed: {}", ex.getMessage());
            result.addWarning("Analyzer unavailable: " + ex.getMessage());
        }
        return result;
    }
}
