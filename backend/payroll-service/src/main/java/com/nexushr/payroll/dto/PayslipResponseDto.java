package com.nexushr.payroll.dto;

import com.nexushr.payroll.entity.PayrollRecord;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PayslipResponseDto {

    private UUID recordId;
    private UUID employeeId;
    private UUID payrollRunId;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Double basicSalary;
    private Double allowances;
    private Double bonuses;
    private Double overtimePay;
    private Double grossSalary;
    private Double taxDeduction;
    private Double leaveDeduction;
    private Double otherDeductions;
    private Double netSalary;
    private List<ItemDto> items;

    public PayslipResponseDto() {}

    public PayslipResponseDto(PayrollRecord record) {
        this.recordId = record.getId();
        this.employeeId = record.getEmployeeId();
        if (record.getPayrollRun() != null) {
            this.payrollRunId = record.getPayrollRun().getId();
            this.periodStart = record.getPayrollRun().getPeriodStart();
            this.periodEnd = record.getPayrollRun().getPeriodEnd();
        }
        this.basicSalary = record.getBasicSalary();
        this.allowances = record.getAllowances();
        this.bonuses = record.getBonuses();
        this.overtimePay = record.getOvertimePay();
        this.grossSalary = record.getGrossSalary();
        this.taxDeduction = record.getTaxDeduction();
        this.leaveDeduction = record.getLeaveDeduction();
        this.otherDeductions = record.getOtherDeductions();
        this.netSalary = record.getNetSalary();
        if (record.getItems() != null) {
            this.items = record.getItems().stream()
                    .map(item -> new ItemDto(item.getComponentName(), item.getType(), item.getAmount()))
                    .collect(Collectors.toList());
        }
    }

    public static class ItemDto {
        private String componentName;
        private String type;
        private Double amount;

        public ItemDto(String componentName, String type, Double amount) {
            this.componentName = componentName;
            this.type = type;
            this.amount = amount;
        }

        public String getComponentName() { return componentName; }
        public String getType() { return type; }
        public Double getAmount() { return amount; }
    }

    public UUID getRecordId() { return recordId; }
    public UUID getEmployeeId() { return employeeId; }
    public UUID getPayrollRunId() { return payrollRunId; }
    public LocalDate getPeriodStart() { return periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public Double getBasicSalary() { return basicSalary; }
    public Double getAllowances() { return allowances; }
    public Double getBonuses() { return bonuses; }
    public Double getOvertimePay() { return overtimePay; }
    public Double getGrossSalary() { return grossSalary; }
    public Double getTaxDeduction() { return taxDeduction; }
    public Double getLeaveDeduction() { return leaveDeduction; }
    public Double getOtherDeductions() { return otherDeductions; }
    public Double getNetSalary() { return netSalary; }
    public List<ItemDto> getItems() { return items; }
}
