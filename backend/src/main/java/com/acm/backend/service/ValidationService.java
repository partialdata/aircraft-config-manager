package com.acm.backend.service;

import com.acm.backend.dto.ValidationResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class ValidationService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Pattern SEMVER = Pattern.compile("^\\d+\\.\\d+\\.\\d+$");
    private static final Pattern AIRAC = Pattern.compile("^AIRAC-\\d{4}-\\d{2}$");

    public JsonNode parse(String json) throws IOException {
        return objectMapper.readTree(json);
    }

    public Map<String, Object> extractMetadata(JsonNode node) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("configId", text(node, "configId"));
        metadata.put("aircraftType", text(node, "aircraftType"));
        metadata.put("softwareVersion", text(node, "softwareVersion"));
        metadata.put("navDataCycle", text(node, "navDataCycle"));
        metadata.put("modules", node.has("modules") && node.get("modules").isArray() ? node.get("modules") : null);
        return metadata;
    }

    public ValidationResult validate(JsonNode node) {
        ValidationResult result = new ValidationResult();
        checkRequired(node, "configId", result);
        checkRequired(node, "aircraftType", result);
        checkRequired(node, "softwareVersion", result);
        checkRequired(node, "navDataCycle", result);

        if (node.has("softwareVersion") && !SEMVER.matcher(node.get("softwareVersion").asText("")) .matches()) {
            result.addError("softwareVersion must follow MAJOR.MINOR.PATCH semver format");
        }
        if (node.has("navDataCycle") && !AIRAC.matcher(node.get("navDataCycle").asText("")) .matches()) {
            result.addWarning("navDataCycle should follow AIRAC-YYYY-NN format");
        }

        if (!node.has("modules") || !node.get("modules").isArray()) {
            result.addError("modules array is required");
        } else if (!node.get("modules").elements().hasNext()) {
            result.addWarning("modules array is empty");
        } else {
            Iterator<JsonNode> it = node.get("modules").elements();
            boolean fmsFound = false;
            while (it.hasNext()) {
                JsonNode module = it.next();
                String name = module.has("name") ? module.get("name").asText("") : "";
                boolean enabled = module.has("enabled") && module.get("enabled").asBoolean(false);
                if (name.isEmpty()) {
                    result.addWarning("module missing name");
                }
                if ("FMS".equalsIgnoreCase(name) && enabled) {
                    fmsFound = true;
                }
            }
            if (!fmsFound) {
                result.addWarning("FMS module should be present and enabled");
            }
        }
        return result;
    }

    private void checkRequired(JsonNode node, String field, ValidationResult result) {
        if (!node.has(field) || node.get(field).asText("").isBlank()) {
            result.addError(field + " is required");
        }
    }

    private String text(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asText(null) : null;
    }
}
