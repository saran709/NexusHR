package com.nexushr.attendance.repository;

import com.nexushr.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    Optional<Attendance> findByEmployeeIdAndAttendanceDate(UUID employeeId, LocalDate attendanceDate);

    Page<Attendance> findByEmployeeId(UUID employeeId, Pageable pageable);

    @Query("SELECT a FROM Attendance a WHERE " +
           "(:employeeId IS NULL OR a.employeeId = :employeeId) AND " +
           "(:startDate IS NULL OR a.attendanceDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.attendanceDate <= :endDate) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<Attendance> searchAttendance(@Param("employeeId") UUID employeeId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      @Param("status") String status,
                                      Pageable pageable);

    List<Attendance> findByAttendanceDateBetween(LocalDate startDate, LocalDate endDate);
    List<Attendance> findByEmployeeIdAndAttendanceDateBetween(UUID employeeId, LocalDate startDate, LocalDate endDate);
}
