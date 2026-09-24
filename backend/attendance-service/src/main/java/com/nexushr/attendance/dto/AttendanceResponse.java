package com.nexushr.attendance.dto;

import com.nexushr.attendance.entity.Attendance;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class AttendanceResponse {

    private UUID id;
    private UUID employeeId;
    private LocalDate attendanceDate;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Double totalWorkingHours;
    private String status;
    private Boolean isLate;
    private Boolean isEarlyDeparture;
    private Double overtimeHours;
    private String timezone;
    private String remarks;

    public AttendanceResponse() {}

    public AttendanceResponse(Attendance attendance) {
        this.id = attendance.getId();
        this.employeeId = attendance.getEmployeeId();
        this.attendanceDate = attendance.getAttendanceDate();
        this.checkInTime = attendance.getCheckInTime();
        this.checkOutTime = attendance.getCheckOutTime();
        this.totalWorkingHours = attendance.getTotalWorkingHours();
        this.status = attendance.getStatus();
        this.isLate = attendance.getIsLate();
        this.isEarlyDeparture = attendance.getIsEarlyDeparture();
        this.overtimeHours = attendance.getOvertimeHours();
        this.timezone = attendance.getTimezone();
        this.remarks = attendance.getRemarks();
    }

    public UUID getId() { return id; }
    public UUID getEmployeeId() { return employeeId; }
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public LocalDateTime getCheckInTime() { return checkInTime; }
    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public Double getTotalWorkingHours() { return totalWorkingHours; }
    public String getStatus() { return status; }
    public Boolean getIsLate() { return isLate; }
    public Boolean getIsEarlyDeparture() { return isEarlyDeparture; }
    public Double getOvertimeHours() { return overtimeHours; }
    public String getTimezone() { return timezone; }
    public String getRemarks() { return remarks; }
}
