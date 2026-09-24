package com.nexushr.ai.model;

import java.time.Instant;
import java.util.UUID;

public class AttritionPrediction {
    private UUID employeeId;
    private String riskLevel; // LOW, MEDIUM, HIGH
    private double confidenceScore;
    private String modelVersion;
    private String featureSummary;
    private Instant generatedAt;
    private String explanation;
    private String recommendation;

    public AttritionPrediction(UUID employeeId, String riskLevel, double confidenceScore, String modelVersion, String featureSummary, String explanation, String recommendation) {
        this.employeeId = employeeId;
        this.riskLevel = riskLevel;
        this.confidenceScore = confidenceScore;
        this.modelVersion = modelVersion;
        this.featureSummary = featureSummary;
        this.generatedAt = Instant.now();
        this.explanation = explanation;
        this.recommendation = recommendation;
    }

    // Getters
    public UUID getEmployeeId() { return employeeId; }
    public String getRiskLevel() { return riskLevel; }
    public double getConfidenceScore() { return confidenceScore; }
    public String getModelVersion() { return modelVersion; }
    public String getFeatureSummary() { return featureSummary; }
    public Instant getGeneratedAt() { return generatedAt; }
    public String getExplanation() { return explanation; }
    public String getRecommendation() { return recommendation; }
}
