package com.nexushr.payroll.controller;

import com.nexushr.payroll.dto.*;
import com.nexushr.payroll.service.PayrollService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/runs")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'PAYROLL_ADMIN')")
    public ResponseEntity<PayrollResponseDto> createPayrollRun(@Valid @RequestBody PayrollRunDto dto,
                                                               Authentication authentication,
                                                               HttpServletRequest httpRequest) {
        String email = authentication.getName();
        String ip = getClientIp(httpRequest);
        PayrollResponseDto response = payrollService.createPayrollRun(dto, email, ip);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/runs")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'PAYROLL_ADMIN')")
    public ResponseEntity<Page<PayrollResponseDto>> getAllPayrollRuns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "periodStart"));
        return ResponseEntity.ok(payrollService.getAllPayrollRuns(pageable));
    }

    @GetMapping("/runs/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'PAYROLL_ADMIN')")
    public ResponseEntity<PayrollResponseDto> getPayrollRunById(@PathVariable UUID id) {
        return ResponseEntity.ok(payrollService.getPayrollRunById(id));
    }

    @PostMapping("/runs/{id}/process")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'PAYROLL_ADMIN')")
    public ResponseEntity<PayrollResponseDto> processPayrollRun(@PathVariable UUID id,
                                                               Authentication authentication,
                                                               HttpServletRequest httpRequest) {
        String email = authentication.getName();
        String ip = getClientIp(httpRequest);
        PayrollResponseDto response = payrollService.processPayrollRun(id, email, ip);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/runs/{id}/lock")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'PAYROLL_ADMIN')")
    public ResponseEntity<PayrollResponseDto> lockPayrollRun(@PathVariable UUID id,
                                                             Authentication authentication,
                                                             HttpServletRequest httpRequest) {
        String email = authentication.getName();
        String ip = getClientIp(httpRequest);
        PayrollResponseDto response = payrollService.lockPayrollRun(id, email, ip);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'PAYROLL_ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<PayslipResponseDto>> getMyPayroll(Authentication authentication) {
        // Map authentication email to employee payslips or use a resolved UUID
        UUID dummyEmployeeId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        return ResponseEntity.ok(payrollService.getEmployeePayslips(dummyEmployeeId));
    }

    @GetMapping("/me/payslips")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'PAYROLL_ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<PayslipResponseDto>> getMyPayslips(Authentication authentication) {
        UUID dummyEmployeeId = UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        return ResponseEntity.ok(payrollService.getEmployeePayslips(dummyEmployeeId));
    }

    @GetMapping("/{employeeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'PAYROLL_ADMIN')")
    public ResponseEntity<List<PayslipResponseDto>> getEmployeePayroll(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(payrollService.getEmployeePayslips(employeeId));
    }

    @GetMapping("/{id}/payslip")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'PAYROLL_ADMIN', 'EMPLOYEE')")
    public ResponseEntity<PayslipResponseDto> getPayslipById(@PathVariable UUID id) {
        return ResponseEntity.ok(payrollService.getPayslipById(id));
    }

    @GetMapping("/runs/{id}/export/csv")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'PAYROLL_ADMIN')")
    public ResponseEntity<String> exportCsv(@PathVariable UUID id) {
        String csv = payrollService.exportPayrollCsv(id);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payroll_run_" + id + ".csv");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.TEXT_PLAIN).body(csv);
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        return xf != null ? xf.split(",")[0] : request.getRemoteAddr();
    }
}
