package com.acm.backend.dto;

import java.time.Instant;

public class ConfigSummary {
    private String id;
    private String configId;
    private String aircraftType;
    private String softwareVersion;
    private String navDataCycle;
    private Instant createdAt;

    public ConfigSummary() {
    }

    public ConfigSummary(String id, String configId, String aircraftType, String softwareVersion, String navDataCycle, Instant createdAt) {
        this.id = id;
        this.configId = configId;
        this.aircraftType = aircraftType;
        this.softwareVersion = softwareVersion;
        this.navDataCycle = navDataCycle;
        this.createdAt = createdAt;
    }

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
