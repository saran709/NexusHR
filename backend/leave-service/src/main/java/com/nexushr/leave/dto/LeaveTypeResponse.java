package com.nexushr.leave.dto;

import com.nexushr.leave.entity.LeaveType;
import java.util.UUID;

public class LeaveTypeResponse {

    private UUID id;
    private String name;
    private String description;
    private Integer maxDaysPerYear;
    private Boolean requiresApproval;

    public LeaveTypeResponse() {}

    public LeaveTypeResponse(LeaveType type) {
        this.id = type.getId();
        this.name = type.getName();
        this.description = type.getDescription();
        this.maxDaysPerYear = type.getMaxDaysPerYear();
        this.requiresApproval = type.getRequiresApproval();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getMaxDaysPerYear() { return maxDaysPerYear; }
    public Boolean getRequiresApproval() { return requiresApproval; }
}
