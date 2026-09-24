package com.nexushr.leave.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "leave_balances", indexes = {
    @Index(name = "idx_leave_balance_emp_type_year", columnList = "employee_id, leave_type_id, year", unique = true)
})
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "total_days", nullable = false)
    private Double totalDays = 0.0;

    @Column(name = "used_days", nullable = false)
    private Double usedDays = 0.0;

    @Column(name = "pending_days", nullable = false)
    private Double pendingDays = 0.0;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public LeaveBalance() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public Double getTotalDays() { return totalDays; }
    public void setTotalDays(Double totalDays) { this.totalDays = totalDays; }

    public Double getUsedDays() { return usedDays; }
    public void setUsedDays(Double usedDays) { this.usedDays = usedDays; }

    public Double getPendingDays() { return pendingDays; }
    public void setPendingDays(Double pendingDays) { this.pendingDays = pendingDays; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
