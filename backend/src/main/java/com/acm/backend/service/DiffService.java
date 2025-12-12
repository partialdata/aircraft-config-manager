package com.acm.backend.service;

import com.acm.backend.dto.DiffResponse;
import com.acm.backend.model.ConfigDocument;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DiffService {

    public DiffResponse diff(ConfigDocument first, ConfigDocument second) {
        DiffResponse response = new DiffResponse();
        response.setFirstId(first.getId());
        response.setSecondId(second.getId());

        Map<String, String> changes = new HashMap<>();
        compareField("aircraftType", first.getAircraftType(), second.getAircraftType(), changes);
        compareField("softwareVersion", first.getSoftwareVersion(), second.getSoftwareVersion(), changes);
        compareField("navDataCycle", first.getNavDataCycle(), second.getNavDataCycle(), changes);
        compareField("configId", first.getConfigId(), second.getConfigId(), changes);
        response.setChangedFields(changes);

        Set<String> firstModules = Set.copyOf(first.moduleList());
        Set<String> secondModules = Set.copyOf(second.moduleList());

        List<String> added = new ArrayList<>();
        for (String module : secondModules) {
            if (!firstModules.contains(module)) {
                added.add(module);
            }
        }
        List<String> removed = new ArrayList<>();
        for (String module : firstModules) {
            if (!secondModules.contains(module)) {
                removed.add(module);
            }
        }
        response.setAddedModules(added);
        response.setRemovedModules(removed);
        return response;
    }

    private void compareField(String key, String a, String b, Map<String, String> changes) {
        if (a == null && b == null) {
            return;
        }
        if (a == null || b == null || !a.equals(b)) {
            changes.put(key, (a == null ? "null" : a) + " -> " + (b == null ? "null" : b));
        }
    }
}
