package com.nexushr.payroll.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tax_configurations")
public class TaxConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tax_bracket_name", nullable = false, length = 100)
    private String taxBracketName;

    @Column(name = "min_income", nullable = false)
    private Double minIncome;

    @Column(name = "max_income")
    private Double maxIncome; // null for highest bracket

    @Column(name = "tax_percentage", nullable = false)
    private Double taxPercentage;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public TaxConfiguration() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTaxBracketName() { return taxBracketName; }
    public void setTaxBracketName(String taxBracketName) { this.taxBracketName = taxBracketName; }

    public Double getMinIncome() { return minIncome; }
    public void setMinIncome(Double minIncome) { this.minIncome = minIncome; }

    public Double getMaxIncome() { return maxIncome; }
    public void setMaxIncome(Double maxIncome) { this.maxIncome = maxIncome; }

    public Double getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(Double taxPercentage) { this.taxPercentage = taxPercentage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
