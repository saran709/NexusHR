package com.nexushr.payroll.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class SalaryStructureDto {

    @NotNull(message = "Employee ID is required")
    private UUID employeeId;

    @NotNull(message = "Basic salary is required")
    private Double basicSalary;

    private Double allowances = 0.0;
    private Double deductions = 0.0;

    public SalaryStructureDto() {}

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public Double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(Double basicSalary) { this.basicSalary = basicSalary; }

    public Double getAllowances() { return allowances; }
    public void setAllowances(Double allowances) { this.allowances = allowances; }

    public Double getDeductions() { return deductions; }
    public void setDeductions(Double deductions) { this.deductions = deductions; }
}
