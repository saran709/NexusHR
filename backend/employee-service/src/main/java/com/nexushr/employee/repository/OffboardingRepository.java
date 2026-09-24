package com.nexushr.employee.repository;

import com.nexushr.employee.entity.Offboarding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OffboardingRepository extends JpaRepository<Offboarding, UUID> {
    Optional<Offboarding> findByEmployeeId(UUID employeeId);
}
