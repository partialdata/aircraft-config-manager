package com.acm.backend.service;

import com.acm.backend.dto.ReportResponse;
import com.acm.backend.dto.ValidationResult;
import com.acm.backend.model.ConfigDocument;
import com.acm.backend.repository.ConfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConfigService {
    private final ConfigRepository repository;
    private final ValidationService validationService;
    private final AnalyzerPort analyzerClient;

    public ConfigService(ConfigRepository repository, ValidationService validationService, AnalyzerPort analyzerClient) {
        this.repository = repository;
        this.validationService = validationService;
        this.analyzerClient = analyzerClient;
    }

    @Transactional
    public ConfigDocument save(JsonNode node, String rawJson) {
        ConfigDocument document = new ConfigDocument();
        Map<String, Object> metadata = validationService.extractMetadata(node);
        document.setConfigId(String.valueOf(metadata.get("configId")));
        document.setAircraftType(String.valueOf(metadata.get("aircraftType")));
        document.setSoftwareVersion(String.valueOf(metadata.get("softwareVersion")));
        document.setNavDataCycle(String.valueOf(metadata.get("navDataCycle")));

        if (metadata.get("modules") != null) {
            String joined = String.join(",", node.get("modules").findValuesAsText("name"));
            document.setModules(joined);
        }
        document.setRawJson(rawJson);
        return repository.save(document);
    }

    public List<ConfigDocument> list() {
        return repository.findAll();
    }

    public Optional<ConfigDocument> find(String id) {
        return repository.findById(id);
    }

    public boolean delete(String id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    public ReportResponse buildReport(ConfigDocument doc, JsonNode parsed) {
        ValidationResult validation = validationService.validate(parsed);
        ValidationResult analyzer = analyzerClient.analyze(parsed);
        ReportResponse report = new ReportResponse();
        report.setId(doc.getId());
        report.setMetadata(validationService.extractMetadata(parsed));
        report.setValidation(validation);
        report.setAnalyzer(analyzer);
        report.setGeneratedAt(java.time.Instant.now());
        return report;
    }
}
