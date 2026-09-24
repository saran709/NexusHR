package com.nexushr.employee.service;

import com.nexushr.employee.dto.EmployeeRequest;
import com.nexushr.employee.dto.EmployeeResponse;
import com.nexushr.employee.entity.Department;
import com.nexushr.employee.entity.Employee;
import com.nexushr.employee.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DesignationRepository designationRepository;

    @Mock
    private EmployeeDocumentRepository documentRepository;

    @Mock
    private OnboardingRepository onboardingRepository;

    @Mock
    private OffboardingRepository offboardingRepository;

    @Mock
    private DocumentStorageService storageService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateEmployeeSuccess() {
        EmployeeRequest request = new EmployeeRequest();
        request.setEmployeeId("EMP001");
        request.setFirstName("Alice");
        request.setLastName("Smith");
        request.setEmail("alice@nexushr.com");

        when(employeeRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(employeeRepository.existsByEmployeeId(request.getEmployeeId())).thenReturn(false);

        Employee saved = new Employee();
        saved.setId(UUID.randomUUID());
        saved.setEmployeeId("EMP001");
        saved.setFirstName("Alice");
        saved.setLastName("Smith");
        saved.setEmail("alice@nexushr.com");
        saved.setStatus("INVITED");

        when(employeeRepository.save(any(Employee.class))).thenReturn(saved);

        EmployeeResponse response = employeeService.createEmployee(request, "admin@nexushr.com", "127.0.0.1");

        assertNotNull(response);
        assertEquals("EMP001", response.getEmployeeId());
        assertEquals("alice@nexushr.com", response.getEmail());
        verify(onboardingRepository, times(1)).save(any());
        verify(auditService, times(1)).log(any(), eq("CREATE_EMPLOYEE"), any(), any(), any(), any());
    }

    @Test
    void testCreateEmployeeDuplicateEmail() {
        EmployeeRequest request = new EmployeeRequest();
        request.setEmail("alice@nexushr.com");

        when(employeeRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            employeeService.createEmployee(request, "admin@nexushr.com", "127.0.0.1");
        });
    }

    @Test
    void testUploadDocumentValidationSuccess() {
        UUID employeeId = UUID.randomUUID();
        Employee employee = new Employee();
        employee.setId(employeeId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        when(storageService.storeFile(any(), eq(employeeId))).thenReturn("uploads/documents/resume.pdf");

        com.nexushr.employee.entity.EmployeeDocument doc = new com.nexushr.employee.entity.EmployeeDocument();
        doc.setId(UUID.randomUUID());
        doc.setDocumentName("Resume");
        doc.setDocumentType("RESUME");
        doc.setMimeType("application/pdf");
        doc.setExtension("pdf");
        doc.setFileSize(10L);
        doc.setFilePath("path");
        doc.setEmployee(employee);

        when(documentRepository.save(any())).thenReturn(doc);

        var response = employeeService.uploadDocument(employeeId, "Resume", "RESUME", file, "admin@nexushr.com", "127.0.0.1");

        assertNotNull(response);
        assertEquals("Resume", response.getDocumentName());
        assertEquals("application/pdf", response.getMimeType());
    }
}
