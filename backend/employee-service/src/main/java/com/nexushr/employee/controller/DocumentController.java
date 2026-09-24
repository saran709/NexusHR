package com.nexushr.employee.controller;

import com.nexushr.employee.dto.DocumentResponse;
import com.nexushr.employee.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final EmployeeService employeeService;

    public DocumentController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable UUID employeeId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentName") String documentName,
            @RequestParam("documentType") String documentType,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        String email = authentication.getName();
        String ip = getClientIp(httpRequest);
        DocumentResponse response = employeeService.uploadDocument(employeeId, documentName, documentType, file, email, ip);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<DocumentResponse>> getEmployeeDocuments(
            @PathVariable UUID employeeId,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        String email = authentication.getName();
        String ip = getClientIp(httpRequest);
        return ResponseEntity.ok(employeeService.getEmployeeDocuments(employeeId, email, ip));
    }

    @GetMapping("/{documentId}/download")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'HR_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<InputStreamResource> downloadDocument(
            @PathVariable UUID documentId,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        String email = authentication.getName();
        String ip = getClientIp(httpRequest);
        InputStream stream = employeeService.downloadDocument(documentId, email, ip);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document_" + documentId + "\"")
                .body(new InputStreamResource(stream));
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        return xf != null ? xf.split(",")[0] : request.getRemoteAddr();
    }
}
