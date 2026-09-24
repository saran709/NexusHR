package com.nexushr.performance.dto;

import java.util.UUID;

public class PerformanceTrendDto {

    private UUID cycleId;
    private String cycleName;
    private Double weightedScore;
    private String status;

    public PerformanceTrendDto() {}

    public PerformanceTrendDto(UUID cycleId, String cycleName, Double weightedScore, String status) {
        this.cycleId = cycleId;
        this.cycleName = cycleName;
        this.weightedScore = weightedScore;
        this.status = status;
    }

    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }

    public String getCycleName() { return cycleName; }
    public void setCycleName(String cycleName) { this.cycleName = cycleName; }

    public Double getWeightedScore() { return weightedScore; }
    public void setWeightedScore(Double weightedScore) { this.weightedScore = weightedScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
