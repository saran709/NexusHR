package com.nexushr.performance.dto;

import java.util.List;
import java.util.UUID;

public class ScorecardResponseDto {

    private UUID employeeId;
    private UUID cycleId;
    private Double weightedScore;
    private int totalGoals;
    private int completedGoals;
    private List<PerformanceGoalDto> goals;
    private List<FeedbackDto> feedback;

    public ScorecardResponseDto() {}

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }

    public Double getWeightedScore() { return weightedScore; }
    public void setWeightedScore(Double weightedScore) { this.weightedScore = weightedScore; }

    public int getTotalGoals() { return totalGoals; }
    public void setTotalGoals(int totalGoals) { this.totalGoals = totalGoals; }

    public int getCompletedGoals() { return completedGoals; }
    public void setCompletedGoals(int completedGoals) { this.completedGoals = completedGoals; }

    public List<PerformanceGoalDto> getGoals() { return goals; }
    public void setGoals(List<PerformanceGoalDto> goals) { this.goals = goals; }

    public List<FeedbackDto> getFeedback() { return feedback; }
    public void setFeedback(List<FeedbackDto> feedback) { this.feedback = feedback; }
}
