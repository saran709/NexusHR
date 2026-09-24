package com.nexushr.payroll.service;

import com.nexushr.payroll.dto.PayrollResponseDto;
import com.nexushr.payroll.dto.PayrollRunDto;
import com.nexushr.payroll.dto.PayslipResponseDto;
import com.nexushr.payroll.entity.*;
import com.nexushr.payroll.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PayrollService {

    private final PayrollRunRepository payrollRunRepository;
    private final PayrollRecordRepository payrollRecordRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final TaxConfigurationRepository taxConfigurationRepository;
    private final AuditService auditService;

    public PayrollService(PayrollRunRepository payrollRunRepository,
                          PayrollRecordRepository payrollRecordRepository,
                          SalaryStructureRepository salaryStructureRepository,
                          TaxConfigurationRepository taxConfigurationRepository,
                          AuditService auditService) {
        this.payrollRunRepository = payrollRunRepository;
        this.payrollRecordRepository = payrollRecordRepository;
        this.salaryStructureRepository = salaryStructureRepository;
        this.taxConfigurationRepository = taxConfigurationRepository;
        this.auditService = auditService;
    }

    @Transactional
    public PayrollResponseDto createPayrollRun(PayrollRunDto dto, String userEmail, String ip) {
        payrollRunRepository.findByPeriodStartAndPeriodEnd(dto.getPeriodStart(), dto.getPeriodEnd())
                .ifPresent(run -> {
                    throw new IllegalStateException("A payroll run for this period already exists.");
                });

        PayrollRun run = new PayrollRun();
        run.setPeriodStart(dto.getPeriodStart());
        run.setPeriodEnd(dto.getPeriodEnd());
        run.setStatus("DRAFT");
        run.setProcessedBy(userEmail);

        PayrollRun saved = payrollRunRepository.save(run);
        auditService.log(userEmail, "CREATE_PAYROLL_RUN", "PayrollRun", saved.getId(), "Created payroll run for period " + dto.getPeriodStart() + " to " + dto.getPeriodEnd(), ip);
        return new PayrollResponseDto(saved);
    }

    public Page<PayrollResponseDto> getAllPayrollRuns(Pageable pageable) {
        return payrollRunRepository.findAll(pageable).map(PayrollResponseDto::new);
    }

    public PayrollResponseDto getPayrollRunById(UUID id) {
        PayrollRun run = payrollRunRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll run not found"));
        return new PayrollResponseDto(run);
    }

    @Transactional
    public PayrollResponseDto processPayrollRun(UUID id, String userEmail, String ip) {
        PayrollRun run = payrollRunRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll run not found"));

        if ("LOCKED".equals(run.getStatus())) {
            throw new IllegalStateException("Cannot modify or process a locked payroll run.");
        }

        run.setStatus("PROCESSING");
        payrollRunRepository.save(run);

        List<SalaryStructure> structures = salaryStructureRepository.findAll();
        if (structures.isEmpty()) {
            run.setStatus("FAILED");
            payrollRunRepository.save(run);
            throw new IllegalStateException("No salary structures found for payroll calculation.");
        }

        List<TaxConfiguration> taxConfigs = taxConfigurationRepository.findAll();
        List<PayrollRecord> records = new ArrayList<>();
        double totalPayout = 0.0;

        for (SalaryStructure structure : structures) {
            double basic = structure.getBasicSalary();
            double allowances = structure.getAllowances();
            double bonuses = 500.0; // Standard demo bonus / component
            double overtime = 200.0; // Standard demo overtime
            double gross = basic + allowances + bonuses + overtime;

            // Calculate tax based on configured rules
            double tax = calculateTax(gross, taxConfigs);
            double leaveDeduction = 150.0; // Demo leave deduction
            double otherDeductions = structure.getDeductions();
            double totalDeductions = tax + leaveDeduction + otherDeductions;
            double net = Math.max(0.0, gross - totalDeductions);

            PayrollRecord record = new PayrollRecord();
            record.setPayrollRun(run);
            record.setEmployeeId(structure.getEmployeeId());
            record.setBasicSalary(basic);
            record.setAllowances(allowances);
            record.setBonuses(bonuses);
            record.setOvertimePay(overtime);
            record.setGrossSalary(gross);
            record.setTaxDeduction(tax);
            record.setLeaveDeduction(leaveDeduction);
            record.setOtherDeductions(otherDeductions);
            record.setNetSalary(net);

            List<PayrollItem> items = new ArrayList<>();
            items.add(new PayrollItem(record, "Basic Salary", "EARNING", basic));
            items.add(new PayrollItem(record, "Allowances", "EARNING", allowances));
            items.add(new PayrollItem(record, "Bonus", "EARNING", bonuses));
            items.add(new PayrollItem(record, "Overtime", "EARNING", overtime));
            items.add(new PayrollItem(record, "Tax", "DEDUCTION", tax));
            items.add(new PayrollItem(record, "Leave Deduction", "DEDUCTION", leaveDeduction));
            items.add(new PayrollItem(record, "Other Deductions", "DEDUCTION", otherDeductions));
            record.setItems(items);

            records.add(record);
            totalPayout += net;
        }

        payrollRecordRepository.saveAll(records);

        run.setStatus("COMPLETED");
        run.setTotalEmployees(records.size());
        run.setTotalPayout(Math.round(totalPayout * 100.0) / 100.0);
        PayrollRun saved = payrollRunRepository.save(run);

        auditService.log(userEmail, "PROCESS_PAYROLL", "PayrollRun", saved.getId(), "Successfully processed payroll for " + records.size() + " employees", ip);
        return new PayrollResponseDto(saved);
    }

    @Transactional
    public PayrollResponseDto lockPayrollRun(UUID id, String userEmail, String ip) {
        PayrollRun run = payrollRunRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll run not found"));

        if (!"COMPLETED".equals(run.getStatus())) {
            throw new IllegalStateException("Only completed payroll runs can be locked.");
        }

        run.setStatus("LOCKED");
        PayrollRun saved = payrollRunRepository.save(run);

        auditService.log(userEmail, "LOCK_PAYROLL", "PayrollRun", saved.getId(), "Locked payroll run", ip);
        return new PayrollResponseDto(saved);
    }

    public List<PayslipResponseDto> getEmployeePayslips(UUID employeeId) {
        return payrollRecordRepository.findByEmployeeId(employeeId)
                .stream().map(PayslipResponseDto::new).collect(Collectors.toList());
    }

    public PayslipResponseDto getPayslipById(UUID recordId) {
        PayrollRecord record = payrollRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Payslip record not found"));
        return new PayslipResponseDto(record);
    }

    public List<PayslipResponseDto> getPayrollRunRecords(UUID payrollRunId) {
        return payrollRecordRepository.findByPayrollRunId(payrollRunId)
                .stream().map(PayslipResponseDto::new).collect(Collectors.toList());
    }

    public String exportPayrollCsv(UUID payrollRunId) {
        List<PayrollRecord> records = payrollRecordRepository.findByPayrollRunId(payrollRunId);
        StringBuilder sb = new StringBuilder();
        sb.append("EmployeeID,Basic,Allowances,Bonuses,Overtime,Gross,Tax,LeaveDeduction,NetSalary\n");
        for (PayrollRecord r : records) {
            sb.append(r.getEmployeeId()).append(",")
              .append(r.getBasicSalary()).append(",")
              .append(r.getAllowances()).append(",")
              .append(r.getBonuses()).append(",")
              .append(r.getOvertimePay()).append(",")
              .append(r.getGrossSalary()).append(",")
              .append(r.getTaxDeduction()).append(",")
              .append(r.getLeaveDeduction()).append(",")
              .append(r.getNetSalary()).append("\n");
        }
        return sb.toString();
    }

    private double calculateTax(double gross, List<TaxConfiguration> taxConfigs) {
        if (taxConfigs == null || taxConfigs.isEmpty()) {
            return gross * 0.10; // Default 10% if no config
        }
        for (TaxConfiguration config : taxConfigs) {
            if (gross >= config.getMinIncome() && (config.getMaxIncome() == null || gross <= config.getMaxIncome())) {
                return gross * (config.getTaxPercentage() / 100.0);
            }
        }
        return gross * 0.15; // Fallback tax
    }
}
