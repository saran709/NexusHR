package com.nexushr.payroll.repository;

import com.nexushr.payroll.entity.PayrollRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayrollRunRepository extends JpaRepository<PayrollRun, UUID> {
    Optional<PayrollRun> findByPeriodStartAndPeriodEnd(LocalDate periodStart, LocalDate periodEnd);
}
