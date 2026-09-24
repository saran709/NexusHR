package com.nexushr.leave.repository;

import com.nexushr.leave.entity.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {
    Page<LeaveRequest> findByEmployeeId(UUID employeeId, Pageable pageable);

    @Query("SELECT r FROM LeaveRequest r WHERE r.employeeId = :employeeId AND r.status IN ('PENDING', 'APPROVED') AND " +
           "((r.startDate <= :endDate) AND (r.endDate >= :startDate))")
    List<LeaveRequest> findOverlappingRequests(@Param("employeeId") UUID employeeId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    List<LeaveRequest> findByStartDateBetweenOrEndDateBetween(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2);
}
