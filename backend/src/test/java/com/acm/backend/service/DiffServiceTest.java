package com.acm.backend.service;

import com.acm.backend.dto.DiffResponse;
import com.acm.backend.model.ConfigDocument;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DiffServiceTest {

    private final DiffService diffService = new DiffService();

    @Test
    void detectsFieldAndModuleChanges() {
        ConfigDocument first = new ConfigDocument();
        first.setId("one");
        first.setConfigId("ACM-1001");
        first.setAircraftType("A320");
        first.setSoftwareVersion("1.2.3");
        first.setNavDataCycle("AIRAC-2024-08");
        first.setModules("FMS,Comms");

        ConfigDocument second = new ConfigDocument();
        second.setId("two");
        second.setConfigId("ACM-1002");
        second.setAircraftType("A320");
        second.setSoftwareVersion("1.3.0");
        second.setNavDataCycle("AIRAC-2024-09");
        second.setModules("FMS,Weather");

        DiffResponse diff = diffService.diff(first, second);

        assertThat(diff.getChangedFields()).containsEntry("softwareVersion", "1.2.3 -> 1.3.0")
                .containsEntry("navDataCycle", "AIRAC-2024-08 -> AIRAC-2024-09")
                .containsEntry("configId", "ACM-1001 -> ACM-1002");
        assertThat(diff.getAddedModules()).containsExactly("Weather");
        assertThat(diff.getRemovedModules()).containsExactly("Comms");
    }
}
