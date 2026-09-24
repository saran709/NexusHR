package com.nexushr.performance.service;

import com.nexushr.performance.dto.PerformanceGoalDto;
import com.nexushr.performance.entity.PerformanceGoal;
import com.nexushr.performance.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PerformanceServiceTest {

    @Mock
    private PerformanceCycleRepository cycleRepository;

    @Mock
    private PerformanceGoalRepository goalRepository;

    @Mock
    private PerformanceReviewRepository reviewRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private PerformanceRatingRepository ratingRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private PerformanceService performanceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateGoalSuccess() {
        PerformanceGoalDto dto = new PerformanceGoalDto();
        dto.setTitle("Complete Microservices Migration");
        dto.setDescription("Migrate all modules to Spring Boot");
        dto.setCategory("OKR");
        dto.setTarget(100.0);
        dto.setMeasurement("percentage");
        dto.setWeight(2.0);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusMonths(3));

        PerformanceGoal saved = new PerformanceGoal();
        saved.setId(UUID.randomUUID());
        saved.setTitle(dto.getTitle());
        saved.setWeight(dto.getWeight());
        saved.setProgress(50.0);

        when(goalRepository.save(any())).thenReturn(saved);

        PerformanceGoal result = performanceService.createGoal(dto, "employee@nexushr.com", "127.0.0.1");

        assertNotNull(result);
        assertEquals("Complete Microservices Migration", result.getTitle());
        verify(goalRepository, times(1)).save(any());
        verify(auditService, times(1)).log(any(), eq("CREATE_GOAL"), any(), any(), any(), any());
    }

    @Test
    void testUpdateGoalProgress() {
        UUID goalId = UUID.randomUUID();
        PerformanceGoal goal = new PerformanceGoal();
        goal.setId(goalId);
        goal.setProgress(20.0);
        goal.setStatus("IN_PROGRESS");

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PerformanceGoal updated = performanceService.updateGoalProgress(goalId, 80.0, "IN_PROGRESS", "employee@nexushr.com", "127.0.0.1");

        assertNotNull(updated);
        assertEquals(80.0, updated.getProgress());
        verify(auditService, times(1)).log(any(), eq("UPDATE_GOAL_PROGRESS"), any(), any(), any(), any());
    }
}
