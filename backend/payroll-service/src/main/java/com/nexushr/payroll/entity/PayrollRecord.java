package com.nexushr.payroll.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "payroll_records", indexes = {
    @Index(name = "idx_payroll_record_run_emp", columnList = "payroll_run_id, employee_id", unique = true)
})
public class PayrollRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_run_id", nullable = false)
    private PayrollRun payrollRun;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "basic_salary", nullable = false)
    private Double basicSalary;

    @Column(name = "allowances", nullable = false)
    private Double allowances = 0.0;

    @Column(name = "bonuses", nullable = false)
    private Double bonuses = 0.0;

    @Column(name = "overtime_pay", nullable = false)
    private Double overtimePay = 0.0;

    @Column(name = "gross_salary", nullable = false)
    private Double grossSalary;

    @Column(name = "tax_deduction", nullable = false)
    private Double taxDeduction = 0.0;

    @Column(name = "leave_deduction", nullable = false)
    private Double leaveDeduction = 0.0;

    @Column(name = "other_deductions", nullable = false)
    private Double otherDeductions = 0.0;

    @Column(name = "net_salary", nullable = false)
    private Double netSalary;

    @OneToMany(mappedBy = "payrollRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PayrollItem> items;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public PayrollRecord() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public PayrollRun getPayrollRun() { return payrollRun; }
    public void setPayrollRun(PayrollRun payrollRun) { this.payrollRun = payrollRun; }

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public Double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(Double basicSalary) { this.basicSalary = basicSalary; }

    public Double getAllowances() { return allowances; }
    public void setAllowances(Double allowances) { this.allowances = allowances; }

    public Double getBonuses() { return bonuses; }
    public void setBonuses(Double bonuses) { this.bonuses = bonuses; }

    public Double getOvertimePay() { return overtimePay; }
    public void setOvertimePay(Double overtimePay) { this.overtimePay = overtimePay; }

    public Double getGrossSalary() { return grossSalary; }
    public void setGrossSalary(Double grossSalary) { this.grossSalary = grossSalary; }

    public Double getTaxDeduction() { return taxDeduction; }
    public void setTaxDeduction(Double taxDeduction) { this.taxDeduction = taxDeduction; }

    public Double getLeaveDeduction() { return leaveDeduction; }
    public void setLeaveDeduction(Double leaveDeduction) { this.leaveDeduction = leaveDeduction; }

    public Double getOtherDeductions() { return otherDeductions; }
    public void setOtherDeductions(Double otherDeductions) { this.otherDeductions = otherDeductions; }

    public Double getNetSalary() { return netSalary; }
    public void setNetSalary(Double netSalary) { this.netSalary = netSalary; }

    public List<PayrollItem> getItems() { return items; }
    public void setItems(List<PayrollItem> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
