package com.nexushr.payroll.repository;

import com.nexushr.payroll.entity.SalaryComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalaryComponentRepository extends JpaRepository<SalaryComponent, UUID> {
    Optional<SalaryComponent> findByName(String name);
}
