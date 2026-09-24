package com.nexushr.employee.dto;

import com.nexushr.employee.entity.Employee;
import java.time.LocalDate;
import java.util.UUID;

public class EmployeeResponse {

    private UUID id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private LocalDate joiningDate;
    private DepartmentResponse department;
    private UUID managerId;
    private String managerName;
    private String employmentType;
    private String status;
    private String address;
    private String emergencyContact;
    private String profilePhoto;

    public EmployeeResponse() {}

    public EmployeeResponse(Employee employee) {
        this.id = employee.getId();
        this.employeeId = employee.getEmployeeId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.email = employee.getEmail();
        this.phone = employee.getPhone();
        this.dateOfBirth = employee.getDateOfBirth();
        this.joiningDate = employee.getJoiningDate();
        if (employee.getDepartment() != null) {
            this.department = new DepartmentResponse(employee.getDepartment());
        }
        if (employee.getManager() != null) {
            this.managerId = employee.getManager().getId();
            this.managerName = employee.getManager().getFirstName() + " " + employee.getManager().getLastName();
        }
        this.employmentType = employee.getEmploymentType();
        this.status = employee.getStatus();
        this.address = employee.getAddress();
        this.emergencyContact = employee.getEmergencyContact();
        this.profilePhoto = employee.getProfilePhoto();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }

    public DepartmentResponse getDepartment() { return department; }
    public void setDepartment(DepartmentResponse department) { this.department = department; }

    public UUID getManagerId() { return managerId; }
    public void setManagerId(UUID managerId) { this.managerId = managerId; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }
}
