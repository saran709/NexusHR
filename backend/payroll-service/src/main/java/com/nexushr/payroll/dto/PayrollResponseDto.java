package com.nexushr.payroll.dto;

import com.nexushr.payroll.entity.PayrollRun;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class PayrollResponseDto {

    private UUID id;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String status;
    private Integer totalEmployees;
    private Double totalPayout;
    private String processedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PayrollResponseDto() {}

    public PayrollResponseDto(PayrollRun run) {
        this.id = run.getId();
        this.periodStart = run.getPeriodStart();
        this.periodEnd = run.getPeriodEnd();
        this.status = run.getStatus();
        this.totalEmployees = run.getTotalEmployees();
        this.totalPayout = run.getTotalPayout();
        this.processedBy = run.getProcessedBy();
        this.createdAt = run.getCreatedAt();
        this.updatedAt = run.getUpdatedAt();
    }

    public UUID getId() { return id; }
    public LocalDate getPeriodStart() { return periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public String getStatus() { return status; }
    public Integer getTotalEmployees() { return totalEmployees; }
    public Double getTotalPayout() { return totalPayout; }
    public String getProcessedBy() { return processedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
