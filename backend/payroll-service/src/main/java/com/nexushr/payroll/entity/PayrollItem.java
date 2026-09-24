package com.nexushr.payroll.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "payroll_items")
public class PayrollItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_record_id", nullable = false)
    private PayrollRecord payrollRecord;

    @Column(name = "component_name", nullable = false, length = 50)
    private String componentName;

    @Column(nullable = false, length = 20)
    private String type; // EARNING or DEDUCTION

    @Column(nullable = false)
    private Double amount;

    public PayrollItem() {}

    public PayrollItem(PayrollRecord payrollRecord, String componentName, String type, Double amount) {
        this.payrollRecord = payrollRecord;
        this.componentName = componentName;
        this.type = type;
        this.amount = amount;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public PayrollRecord getPayrollRecord() { return payrollRecord; }
    public void setPayrollRecord(PayrollRecord payrollRecord) { this.payrollRecord = payrollRecord; }

    public String getComponentName() { return componentName; }
    public void setComponentName(String componentName) { this.componentName = componentName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
