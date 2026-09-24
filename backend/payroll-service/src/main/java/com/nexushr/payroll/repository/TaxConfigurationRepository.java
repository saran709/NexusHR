package com.nexushr.payroll.repository;

import com.nexushr.payroll.entity.TaxConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaxConfigurationRepository extends JpaRepository<TaxConfiguration, UUID> {
}
