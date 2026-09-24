package com.nexushr.performance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "performance_ratings")
public class PerformanceRating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "review_id", nullable = false)
    private UUID reviewId;

    @Column(name = "rater_id", nullable = false)
    private UUID raterId;

    @Column(name = "rating_type", nullable = false, length = 30)
    private String ratingType; // SELF, MANAGER, PEER, HR

    @Column(nullable = false)
    private Double score;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public PerformanceRating() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getReviewId() { return reviewId; }
    public void setReviewId(UUID reviewId) { this.reviewId = reviewId; }

    public UUID getRaterId() { return raterId; }
    public void setRaterId(UUID raterId) { this.raterId = raterId; }

    public String getRatingType() { return ratingType; }
    public void setRatingType(String ratingType) { this.ratingType = ratingType; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
