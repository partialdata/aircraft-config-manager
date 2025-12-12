package com.acm.backend.dto;

import java.util.List;
import java.util.Map;

public class DiffResponse {
    private String firstId;
    private String secondId;
    private Map<String, String> changedFields;
    private List<String> addedModules;
    private List<String> removedModules;

    public String getFirstId() {
        return firstId;
    }

    public void setFirstId(String firstId) {
        this.firstId = firstId;
    }

    public String getSecondId() {
        return secondId;
    }

    public void setSecondId(String secondId) {
        this.secondId = secondId;
    }

    public Map<String, String> getChangedFields() {
        return changedFields;
    }

    public void setChangedFields(Map<String, String> changedFields) {
        this.changedFields = changedFields;
    }

    public List<String> getAddedModules() {
        return addedModules;
    }

    public void setAddedModules(List<String> addedModules) {
        this.addedModules = addedModules;
    }

    public List<String> getRemovedModules() {
        return removedModules;
    }

    public void setRemovedModules(List<String> removedModules) {
        this.removedModules = removedModules;
    }
}
