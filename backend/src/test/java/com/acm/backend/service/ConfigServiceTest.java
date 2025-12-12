package com.acm.backend.service;

import com.acm.backend.dto.ReportResponse;
import com.acm.backend.dto.ValidationResult;
import com.acm.backend.model.ConfigDocument;
import com.acm.backend.repository.ConfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigServiceTest {

    @Mock
    private ConfigRepository repository;

    private ValidationService validationService;
    private ConfigService configService;
    private StubAnalyzer stubAnalyzer;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
        stubAnalyzer = new StubAnalyzer();
        configService = new ConfigService(repository, validationService, stubAnalyzer);
    }

    @Test
    void savePersistsMetadataFromJson() throws Exception {
        String json = """
                {
                  \"configId\": \"ACM-1001\",
                  \"aircraftType\": \"A320\",
                  \"softwareVersion\": \"1.2.3\",
                  \"navDataCycle\": \"AIRAC-2024-08\",
                  \"modules\": [ { \"name\": \"FMS\", \"enabled\": true } ]
                }
                """;
        JsonNode node = validationService.parse(json);
        when(repository.save(any(ConfigDocument.class))).thenAnswer(invocation -> {
            ConfigDocument doc = invocation.getArgument(0);
            if (doc.getId() == null) {
                doc.setId("generated-id");
            }
            return doc;
        });

        ConfigDocument saved = configService.save(node, json);

        assertThat(saved.getId()).isEqualTo("generated-id");
        assertThat(saved.getConfigId()).isEqualTo("ACM-1001");
        assertThat(saved.getSoftwareVersion()).isEqualTo("1.2.3");
        assertThat(saved.getNavDataCycle()).isEqualTo("AIRAC-2024-08");
        assertThat(saved.getModules()).contains("FMS");
    }

    @Test
    void buildReportCombinesValidationAndAnalyzer() throws Exception {
        String json = """
                {
                  \"configId\": \"ACM-1001\",
                  \"aircraftType\": \"A320\",
                  \"softwareVersion\": \"1.2.3\",
                  \"navDataCycle\": \"AIRAC-2024-08\",
                  \"modules\": [ { \"name\": \"FMS\", \"enabled\": true } ]
                }
                """;
        JsonNode node = validationService.parse(json);

        ConfigDocument doc = new ConfigDocument();
        doc.setId("doc-1");
        doc.setConfigId("ACM-1001");
        doc.setAircraftType("A320");
        doc.setSoftwareVersion("1.2.3");
        doc.setNavDataCycle("AIRAC-2024-08");
        doc.setRawJson(json);

        ValidationResult analyzerResult = new ValidationResult().addWarning("FMS module not enabled");
        stubAnalyzer.setResult(analyzerResult);

        ReportResponse report = configService.buildReport(doc, node);

        assertThat(report.getId()).isEqualTo("doc-1");
        assertThat(report.getValidation().getErrors()).isEmpty();
        assertThat(report.getAnalyzer().getWarnings()).contains("FMS module not enabled");
        assertThat(report.getMetadata().get("configId")).isEqualTo("ACM-1001");
    }

    private static class StubAnalyzer implements AnalyzerPort {
        private ValidationResult result = new ValidationResult();

        @Override
        public ValidationResult analyze(JsonNode node) {
            return result;
        }

        void setResult(ValidationResult result) {
            this.result = result;
        }
    }
}
