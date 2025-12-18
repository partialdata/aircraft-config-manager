package com.acm.backend.controller;

import com.acm.backend.dto.CompareRequest;
import com.acm.backend.dto.ConfigSummary;
import com.acm.backend.dto.DiffResponse;
import com.acm.backend.dto.ReportResponse;
import com.acm.backend.dto.UploadResponse;
import com.acm.backend.dto.ValidationResult;
import com.acm.backend.model.ConfigDocument;
import com.acm.backend.service.AnalyzerPort;
import com.acm.backend.service.ConfigService;
import com.acm.backend.service.DiffService;
import com.acm.backend.service.ValidationService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/configs")
@CrossOrigin(origins = "*")
public class ConfigController {

    private final ValidationService validationService;
    private final AnalyzerPort analyzerClient;
    private final ConfigService configService;
    private final DiffService diffService;

    public ConfigController(ValidationService validationService, AnalyzerPort analyzerClient, ConfigService configService, DiffService diffService) {
        this.validationService = validationService;
        this.analyzerClient = analyzerClient;
        this.configService = configService;
        this.diffService = diffService;
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UploadResponse> upload(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "configJson", required = false) String configJson
    ) {
        if ((file == null || file.isEmpty()) && !StringUtils.hasText(configJson)) {
            return ResponseEntity.badRequest().body(new UploadResponse(null, "configJson or file is required", new ValidationResult(), new ValidationResult()));
        }
        String rawJson = configJson;
        if (file != null && !file.isEmpty()) {
            try {
                rawJson = new String(file.getBytes(), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                return ResponseEntity.badRequest().body(new UploadResponse(null, "Unable to read file: " + ex.getMessage(), new ValidationResult(), new ValidationResult()));
            }
        }

        JsonNode parsed;
        try {
            parsed = validationService.parse(rawJson);
        } catch (IOException ex) {
            return ResponseEntity.badRequest().body(new UploadResponse(null, "Invalid JSON: " + ex.getMessage(), new ValidationResult(), new ValidationResult()));
        }
        ValidationResult validation = validationService.validate(parsed);
        ValidationResult analyzer = analyzerClient.analyze(parsed);

        if (validation.hasErrors()) {
            return ResponseEntity.badRequest().body(new UploadResponse(null, "Validation failed", validation, analyzer));
        }

        ConfigDocument saved = configService.save(parsed, rawJson);
        UploadResponse response = new UploadResponse(saved.getId(), "Upload successful", validation, analyzer);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<ConfigSummary> list() {
        return configService.list().stream()
                .map(doc -> new ConfigSummary(doc.getId(), doc.getConfigId(), doc.getAircraftType(), doc.getSoftwareVersion(), doc.getNavDataCycle(), doc.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @PostMapping("/compare")
    public ResponseEntity<DiffResponse> compare(@RequestBody CompareRequest request) {
        Optional<ConfigDocument> first = configService.find(request.getFirstId());
        Optional<ConfigDocument> second = configService.find(request.getSecondId());
        if (first.isEmpty() || second.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        DiffResponse diff = diffService.diff(first.get(), second.get());
        return ResponseEntity.ok(diff);
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<ReportResponse> report(@PathVariable String id) throws IOException {
        Optional<ConfigDocument> doc = configService.find(id);
        if (doc.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        JsonNode parsed = validationService.parse(doc.get().getRawJson());
        ReportResponse report = configService.buildReport(doc.get(), parsed);
        return ResponseEntity.ok(report);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!configService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
