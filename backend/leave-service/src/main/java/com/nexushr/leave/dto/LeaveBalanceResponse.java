package com.nexushr.leave.dto;

import com.nexushr.leave.entity.LeaveBalance;
import java.util.UUID;

public class LeaveBalanceResponse {

    private UUID id;
    private UUID employeeId;
    private LeaveTypeResponse leaveType;
    private Double totalDays;
    private Double usedDays;
    private Double pendingDays;
    private Integer year;

    public LeaveBalanceResponse() {}

    public LeaveBalanceResponse(LeaveBalance balance) {
        this.id = balance.getId();
        this.employeeId = balance.getEmployeeId();
        if (balance.getLeaveType() != null) {
            this.leaveType = new LeaveTypeResponse(balance.getLeaveType());
        }
        this.totalDays = balance.getTotalDays();
        this.usedDays = balance.getUsedDays();
        this.pendingDays = balance.getPendingDays();
        this.year = balance.getYear();
    }

    public UUID getId() { return id; }
    public UUID getEmployeeId() { return employeeId; }
    public LeaveTypeResponse getLeaveType() { return leaveType; }
    public Double getTotalDays() { return totalDays; }
    public Double getUsedDays() { return usedDays; }
    public Double getPendingDays() { return pendingDays; }
    public Integer getYear() { return year; }
}
