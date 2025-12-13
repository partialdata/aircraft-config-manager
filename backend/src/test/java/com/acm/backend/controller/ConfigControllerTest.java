package com.acm.backend.controller;

import com.acm.backend.dto.ValidationResult;
import com.acm.backend.repository.ConfigRepository;
import com.acm.backend.service.AnalyzerPort;
import com.acm.backend.service.ConfigService;
import com.acm.backend.service.DiffService;
import com.acm.backend.service.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigControllerTest {

    @Mock
    private ConfigRepository repository;

    private ConfigController controller;
    private ValidationService validationService;
    private AnalyzerPort analyzerClient;
    private ConfigService configService;
    private DiffService diffService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
        analyzerClient = new StubAnalyzer();
        diffService = new DiffService();
        configService = new ConfigService(repository, validationService, analyzerClient);
        controller = new ConfigController(validationService, analyzerClient, configService, diffService, repository);
    }

    @Test
    void deleteRemovesExistingConfig() {
        when(repository.existsById("abc")).thenReturn(true);

        ResponseEntity<Void> response = controller.delete("abc");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(repository).deleteById("abc");
    }

    @Test
    void deleteReturnsNotFoundWhenMissing() {
        when(repository.existsById("missing")).thenReturn(false);

        ResponseEntity<Void> response = controller.delete("missing");

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    private static class StubAnalyzer implements AnalyzerPort {
        @Override
        public ValidationResult analyze(com.fasterxml.jackson.databind.JsonNode node) {
            return new ValidationResult();
        }
    }
}
