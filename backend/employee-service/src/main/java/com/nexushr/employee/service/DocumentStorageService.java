package com.nexushr.employee.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.UUID;

public interface DocumentStorageService {
    String storeFile(MultipartFile file, UUID employeeId);
    InputStream loadFile(String filePath);
    void deleteFile(String filePath);
}
