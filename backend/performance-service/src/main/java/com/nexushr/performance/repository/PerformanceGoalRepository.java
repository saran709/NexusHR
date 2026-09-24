package com.nexushr.performance.repository;

import com.nexushr.performance.entity.PerformanceGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PerformanceGoalRepository extends JpaRepository<PerformanceGoal, UUID> {
    List<PerformanceGoal> findByEmployeeId(UUID employeeId);
    List<PerformanceGoal> findByEmployeeIdAndCycleId(UUID employeeId, UUID cycleId);
}
