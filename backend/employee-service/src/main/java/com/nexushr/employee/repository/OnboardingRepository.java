package com.nexushr.employee.repository;

import com.nexushr.employee.entity.Onboarding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OnboardingRepository extends JpaRepository<Onboarding, UUID> {
    Optional<Onboarding> findByEmployeeId(UUID employeeId);
}
