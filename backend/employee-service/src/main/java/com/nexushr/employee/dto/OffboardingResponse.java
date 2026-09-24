package com.nexushr.employee.dto;

import com.nexushr.employee.entity.Offboarding;
import java.time.LocalDate;
import java.util.UUID;

public class OffboardingResponse {

    private UUID id;
    private UUID employeeId;
    private String reason;
    private LocalDate resignationDate;
    private LocalDate lastWorkingDate;
    private String clearanceStatus;
    private String status;
    private String remarks;

    public OffboardingResponse() {}

    public OffboardingResponse(Offboarding offboarding) {
        this.id = offboarding.getId();
        this.employeeId = offboarding.getEmployee().getId();
        this.reason = offboarding.getReason();
        this.resignationDate = offboarding.getResignationDate();
        this.lastWorkingDate = offboarding.getLastWorkingDate();
        this.clearanceStatus = offboarding.getClearanceStatus();
        this.status = offboarding.getStatus();
        this.remarks = offboarding.getRemarks();
    }

    public UUID getId() { return id; }
    public UUID getEmployeeId() { return employeeId; }
    public String getReason() { return reason; }
    public LocalDate getResignationDate() { return resignationDate; }
    public LocalDate getLastWorkingDate() { return lastWorkingDate; }
    public String getClearanceStatus() { return clearanceStatus; }
    public String getStatus() { return status; }
    public String getRemarks() { return remarks; }
}
