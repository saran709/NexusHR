package com.nexushr.performance.repository;

import com.nexushr.performance.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, UUID> {
    List<PerformanceReview> findByEmployeeId(UUID employeeId);
    List<PerformanceReview> findByCycleId(UUID cycleId);
    Optional<PerformanceReview> findByEmployeeIdAndCycleId(UUID employeeId, UUID cycleId);
}
