package com.nexushr.attendance.controller;

import com.nexushr.attendance.dto.AttendanceResponse;
import com.nexushr.attendance.dto.CheckInRequest;
import com.nexushr.attendance.dto.CheckOutRequest;
import com.nexushr.attendance.entity.Attendance;
import com.nexushr.attendance.service.AttendanceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/check-in")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<AttendanceResponse> checkIn(@Valid @RequestBody CheckInRequest request,
                                                    Authentication authentication,
                                                    HttpServletRequest httpRequest) {
        String email = authentication.getName();
        String ip = getClientIp(httpRequest);
        AttendanceResponse response = attendanceService.checkIn(request, email, ip);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/check-out")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<AttendanceResponse> checkOut(@Valid @RequestBody CheckOutRequest request,
                                                     Authentication authentication,
                                                     HttpServletRequest httpRequest) {
        String email = authentication.getName();
        String ip = getClientIp(httpRequest);
        AttendanceResponse response = attendanceService.checkOut(request, email, ip);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Page<AttendanceResponse>> getMyAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        // For demonstration, passing a dummy or extracted employee UUID
        UUID dummyEmployeeId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "attendanceDate"));
        Page<Attendance> history = attendanceService.getAttendanceHistory(dummyEmployeeId, startDate, endDate, status, pageable);
        return ResponseEntity.ok(history.map(AttendanceResponse::new));
    }

    @GetMapping("/employee/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER')")
    public ResponseEntity<Page<AttendanceResponse>> getEmployeeAttendance(
            @PathVariable UUID id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "attendanceDate"));
        Page<Attendance> history = attendanceService.getAttendanceHistory(id, startDate, endDate, status, pageable);
        return ResponseEntity.ok(history.map(AttendanceResponse::new));
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER')")
    public ResponseEntity<List<AttendanceResponse>> getTeamAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getTeamAttendance(date));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER')")
    public ResponseEntity<Object> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getDashboardSummary(date));
    }

    @GetMapping("/report")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER')")
    public ResponseEntity<Page<AttendanceResponse>> getReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "attendanceDate"));
        Page<Attendance> report = attendanceService.getAttendanceHistory(null, startDate, endDate, status, pageable);
        return ResponseEntity.ok(report.map(AttendanceResponse::new));
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        return xf != null ? xf.split(",")[0] : request.getRemoteAddr();
    }
}
