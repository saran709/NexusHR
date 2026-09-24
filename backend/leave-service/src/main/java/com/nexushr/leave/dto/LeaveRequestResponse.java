package com.nexushr.leave.dto;

import com.nexushr.leave.entity.LeaveRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class LeaveRequestResponse {

    private UUID id;
    private UUID employeeId;
    private LeaveTypeResponse leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalDays;
    private String reason;
    private String status;
    private UUID approvedBy;
    private LocalDateTime approvedAt;
    private String remarks;

    public LeaveRequestResponse() {}

    public LeaveRequestResponse(LeaveRequest request) {
        this.id = request.getId();
        this.employeeId = request.getEmployeeId();
        if (request.getLeaveType() != null) {
            this.leaveType = new LeaveTypeResponse(request.getLeaveType());
        }
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.totalDays = request.getTotalDays();
        this.reason = request.getReason();
        this.status = request.getStatus();
        this.approvedBy = request.getApprovedBy();
        this.approvedAt = request.getApprovedAt();
        this.remarks = request.getRemarks();
    }

    public UUID getId() { return id; }
    public UUID getEmployeeId() { return employeeId; }
    public LeaveTypeResponse getLeaveType() { return leaveType; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Double getTotalDays() { return totalDays; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public UUID getApprovedBy() { return approvedBy; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public String getRemarks() { return remarks; }
}
