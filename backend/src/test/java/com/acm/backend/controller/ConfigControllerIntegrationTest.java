package com.acm.backend.controller;

import com.acm.backend.dto.ValidationResult;
import com.acm.backend.model.ConfigDocument;
import com.acm.backend.repository.ConfigRepository;
import com.acm.backend.service.AnalyzerPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ConfigControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConfigRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void uploadJsonPersistsAndReturnsIds() throws Exception {
        String payload = """
                {
                  "configId": "ACM-1001",
                  "aircraftType": "A320",
                  "softwareVersion": "1.2.3",
                  "navDataCycle": "AIRAC-2024-08",
                  "modules": [ { "name": "FMS", "enabled": true } ]
                }
                """;

        MockMultipartFile jsonPart = new MockMultipartFile("configJson", "config.json", MediaType.APPLICATION_JSON_VALUE, payload.getBytes());
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/configs")
                                .file(jsonPart)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Upload successful"));

        assertThat(repository.findAll()).hasSize(1);
        ConfigDocument saved = repository.findAll().get(0);
        assertThat(saved.getConfigId()).isEqualTo("ACM-1001");
    }

    @Test
    void uploadInvalidJsonFails() throws Exception {
        String badPayload = "{ \"configId\": ";

        MockMultipartFile jsonPart = new MockMultipartFile("configJson", "config.json", MediaType.APPLICATION_JSON_VALUE, badPayload.getBytes());
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/configs")
                                .file(jsonPart)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.startsWith("Invalid JSON")));

        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void reportIncludesValidationAndAnalyzer() throws Exception {
        String payload = """
                {
                  "configId": "ACM-2001",
                  "aircraftType": "A320",
                  "softwareVersion": "1.2.3",
                  "navDataCycle": "AIRAC-2024-08",
                  "modules": [ { "name": "FMS", "enabled": true } ]
                }
                """;
        MockMultipartFile jsonPart = new MockMultipartFile("configJson", "config.json", MediaType.APPLICATION_JSON_VALUE, payload.getBytes());
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/configs")
                                .file(jsonPart)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andExpect(status().isOk());

        String id = repository.findAll().get(0).getId();

        mockMvc.perform(get("/api/configs/{id}/report", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.validation.errors").isEmpty())
                .andExpect(jsonPath("$.analyzer.warnings[0]").value("stub-warning"));
    }

    @TestConfiguration
    static class StubAnalyzerConfig {
        @Bean
        @Primary
        AnalyzerPort analyzerPort() {
            return node -> new ValidationResult().addWarning("stub-warning");
        }
    }
}
