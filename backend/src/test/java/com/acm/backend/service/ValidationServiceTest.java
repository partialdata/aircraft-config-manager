package com.acm.backend.service;

import com.acm.backend.dto.ValidationResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationServiceTest {

    private ValidationService service;

    @BeforeEach
    void setUp() {
        service = new ValidationService();
    }

    @Test
    void validPayloadHasNoErrors() throws Exception {
        String json = """
                {
                  \"configId\": \"ACM-1001\",
                  \"aircraftType\": \"A320\",
                  \"softwareVersion\": \"1.2.3\",
                  \"navDataCycle\": \"AIRAC-2024-08\",
                  \"modules\": [ { \"name\": \"FMS\", \"enabled\": true } ]
                }
                """;
        JsonNode node = service.parse(json);

        ValidationResult result = service.validate(node);

        assertThat(result.getErrors()).isEmpty();
        assertThat(result.getWarnings()).isEmpty();
    }

    @Test
    void missingFieldsProduceErrors() throws Exception {
        String json = """
                {
                  \"aircraftType\": \"A320\",
                  \"modules\": []
                }
                """;
        JsonNode node = service.parse(json);

        ValidationResult result = service.validate(node);

        assertThat(result.getErrors()).contains("configId is required", "softwareVersion is required", "navDataCycle is required");
        assertThat(result.getWarnings()).contains("modules array is empty");
    }
}
