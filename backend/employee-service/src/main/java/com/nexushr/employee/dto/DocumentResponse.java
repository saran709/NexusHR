package com.nexushr.employee.dto;

import com.nexushr.employee.entity.EmployeeDocument;
import java.time.LocalDateTime;
import java.util.UUID;

public class DocumentResponse {

    private UUID id;
    private UUID employeeId;
    private String documentName;
    private String documentType;
    private String mimeType;
    private String extension;
    private Long fileSize;
    private LocalDateTime createdAt;

    public DocumentResponse() {}

    public DocumentResponse(EmployeeDocument document) {
        this.id = document.getId();
        this.employeeId = document.getEmployee().getId();
        this.documentName = document.getDocumentName();
        this.documentType = document.getDocumentType();
        this.mimeType = document.getMimeType();
        this.extension = document.getExtension();
        this.fileSize = document.getFileSize();
        this.createdAt = document.getCreatedAt();
    }

    public UUID getId() { return id; }
    public UUID getEmployeeId() { return employeeId; }
    public String getDocumentName() { return documentName; }
    public String getDocumentType() { return documentType; }
    public String getMimeType() { return mimeType; }
    public String getExtension() { return extension; }
    public Long getFileSize() { return fileSize; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
