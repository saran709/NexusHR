package com.nexushr.performance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class FeedbackDto {

    @NotNull(message = "Review ID is required")
    private UUID reviewId;

    private UUID employeeId;

    @NotBlank(message = "Author role is required")
    private String authorRole; // SELF, MANAGER, PEER, HR

    @NotBlank(message = "Comments are required")
    private String comments;

    private Double rating;
    private Boolean isConfidential = false;

    public FeedbackDto() {}

    public UUID getReviewId() { return reviewId; }
    public void setReviewId(UUID reviewId) { this.reviewId = reviewId; }

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public String getAuthorRole() { return authorRole; }
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Boolean getIsConfidential() { return isConfidential; }
    public void setIsConfidential(Boolean isConfidential) { this.isConfidential = isConfidential; }
}
