package com.nexushr.performance.service;

import com.nexushr.performance.dto.*;
import com.nexushr.performance.entity.*;
import com.nexushr.performance.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PerformanceService {

    private final PerformanceCycleRepository cycleRepository;
    private final PerformanceGoalRepository goalRepository;
    private final PerformanceReviewRepository reviewRepository;
    private final FeedbackRepository feedbackRepository;
    private final PerformanceRatingRepository ratingRepository;
    private final AuditService auditService;

    public PerformanceService(PerformanceCycleRepository cycleRepository,
                              PerformanceGoalRepository goalRepository,
                              PerformanceReviewRepository reviewRepository,
                              FeedbackRepository feedbackRepository,
                              PerformanceRatingRepository ratingRepository,
                              AuditService auditService) {
        this.cycleRepository = cycleRepository;
        this.goalRepository = goalRepository;
        this.reviewRepository = reviewRepository;
        this.feedbackRepository = feedbackRepository;
        this.ratingRepository = ratingRepository;
        this.auditService = auditService;
    }

    // Cycles
    @Transactional
    public PerformanceCycle createCycle(PerformanceCycleDto dto, String userEmail, String ip) {
        PerformanceCycle cycle = new PerformanceCycle();
        cycle.setName(dto.getName());
        cycle.setDescription(dto.getDescription());
        cycle.setStartDate(dto.getStartDate());
        cycle.setEndDate(dto.getEndDate());
        cycle.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");

        PerformanceCycle saved = cycleRepository.save(cycle);
        auditService.log(userEmail, "CREATE_PERFORMANCE_CYCLE", "PerformanceCycle", saved.getId(), "Created cycle " + saved.getName(), ip);
        return saved;
    }

    public List<PerformanceCycle> getAllCycles() {
        return cycleRepository.findAll();
    }

    // Goals
    @Transactional
    public PerformanceGoal createGoal(PerformanceGoalDto dto, String userEmail, String ip) {
        UUID employeeId = dto.getEmployeeId() != null ? dto.getEmployeeId() : UUID.nameUUIDFromBytes(userEmail.getBytes());

        PerformanceGoal goal = new PerformanceGoal();
        goal.setEmployeeId(employeeId);
        goal.setCycleId(dto.getCycleId());
        goal.setTitle(dto.getTitle());
        goal.setDescription(dto.getDescription());
        goal.setCategory(dto.getCategory() != null ? dto.getCategory() : "KPI");
        goal.setTarget(dto.getTarget());
        goal.setMeasurement(dto.getMeasurement());
        goal.setWeight(dto.getWeight() != null ? dto.getWeight() : 1.0);
        goal.setStartDate(dto.getStartDate());
        goal.setEndDate(dto.getEndDate());
        goal.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING");
        goal.setProgress(dto.getProgress() != null ? dto.getProgress() : 0.0);

        PerformanceGoal saved = goalRepository.save(goal);
        auditService.log(userEmail, "CREATE_GOAL", "PerformanceGoal", saved.getId(), "Created goal " + saved.getTitle(), ip);
        return saved;
    }

    public List<PerformanceGoal> getGoalsByEmployee(UUID employeeId) {
        return goalRepository.findByEmployeeId(employeeId);
    }

    @Transactional
    public PerformanceGoal updateGoalProgress(UUID goalId, Double progress, String status, String userEmail, String ip) {
        PerformanceGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        goal.setProgress(progress);
        if (status != null) {
            goal.setStatus(status);
        }
        PerformanceGoal saved = goalRepository.save(goal);
        auditService.log(userEmail, "UPDATE_GOAL_PROGRESS", "PerformanceGoal", saved.getId(), "Updated goal progress to " + progress + "%", ip);
        return saved;
    }

    // Reviews
    @Transactional
    public PerformanceReview createOrUpdateReview(PerformanceReviewDto dto, String userEmail, String ip) {
        UUID employeeId = dto.getEmployeeId() != null ? dto.getEmployeeId() : UUID.nameUUIDFromBytes(userEmail.getBytes());

        PerformanceReview review = reviewRepository.findByEmployeeIdAndCycleId(employeeId, dto.getCycleId())
                .orElse(new PerformanceReview());

        if ("FINALIZED".equals(review.getStatus())) {
            throw new IllegalStateException("Finalized reviews cannot be modified.");
        }

        review.setEmployeeId(employeeId);
        review.setCycleId(dto.getCycleId());
        if (dto.getStage() != null) review.setStage(dto.getStage());
        if (dto.getSelfComments() != null) review.setSelfComments(dto.getSelfComments());
        if (dto.getManagerComments() != null) review.setManagerComments(dto.getManagerComments());
        if (dto.getHrComments() != null) review.setHrComments(dto.getHrComments());
        if (dto.getStatus() != null) review.setStatus(dto.getStatus());

        // Calculate weighted score based on employee goals
        List<PerformanceGoal> goals = goalRepository.findByEmployeeIdAndCycleId(employeeId, dto.getCycleId());
        double weightedScore = calculateWeightedScore(goals);
        review.setWeightedScore(weightedScore);

        PerformanceReview saved = reviewRepository.save(review);
        auditService.log(userEmail, "SAVE_REVIEW", "PerformanceReview", saved.getId(), "Saved review for cycle " + dto.getCycleId(), ip);
        return saved;
    }

    public List<PerformanceReview> getReviewsByEmployee(UUID employeeId) {
        return reviewRepository.findByEmployeeId(employeeId);
    }

    public PerformanceReview getReviewById(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Performance review not found"));
    }

    // Feedback & 360-degree
    @Transactional
    public Feedback submitFeedback(FeedbackDto dto, String userEmail, String ip) {
        UUID authorId = UUID.nameUUIDFromBytes(userEmail.getBytes());
        UUID employeeId = dto.getEmployeeId() != null ? dto.getEmployeeId() : authorId;

        Feedback feedback = new Feedback();
        feedback.setReviewId(dto.getReviewId());
        feedback.setEmployeeId(employeeId);
        feedback.setAuthorId(authorId);
        feedback.setAuthorRole(dto.getAuthorRole());
        feedback.setComments(dto.getComments());
        feedback.setRating(dto.getRating());
        feedback.setIsConfidential(dto.getIsConfidential() != null ? dto.getIsConfidential() : false);

        Feedback saved = feedbackRepository.save(feedback);
        auditService.log(userEmail, "SUBMIT_FEEDBACK", "Feedback", saved.getId(), "Submitted feedback as " + dto.getAuthorRole(), ip);
        return saved;
    }

    public List<Feedback> getFeedbackForReview(UUID reviewId, String viewerEmail) {
        List<Feedback> list = feedbackRepository.findByReviewId(reviewId);
        boolean isHrOrManager = viewerEmail.contains("admin") || viewerEmail.contains("hr") || viewerEmail.contains("manager");

        return list.stream().map(f -> {
            if (f.getIsConfidential() && !isHrOrManager) {
                // Mask confidential feedback for normal employees
                Feedback masked = new Feedback();
                masked.setId(f.getId());
                masked.setReviewId(f.getReviewId());
                masked.setEmployeeId(f.getEmployeeId());
                masked.setAuthorRole(f.getAuthorRole());
                masked.setComments("[Confidential Feedback Protected]");
                masked.setRating(null);
                masked.setIsConfidential(true);
                return masked;
            }
            return f;
        }).collect(Collectors.toList());
    }

    // Scorecard
    public ScorecardResponseDto getScorecard(UUID employeeId, UUID cycleId) {
        List<PerformanceGoal> goals = goalRepository.findByEmployeeIdAndCycleId(employeeId, cycleId);
        List<Feedback> feedback = feedbackRepository.findByEmployeeId(employeeId);

        double weightedScore = calculateWeightedScore(goals);
        int totalGoals = goals.size();
        int completedGoals = (int) goals.stream().filter(g -> "COMPLETED".equals(g.getStatus()) || g.getProgress() >= 100.0).count();

        ScorecardResponseDto scorecard = new ScorecardResponseDto();
        scorecard.setEmployeeId(employeeId);
        scorecard.setCycleId(cycleId);
        scorecard.setWeightedScore(weightedScore);
        scorecard.setTotalGoals(totalGoals);
        scorecard.setCompletedGoals(completedGoals);
        scorecard.setGoals(goals.stream().map(g -> {
            PerformanceGoalDto dto = new PerformanceGoalDto();
            dto.setEmployeeId(g.getEmployeeId());
            dto.setCycleId(g.getCycleId());
            dto.setTitle(g.getTitle());
            dto.setDescription(g.getDescription());
            dto.setCategory(g.getCategory());
            dto.setTarget(g.getTarget());
            dto.setMeasurement(g.getMeasurement());
            dto.setWeight(g.getWeight());
            dto.setStartDate(g.getStartDate());
            dto.setEndDate(g.getEndDate());
            dto.setStatus(g.getStatus());
            dto.setProgress(g.getProgress());
            return dto;
        }).collect(Collectors.toList()));

        scorecard.setFeedback(feedback.stream().map(f -> {
            FeedbackDto dto = new FeedbackDto();
            dto.setReviewId(f.getReviewId());
            dto.setEmployeeId(f.getEmployeeId());
            dto.setAuthorRole(f.getAuthorRole());
            dto.setComments(f.getComments());
            dto.setRating(f.getRating());
            dto.setIsConfidential(f.getIsConfidential());
            return dto;
        }).collect(Collectors.toList()));

        return scorecard;
    }

    // Trends
    public List<PerformanceTrendDto> getTrends(UUID employeeId) {
        List<PerformanceReview> reviews = reviewRepository.findByEmployeeId(employeeId);
        List<PerformanceTrendDto> trends = new ArrayList<>();

        for (PerformanceReview r : reviews) {
            PerformanceCycle cycle = cycleRepository.findById(r.getCycleId()).orElse(null);
            String cycleName = cycle != null ? cycle.getName() : "Unknown Cycle";
            trends.add(new PerformanceTrendDto(r.getCycleId(), cycleName, r.getWeightedScore(), r.getStatus()));
        }
        return trends;
    }

    private double calculateWeightedScore(List<PerformanceGoal> goals) {
        if (goals == null || goals.isEmpty()) {
            return 0.0;
        }
        double totalWeightedProgress = 0.0;
        double totalWeight = 0.0;

        for (PerformanceGoal g : goals) {
            double weight = g.getWeight() != null ? g.getWeight() : 1.0;
            double progress = g.getProgress() != null ? g.getProgress() : 0.0;
            totalWeightedProgress += (progress * weight);
            totalWeight += weight;
        }

        if (totalWeight == 0.0) return 0.0;
        return Math.round((totalWeightedProgress / totalWeight) * 100.0) / 100.0;
    }
}
