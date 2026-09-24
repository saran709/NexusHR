package com.nexushr.performance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "performance_reviews")
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "reviewer_id")
    private UUID reviewerId;

    @Column(name = "cycle_id", nullable = false)
    private UUID cycleId;

    @Column(nullable = false, length = 30)
    private String stage = "SELF_REVIEW"; // SELF_REVIEW, MANAGER_REVIEW, PEER_FEEDBACK, HR_REVIEW, FINALIZED

    @Column(name = "self_comments", columnDefinition = "TEXT")
    private String selfComments;

    @Column(name = "manager_comments", columnDefinition = "TEXT")
    private String managerComments;

    @Column(name = "hr_comments", columnDefinition = "TEXT")
    private String hrComments;

    @Column(nullable = false, length = 30)
    private String status = "DRAFT"; // DRAFT, SUBMITTED, FINALIZED

    @Column(name = "weighted_score")
    private Double weightedScore = 0.0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public PerformanceReview() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public UUID getReviewerId() { return reviewerId; }
    public void setReviewerId(UUID reviewerId) { this.reviewerId = reviewerId; }

    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public String getSelfComments() { return selfComments; }
    public void setSelfComments(String selfComments) { this.selfComments = selfComments; }

    public String getManagerComments() { return managerComments; }
    public void setManagerComments(String managerComments) { this.managerComments = managerComments; }

    public String getHrComments() { return hrComments; }
    public void setHrComments(String hrComments) { this.hrComments = hrComments; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getWeightedScore() { return weightedScore; }
    public void setWeightedScore(Double weightedScore) { this.weightedScore = weightedScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
