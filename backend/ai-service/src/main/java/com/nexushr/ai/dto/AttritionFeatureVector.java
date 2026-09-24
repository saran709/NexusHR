package com.nexushr.ai.dto;

import java.util.UUID;

public class AttritionFeatureVector {
    private UUID employeeId;
    private int tenureMonths;
    private double attendanceTrendRate;
    private double averageOvertimeHours;
    private double leaveUtilizationRate;
    private double performanceScore;
    private String salaryBand;
    private int promotionsCount;
    private double engagementScore;
    private double feedbackSentimentScore;
    private double workloadScore;
    private int trainingParticipationCount;

    // Getters and Setters
    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public int getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(int tenureMonths) { this.tenureMonths = tenureMonths; }

    public double getAttendanceTrendRate() { return attendanceTrendRate; }
    public void setAttendanceTrendRate(double attendanceTrendRate) { this.attendanceTrendRate = attendanceTrendRate; }

    public double getAverageOvertimeHours() { return averageOvertimeHours; }
    public void setAverageOvertimeHours(double averageOvertimeHours) { this.averageOvertimeHours = averageOvertimeHours; }

    public double getLeaveUtilizationRate() { return leaveUtilizationRate; }
    public void setLeaveUtilizationRate(double leaveUtilizationRate) { this.leaveUtilizationRate = leaveUtilizationRate; }

    public double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(double performanceScore) { this.performanceScore = performanceScore; }

    public String getSalaryBand() { return salaryBand; }
    public void setSalaryBand(String salaryBand) { this.salaryBand = salaryBand; }

    public int getPromotionsCount() { return promotionsCount; }
    public void setPromotionsCount(int promotionsCount) { this.promotionsCount = promotionsCount; }

    public double getEngagementScore() { return engagementScore; }
    public void setEngagementScore(double engagementScore) { this.engagementScore = engagementScore; }

    public double getFeedbackSentimentScore() { return feedbackSentimentScore; }
    public void setFeedbackSentimentScore(double feedbackSentimentScore) { this.feedbackSentimentScore = feedbackSentimentScore; }

    public double getWorkloadScore() { return workloadScore; }
    public void setWorkloadScore(double workloadScore) { this.workloadScore = workloadScore; }

    public int getTrainingParticipationCount() { return trainingParticipationCount; }
    public void setTrainingParticipationCount(int trainingParticipationCount) { this.trainingParticipationCount = trainingParticipationCount; }
}
