package com.nexushr.employee.service;

import com.nexushr.employee.dto.*;
import com.nexushr.employee.entity.*;
import com.nexushr.employee.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final EmployeeDocumentRepository documentRepository;
    private final OnboardingRepository onboardingRepository;
    private final OffboardingRepository offboardingRepository;
    private final DocumentStorageService storageService;
    private final AuditService auditService;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           DesignationRepository designationRepository,
                           EmployeeDocumentRepository documentRepository,
                           OnboardingRepository onboardingRepository,
                           OffboardingRepository offboardingRepository,
                           DocumentStorageService storageService,
                           AuditService auditService) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.designationRepository = designationRepository;
        this.documentRepository = documentRepository;
        this.onboardingRepository = onboardingRepository;
        this.offboardingRepository = offboardingRepository;
        this.storageService = storageService;
        this.auditService = auditService;
    }

    // Departments
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request, String userEmail, String ip) {
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Department code already exists");
        }
        Department dept = new Department();
        dept.setName(request.getName());
        dept.setCode(request.getCode());
        dept.setDescription(request.getDescription());
        Department saved = departmentRepository.save(dept);
        auditService.log(userEmail, "CREATE_DEPARTMENT", "Department", saved.getId(), "Created department " + saved.getName(), ip);
        return new DepartmentResponse(saved);
    }

    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream().map(DepartmentResponse::new).collect(Collectors.toList());
    }

    // Employees
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request, String userEmail, String ip) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Employee email already registered");
        }
        if (employeeRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID already exists");
        }

        Employee employee = new Employee();
        employee.setEmployeeId(request.getEmployeeId());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setEmploymentType(request.getEmploymentType());
        employee.setStatus(request.getStatus() != null ? request.getStatus() : "INVITED");
        employee.setAddress(request.getAddress());
        employee.setEmergencyContact(request.getEmergencyContact());
        employee.setProfilePhoto(request.getProfilePhoto());

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found"));
            employee.setDepartment(dept);
        }

        if (request.getDesignationId() != null) {
            Designation desig = designationRepository.findById(request.getDesignationId())
                    .orElseThrow(() -> new IllegalArgumentException("Designation not found"));
            employee.setDesignation(desig);
        }

        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("Manager not found"));
            employee.setManager(manager);
        }

        Employee saved = employeeRepository.save(employee);

        // Auto-create onboarding record
        Onboarding onboarding = new Onboarding();
        onboarding.setEmployee(saved);
        onboarding.setStatus("PENDING");
        onboarding.setChecklistJson("[{\"task\":\"Submit ID Proof\",\"completed\":false},{\"task\":\"Complete Tax Forms\",\"completed\":false}]");
        onboardingRepository.save(onboarding);

        auditService.log(userEmail, "CREATE_EMPLOYEE", "Employee", saved.getId(), "Created employee " + saved.getEmail(), ip);
        return new EmployeeResponse(saved);
    }

    public Page<EmployeeResponse> getEmployees(String search, UUID departmentId, String status, Pageable pageable) {
        return employeeRepository.searchEmployees(search, departmentId, status, pageable).map(EmployeeResponse::new);
    }

    public EmployeeResponse getEmployeeById(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));
        return new EmployeeResponse(employee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(UUID id, EmployeeRequest request, String userEmail, String ip) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setPhone(request.getPhone());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setEmploymentType(request.getEmploymentType());
        if (request.getStatus() != null && !request.getStatus().equals(employee.getStatus())) {
            auditService.log(userEmail, "STATUS_CHANGE", "Employee", employee.getId(), "Status changed from " + employee.getStatus() + " to " + request.getStatus(), ip);
            employee.setStatus(request.getStatus());
        }
        employee.setAddress(request.getAddress());
        employee.setEmergencyContact(request.getEmergencyContact());
        employee.setProfilePhoto(request.getProfilePhoto());

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found"));
            employee.setDepartment(dept);
        }

        if (request.getDesignationId() != null) {
            Designation desig = designationRepository.findById(request.getDesignationId())
                    .orElseThrow(() -> new IllegalArgumentException("Designation not found"));
            employee.setDesignation(desig);
        }

        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("Manager not found"));
            employee.setManager(manager);
        }

        Employee updated = employeeRepository.save(employee);
        auditService.log(userEmail, "UPDATE_EMPLOYEE", "Employee", updated.getId(), "Updated employee profile", ip);
        return new EmployeeResponse(updated);
    }

    @Transactional
    public void deleteEmployee(UUID id, String userEmail, String ip) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
        auditService.log(userEmail, "DELETE_EMPLOYEE", "Employee", id, "Deleted employee", ip);
    }

    // Documents
    @Transactional
    public DocumentResponse uploadDocument(UUID employeeId, String documentName, String documentType, MultipartFile file, String userEmail, String ip) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        String filePath = storageService.storeFile(file, employeeId);

        EmployeeDocument doc = new EmployeeDocument();
        doc.setEmployee(employee);
        doc.setDocumentName(documentName);
        doc.setDocumentType(documentType);
        doc.setMimeType(file.getContentType());
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1) : "";
        doc.setExtension(ext);
        doc.setFileSize(file.getSize());
        doc.setFilePath(filePath);

        EmployeeDocument saved = documentRepository.save(doc);
        auditService.log(userEmail, "UPLOAD_DOCUMENT", "EmployeeDocument", saved.getId(), "Uploaded document " + documentName + " for employee " + employeeId, ip);
        return new DocumentResponse(saved);
    }

    public List<DocumentResponse> getEmployeeDocuments(UUID employeeId, String userEmail, String ip) {
        auditService.log(userEmail, "ACCESS_DOCUMENTS", "Employee", employeeId, "Accessed document list for employee", ip);
        return documentRepository.findByEmployeeId(employeeId).stream().map(DocumentResponse::new).collect(Collectors.toList());
    }

    public InputStream downloadDocument(UUID documentId, String userEmail, String ip) {
        EmployeeDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));
        auditService.log(userEmail, "DOWNLOAD_DOCUMENT", "EmployeeDocument", documentId, "Downloaded document " + doc.getDocumentName(), ip);
        return storageService.loadFile(doc.getFilePath());
    }

    // Onboarding
    public OnboardingResponse getOnboarding(UUID employeeId) {
        Onboarding onboarding = onboardingRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Onboarding record not found"));
        return new OnboardingResponse(onboarding);
    }

    @Transactional
    public OnboardingResponse approveOnboarding(UUID employeeId, String remarks, UUID adminUserId, String userEmail, String ip) {
        Onboarding onboarding = onboardingRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Onboarding record not found"));

        onboarding.setStatus("COMPLETED");
        onboarding.setApprovedBy(adminUserId);
        onboarding.setApprovedAt(LocalDateTime.now());
        onboarding.setRemarks(remarks);

        Employee emp = onboarding.getEmployee();
        emp.setStatus("ACTIVE");
        employeeRepository.save(emp);

        Onboarding saved = onboardingRepository.save(onboarding);
        auditService.log(userEmail, "APPROVE_ONBOARDING", "Onboarding", saved.getId(), "Approved onboarding for employee " + employeeId, ip);
        return new OnboardingResponse(saved);
    }

    // Offboarding
    @Transactional
    public OffboardingResponse initiateOffboarding(UUID employeeId, String reason, LocalDate resignationDate, LocalDate lastWorkingDate, String userEmail, String ip) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        employee.setStatus("OFFBOARDING");
        employeeRepository.save(employee);

        Offboarding offboarding = new Offboarding();
        offboarding.setEmployee(employee);
        offboarding.setReason(reason);
        offboarding.setResignationDate(resignationDate);
        offboarding.setLastWorkingDate(lastWorkingDate);
        offboarding.setStatus("INITIATED");
        offboarding.setClearanceStatus("PENDING");

        Offboarding saved = offboardingRepository.save(offboarding);
        auditService.log(userEmail, "INITIATE_OFFBOARDING", "Offboarding", saved.getId(), "Initiated offboarding for employee " + employeeId, ip);
        return new OffboardingResponse(saved);
    }

    public OffboardingResponse getOffboarding(UUID employeeId) {
        Offboarding offboarding = offboardingRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Offboarding record not found"));
        return new OffboardingResponse(offboarding);
    }
}
