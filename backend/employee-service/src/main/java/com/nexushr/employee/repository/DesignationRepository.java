package com.nexushr.employee.repository;

import com.nexushr.employee.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, UUID> {
    Optional<Designation> findByCode(String code);
}
