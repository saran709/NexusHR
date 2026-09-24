package com.nexushr.leave.controller;

import com.nexushr.leave.dto.*;
import com.nexushr.leave.service.LeaveService;
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
@RequestMapping("/api/leave")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping("/types")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<LeaveTypeResponse>> getLeaveTypes() {
        return ResponseEntity.ok(leaveService.getAllLeaveTypes());
    }

    @GetMapping("/balance")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<LeaveBalanceResponse>> getLeaveBalance(
            @RequestParam UUID employeeId,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(leaveService.getLeaveBalance(employeeId, year));
    }

    @GetMapping("/calendar")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<HolidayResponse>> getCalendar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(leaveService.getCalendar(startDate, endDate));
    }

    @PostMapping("/requests")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<LeaveRequestResponse> createRequest(@Valid @RequestBody LeaveRequestDto dto,
                                                              Authentication authentication,
                                                              HttpServletRequest httpRequest) {
        String email = authentication.getName();
        String ip = getClientIp(httpRequest);
        LeaveRequestResponse response = leaveService.createLeaveRequest(dto, email, ip);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/requests")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Page<LeaveRequestResponse>> getRequests(
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<LeaveRequestResponse> requests = leaveService.getLeaveRequests(employeeId, pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/requests/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<LeaveRequestResponse> getRequestById(@PathVariable UUID id) {
        return ResponseEntity.ok(leaveService.getLeaveRequestById(id));
    }

    @PostMapping("/requests/{id}/approve")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER')")
    public ResponseEntity<LeaveRequestResponse> approveRequest(@PathVariable UUID id,
                                                              @RequestParam(required = false) String remarks,
                                                              Authentication authentication,
                                                              HttpServletRequest httpRequest) {
        String email = authentication.getName();
        String ip = getClientIp(httpRequest);
        UUID approverId = UUID.randomUUID(); // Mocked or resolved approver ID
        LeaveRequestResponse response = leaveService.approveLeaveRequest(id, approverId, remarks, email, ip);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/requests/{id}/reject")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER')")
    public ResponseEntity<LeaveRequestResponse> rejectRequest(@PathVariable UUID id,
                                                             @RequestParam(required = false) String remarks,
                                                             Authentication authentication,
                                                             HttpServletRequest httpRequest) {
        String email = authentication.getName();
        String ip = getClientIp(httpRequest);
        UUID approverId = UUID.randomUUID();
        LeaveRequestResponse response = leaveService.rejectLeaveRequest(id, approverId, remarks, email, ip);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/requests/{id}/cancel")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<LeaveRequestResponse> cancelRequest(@PathVariable UUID id,
                                                             Authentication authentication,
                                                             HttpServletRequest httpRequest) {
        String email = authentication.getName();
        String ip = getClientIp(httpRequest);
        LeaveRequestResponse response = leaveService.cancelLeaveRequest(id, email, ip);
        return ResponseEntity.ok(response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        return xf != null ? xf.split(",")[0] : request.getRemoteAddr();
    }
}
