package com.nexushr.leave.service;

import com.nexushr.leave.dto.*;
import com.nexushr.leave.entity.*;
import com.nexushr.leave.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final HolidayRepository holidayRepository;
    private final AuditService auditService;

    public LeaveService(LeaveRequestRepository leaveRequestRepository,
                        LeaveBalanceRepository leaveBalanceRepository,
                        LeaveTypeRepository leaveTypeRepository,
                        HolidayRepository holidayRepository,
                        AuditService auditService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.holidayRepository = holidayRepository;
        this.auditService = auditService;
    }

    public List<LeaveTypeResponse> getAllLeaveTypes() {
        return leaveTypeRepository.findAll().stream().map(LeaveTypeResponse::new).collect(Collectors.toList());
    }

    public List<LeaveBalanceResponse> getLeaveBalance(UUID employeeId, Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        return leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, year)
                .stream().map(LeaveBalanceResponse::new).collect(Collectors.toList());
    }

    public List<HolidayResponse> getCalendar(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().withDayOfYear(1);
        if (endDate == null) endDate = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        return holidayRepository.findByHolidayDateBetween(startDate, endDate)
                .stream().map(HolidayResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public LeaveRequestResponse createLeaveRequest(LeaveRequestDto dto, String userEmail, String ip) {
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }

        // Overlapping check
        List<LeaveRequest> overlapping = leaveRequestRepository.findOverlappingRequests(
                dto.getEmployeeId(), dto.getStartDate(), dto.getEndDate()
        );
        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("Cannot request overlapping leave during this period.");
        }

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Leave type not found"));

        double totalDays = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1.0;

        int year = dto.getStartDate().getYear();
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                dto.getEmployeeId(), leaveType.getId(), year
        ).orElseGet(() -> {
            LeaveBalance newBal = new LeaveBalance();
            newBal.setEmployeeId(dto.getEmployeeId());
            newBal.setLeaveType(leaveType);
            newBal.setYear(year);
            newBal.setTotalDays(leaveType.getMaxDaysPerYear() != null ? leaveType.getMaxDaysPerYear().doubleValue() : 20.0);
            return leaveBalanceRepository.save(newBal);
        });

        // Balance validation (except for unpaid or if exceeded policy)
        if (!"UNPAID".equals(leaveType.getName())) {
            double available = balance.getTotalDays() - balance.getUsedDays() - balance.getPendingDays();
            if (totalDays > available) {
                throw new IllegalArgumentException("Insufficient leave balance. Requested: " + totalDays + ", Available: " + available);
            }
        }

        balance.setPendingDays(balance.getPendingDays() + totalDays);
        leaveBalanceRepository.save(balance);

        LeaveRequest request = new LeaveRequest();
        request.setEmployeeId(dto.getEmployeeId());
        request.setLeaveType(leaveType);
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setTotalDays(totalDays);
        request.setReason(dto.getReason());
        request.setStatus("PENDING");

        LeaveRequest saved = leaveRequestRepository.save(request);
        auditService.log(userEmail, "CREATE_LEAVE_REQUEST", "LeaveRequest", saved.getId(), "Requested " + totalDays + " days of " + leaveType.getName(), ip);
        return new LeaveRequestResponse(saved);
    }

    public Page<LeaveRequestResponse> getLeaveRequests(UUID employeeId, Pageable pageable) {
        if (employeeId != null) {
            return leaveRequestRepository.findByEmployeeId(employeeId, pageable).map(LeaveRequestResponse::new);
        }
        return leaveRequestRepository.findAll(pageable).map(LeaveRequestResponse::new);
    }

    public LeaveRequestResponse getLeaveRequestById(UUID id) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));
        return new LeaveRequestResponse(request);
    }

    @Transactional
    public LeaveRequestResponse approveLeaveRequest(UUID id, UUID approverId, String remarks, String userEmail, String ip) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new IllegalStateException("Leave request is already processed.");
        }

        request.setStatus("APPROVED");
        request.setApprovedBy(approverId);
        request.setApprovedAt(LocalDateTime.now());
        request.setRemarks(remarks);

        // Update balance transactionally
        int year = request.getStartDate().getYear();
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                request.getEmployeeId(), request.getLeaveType().getId(), year
        ).orElseThrow(() -> new IllegalStateException("Leave balance record not found"));

        balance.setPendingDays(balance.getPendingDays() - request.getTotalDays());
        balance.setUsedDays(balance.getUsedDays() + request.getTotalDays());
        leaveBalanceRepository.save(balance);

        LeaveRequest saved = leaveRequestRepository.save(request);
        auditService.log(userEmail, "APPROVE_LEAVE", "LeaveRequest", saved.getId(), "Approved leave request for employee " + request.getEmployeeId(), ip);
        return new LeaveRequestResponse(saved);
    }

    @Transactional
    public LeaveRequestResponse rejectLeaveRequest(UUID id, UUID approverId, String remarks, String userEmail, String ip) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new IllegalStateException("Leave request is already processed.");
        }

        request.setStatus("REJECTED");
        request.setApprovedBy(approverId);
        request.setApprovedAt(LocalDateTime.now());
        request.setRemarks(remarks);

        int year = request.getStartDate().getYear();
        leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                request.getEmployeeId(), request.getLeaveType().getId(), year
        ).ifPresent(balance -> {
            balance.setPendingDays(balance.getPendingDays() - request.getTotalDays());
            leaveBalanceRepository.save(balance);
        });

        LeaveRequest saved = leaveRequestRepository.save(request);
        auditService.log(userEmail, "REJECT_LEAVE", "LeaveRequest", saved.getId(), "Rejected leave request for employee " + request.getEmployeeId(), ip);
        return new LeaveRequestResponse(saved);
    }

    @Transactional
    public LeaveRequestResponse cancelLeaveRequest(UUID id, String userEmail, String ip) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));

        String oldStatus = request.getStatus();
        if ("CANCELLED".equals(oldStatus) || "REJECTED".equals(oldStatus)) {
            throw new IllegalStateException("Leave request cannot be cancelled.");
        }

        request.setStatus("CANCELLED");

        int year = request.getStartDate().getYear();
        leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                request.getEmployeeId(), request.getLeaveType().getId(), year
        ).ifPresent(balance -> {
            if ("APPROVED".equals(oldStatus)) {
                balance.setUsedDays(balance.getUsedDays() - request.getTotalDays());
            } else if ("PENDING".equals(oldStatus)) {
                balance.setPendingDays(balance.getPendingDays() - request.getTotalDays());
            }
            leaveBalanceRepository.save(balance);
        });

        LeaveRequest saved = leaveRequestRepository.save(request);
        auditService.log(userEmail, "CANCEL_LEAVE", "LeaveRequest", saved.getId(), "Cancelled leave request", ip);
        return new LeaveRequestResponse(saved);
    }
}
