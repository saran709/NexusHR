package com.nexushr.performance.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class PerformanceReviewDto {

    private UUID employeeId;

    @NotNull(message = "Cycle ID is required")
    private UUID cycleId;

    private String stage = "SELF_REVIEW";
    private String selfComments;
    private String managerComments;
    private String hrComments;
    private String status = "DRAFT";

    public PerformanceReviewDto() {}

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public String getSelfComments() { return selfComments; }
    public void setSelfComments(String selfComments) { this.selfComments = selfComments; }

    public String getManagerComments() { return managerComments; }
    public void setManagerComments(String managerComments) { this.managerComments = managerComments; }

    public String getHrComments() { return hrComments; }
    public void setHrComments(String hrComments) { this.hrComments = hrComments; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
