package com.nexushr.performance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "performance_feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "review_id", nullable = false)
    private UUID reviewId;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(name = "author_role", nullable = false, length = 30)
    private String authorRole; // SELF, MANAGER, PEER, HR

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comments;

    private Double rating;

    @Column(name = "is_confidential", nullable = false)
    private Boolean isConfidential = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Feedback() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getReviewId() { return reviewId; }
    public void setReviewId(UUID reviewId) { this.reviewId = reviewId; }

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public UUID getAuthorId() { return authorId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }

    public String getAuthorRole() { return authorRole; }
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Boolean getIsConfidential() { return isConfidential; }
    public void setIsConfidential(Boolean isConfidential) { this.isConfidential = isConfidential; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
