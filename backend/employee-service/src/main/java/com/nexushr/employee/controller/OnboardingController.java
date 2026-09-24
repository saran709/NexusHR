package com.nexushr.employee.controller;

import com.nexushr.employee.dto.OnboardingResponse;
import com.nexushr.employee.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/onboarding")
public class OnboardingController {

    private final EmployeeService employeeService;

    public OnboardingController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<OnboardingResponse> getOnboarding(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(employeeService.getOnboarding(employeeId));
    }

    @PostMapping("/employee/{employeeId}/approve")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER')")
    public ResponseEntity<OnboardingResponse> approveOnboarding(@PathVariable UUID employeeId,
                                                                @RequestParam(required = false) String remarks,
                                                                Authentication authentication,
                                                                HttpServletRequest httpRequest) {
        String email = authentication.getName();
        String ip = getClientIp(httpRequest);
        // Assuming approvedBy UUID can be parsed or defaulted
        UUID adminUserId = UUID.randomUUID();
        OnboardingResponse response = employeeService.approveOnboarding(employeeId, remarks, adminUserId, email, ip);
        return ResponseEntity.ok(response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        return xf != null ? xf.split(",")[0] : request.getRemoteAddr();
    }
}
