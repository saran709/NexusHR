package com.nexushr.employee.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "offboarding")
public class Offboarding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    @Column(nullable = false, length = 255)
    private String reason;

    private LocalDate resignationDate;

    private LocalDate lastWorkingDate;

    @Column(nullable = false, length = 30)
    private String clearanceStatus; // PENDING, APPROVED, REJECTED

    @Column(nullable = false, length = 30)
    private String status; // INITIATED, IN_PROGRESS, COMPLETED

    @Column(length = 500)
    private String remarks;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "INITIATED";
        }
        if (clearanceStatus == null) {
            clearanceStatus = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Offboarding() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDate getResignationDate() { return resignationDate; }
    public void setResignationDate(LocalDate resignationDate) { this.resignationDate = resignationDate; }

    public LocalDate getLastWorkingDate() { return lastWorkingDate; }
    public void setLastWorkingDate(LocalDate lastWorkingDate) { this.lastWorkingDate = lastWorkingDate; }

    public String getClearanceStatus() { return clearanceStatus; }
    public void setClearanceStatus(String clearanceStatus) { this.clearanceStatus = clearanceStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
