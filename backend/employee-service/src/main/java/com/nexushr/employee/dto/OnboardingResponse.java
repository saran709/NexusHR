package com.nexushr.employee.dto;

import com.nexushr.employee.entity.Onboarding;
import java.time.LocalDateTime;
import java.util.UUID;

public class OnboardingResponse {

    private UUID id;
    private UUID employeeId;
    private String status;
    private String checklistJson;
    private UUID approvedBy;
    private LocalDateTime approvedAt;
    private String remarks;

    public OnboardingResponse() {}

    public OnboardingResponse(Onboarding onboarding) {
        this.id = onboarding.getId();
        this.employeeId = onboarding.getEmployee().getId();
        this.status = onboarding.getStatus();
        this.checklistJson = onboarding.getChecklistJson();
        this.approvedBy = onboarding.getApprovedBy();
        this.approvedAt = onboarding.getApprovedAt();
        this.remarks = onboarding.getRemarks();
    }

    public UUID getId() { return id; }
    public UUID getEmployeeId() { return employeeId; }
    public String getStatus() { return status; }
    public String getChecklistJson() { return checklistJson; }
    public UUID getApprovedBy() { return approvedBy; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public String getRemarks() { return remarks; }
}
