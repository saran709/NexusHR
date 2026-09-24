package com.nexushr.payroll.repository;

import com.nexushr.payroll.entity.PayrollRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayrollRecordRepository extends JpaRepository<PayrollRecord, UUID> {
    Page<PayrollRecord> findByEmployeeId(UUID employeeId, Pageable pageable);
    List<PayrollRecord> findByEmployeeId(UUID employeeId);
    Optional<PayrollRecord> findByPayrollRunIdAndEmployeeId(UUID payrollRunId, UUID employeeId);
    List<PayrollRecord> findByPayrollRunId(UUID payrollRunId);
}
