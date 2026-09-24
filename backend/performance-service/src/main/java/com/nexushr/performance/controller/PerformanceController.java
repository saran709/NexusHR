package com.nexushr.performance.controller;

import com.nexushr.performance.dto.*;
import com.nexushr.performance.entity.*;
import com.nexushr.performance.service.PerformanceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @PostMapping("/cycles")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<PerformanceCycle> createCycle(@Valid @RequestBody PerformanceCycleDto dto,
                                                        Authentication authentication,
                                                        HttpServletRequest request) {
        String email = authentication.getName();
        String ip = getClientIp(request);
        return new ResponseEntity<>(performanceService.createCycle(dto, email, ip), HttpStatus.CREATED);
    }

    @GetMapping("/cycles")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<PerformanceCycle>> getAllCycles() {
        return ResponseEntity.ok(performanceService.getAllCycles());
    }

    @PostMapping("/goals")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<PerformanceGoal> createGoal(@Valid @RequestBody PerformanceGoalDto dto,
                                                      Authentication authentication,
                                                      HttpServletRequest request) {
        String email = authentication.getName();
        String ip = getClientIp(request);
        return new ResponseEntity<>(performanceService.createGoal(dto, email, ip), HttpStatus.CREATED);
    }

    @GetMapping("/goals/me")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<PerformanceGoal>> getMyGoals(Authentication authentication) {
        UUID employeeId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        return ResponseEntity.ok(performanceService.getGoalsByEmployee(employeeId));
    }

    @PutMapping("/goals/{id}/progress")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<PerformanceGoal> updateGoalProgress(@PathVariable UUID id,
                                                              @RequestParam Double progress,
                                                              @RequestParam(required = false) String status,
                                                              Authentication authentication,
                                                              HttpServletRequest request) {
        String email = authentication.getName();
        String ip = getClientIp(request);
        return ResponseEntity.ok(performanceService.updateGoalProgress(id, progress, status, email, ip));
    }

    @PostMapping("/reviews")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<PerformanceReview> saveReview(@Valid @RequestBody PerformanceReviewDto dto,
                                                        Authentication authentication,
                                                        HttpServletRequest request) {
        String email = authentication.getName();
        String ip = getClientIp(request);
        return new ResponseEntity<>(performanceService.createOrUpdateReview(dto, email, ip), HttpStatus.CREATED);
    }

    @GetMapping("/reviews/me")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<PerformanceReview>> getMyReviews(Authentication authentication) {
        UUID employeeId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        return ResponseEntity.ok(performanceService.getReviewsByEmployee(employeeId));
    }

    @PostMapping("/feedback")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Feedback> submitFeedback(@Valid @RequestBody FeedbackDto dto,
                                                   Authentication authentication,
                                                   HttpServletRequest request) {
        String email = authentication.getName();
        String ip = getClientIp(request);
        return new ResponseEntity<>(performanceService.submitFeedback(dto, email, ip), HttpStatus.CREATED);
    }

    @GetMapping("/reviews/{reviewId}/feedback")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<Feedback>> getReviewFeedback(@PathVariable UUID reviewId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(performanceService.getFeedbackForReview(reviewId, email));
    }

    @GetMapping("/scorecard")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ScorecardResponseDto> getScorecard(@RequestParam UUID employeeId, @RequestParam UUID cycleId) {
        return ResponseEntity.ok(performanceService.getScorecard(employeeId, cycleId));
    }

    @GetMapping("/trends")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<PerformanceTrendDto>> getTrends(Authentication authentication) {
        UUID employeeId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        return ResponseEntity.ok(performanceService.getTrends(employeeId));
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        return xf != null ? xf.split(",")[0] : request.getRemoteAddr();
    }
}
