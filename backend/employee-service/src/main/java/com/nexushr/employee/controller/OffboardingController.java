package com.nexushr.employee.controller;

import com.nexushr.employee.dto.OffboardingResponse;
import com.nexushr.employee.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/offboarding")
public class OffboardingController {

    private final EmployeeService employeeService;

    public OffboardingController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER')")
    public ResponseEntity<OffboardingResponse> initiateOffboarding(
            @PathVariable UUID employeeId,
            @RequestParam String reason,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate resignationDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastWorkingDate,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        String email = authentication.getName();
        String ip = getClientIp(httpRequest);
        OffboardingResponse response = employeeService.initiateOffboarding(employeeId, reason, resignationDate, lastWorkingDate, email, ip);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<OffboardingResponse> getOffboarding(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(employeeService.getOffboarding(employeeId));
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        return xf != null ? xf.split(",")[0] : request.getRemoteAddr();
    }
}
