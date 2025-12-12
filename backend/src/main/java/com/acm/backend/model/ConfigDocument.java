package com.acm.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "configs")
public class ConfigDocument {
    @Id
    private String id;

    private String configId;
    private String aircraftType;
    private String softwareVersion;
    private String navDataCycle;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String rawJson;

    private Instant createdAt;

    // simple comma-separated modules to keep schema compact
    private String modules;

    public ConfigDocument() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
    }

    // getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getNavDataCycle() {
        return navDataCycle;
    }

    public void setNavDataCycle(String navDataCycle) {
        this.navDataCycle = navDataCycle;
    }

    public String getRawJson() {
        return rawJson;
    }

    public void setRawJson(String rawJson) {
        this.rawJson = rawJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getModules() {
        return modules;
    }

    public void setModules(String modules) {
        this.modules = modules;
    }

    public List<String> moduleList() {
        return modules == null || modules.isBlank() ? List.of() : List.of(modules.split(","));
    }
}
