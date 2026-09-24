package com.nexushr.attendance.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CheckInRequest {

    @NotNull(message = "Employee ID is required")
    private UUID employeeId;

    private String deviceId;
    private String location;
    private String source; // BIOMETRIC, WEB, MOBILE
    private String timezone;

    public CheckInRequest() {}

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
}
