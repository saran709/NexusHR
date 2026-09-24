package com.nexushr.leave.service;

import com.nexushr.leave.dto.LeaveRequestDto;
import com.nexushr.leave.dto.LeaveRequestResponse;
import com.nexushr.leave.entity.LeaveBalance;
import com.nexushr.leave.entity.LeaveRequest;
import com.nexushr.leave.entity.LeaveType;
import com.nexushr.leave.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LeaveServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private LeaveService leaveService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateLeaveRequestSuccess() {
        UUID employeeId = UUID.randomUUID();
        UUID leaveTypeId = UUID.randomUUID();

        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setEmployeeId(employeeId);
        dto.setLeaveTypeId(leaveTypeId);
        dto.setStartDate(LocalDate.now().plusDays(1));
        dto.setEndDate(LocalDate.now().plusDays(3));
        dto.setReason("Family vacation");

        LeaveType leaveType = new LeaveType();
        leaveType.setId(leaveTypeId);
        leaveType.setName("CASUAL");
        leaveType.setMaxDaysPerYear(12);

        when(leaveRequestRepository.findOverlappingRequests(any(), any(), any())).thenReturn(Collections.emptyList());
        when(leaveTypeRepository.findById(leaveTypeId)).thenReturn(Optional.of(leaveType));

        LeaveBalance balance = new LeaveBalance();
        balance.setEmployeeId(employeeId);
        balance.setLeaveType(leaveType);
        balance.setTotalDays(12.0);
        balance.setUsedDays(0.0);
        balance.setPendingDays(0.0);
        balance.setYear(LocalDate.now().getYear());

        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(any(), any(), any()))
                .thenReturn(Optional.of(balance));

        LeaveRequest saved = new LeaveRequest();
        saved.setId(UUID.randomUUID());
        saved.setEmployeeId(employeeId);
        saved.setLeaveType(leaveType);
        saved.setStartDate(dto.getStartDate());
        saved.setEndDate(dto.getEndDate());
        saved.setTotalDays(3.0);
        saved.setStatus("PENDING");

        when(leaveRequestRepository.save(any())).thenReturn(saved);

        LeaveRequestResponse response = leaveService.createLeaveRequest(dto, "emp@nexushr.com", "127.0.0.1");

        assertNotNull(response);
        assertEquals(3.0, response.getTotalDays());
        assertEquals("PENDING", response.getStatus());
        verify(leaveBalanceRepository, times(1)).save(any());
        verify(auditService, times(1)).log(any(), eq("CREATE_LEAVE_REQUEST"), any(), any(), any(), any());
    }

    @Test
    void testCreateLeaveRequestOverlappingThrowsException() {
        UUID employeeId = UUID.randomUUID();
        UUID leaveTypeId = UUID.randomUUID();

        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setEmployeeId(employeeId);
        dto.setLeaveTypeId(leaveTypeId);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(2));

        when(leaveRequestRepository.findOverlappingRequests(any(), any(), any()))
                .thenReturn(Collections.singletonList(new LeaveRequest()));

        assertThrows(IllegalStateException.class, () -> {
            leaveService.createLeaveRequest(dto, "emp@nexushr.com", "127.0.0.1");
        });
    }

    @Test
    void testApproveLeaveRequestSuccess() {
        UUID requestId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        UUID leaveTypeId = UUID.randomUUID();

        LeaveType leaveType = new LeaveType();
        leaveType.setId(leaveTypeId);
        leaveType.setName("CASUAL");

        LeaveRequest request = new LeaveRequest();
        request.setId(requestId);
        request.setEmployeeId(employeeId);
        request.setLeaveType(leaveType);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(1));
        request.setTotalDays(2.0);
        request.setStatus("PENDING");

        when(leaveRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        LeaveBalance balance = new LeaveBalance();
        balance.setEmployeeId(employeeId);
        balance.setLeaveType(leaveType);
        balance.setTotalDays(12.0);
        balance.setUsedDays(0.0);
        balance.setPendingDays(2.0);
        balance.setYear(LocalDate.now().getYear());

        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(any(), any(), any()))
                .thenReturn(Optional.of(balance));

        when(leaveRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeaveRequestResponse response = leaveService.approveLeaveRequest(requestId, UUID.randomUUID(), "Approved", "manager@nexushr.com", "127.0.0.1");

        assertNotNull(response);
        assertEquals("APPROVED", response.getStatus());
        assertEquals(2.0, balance.getUsedDays());
        assertEquals(0.0, balance.getPendingDays());
    }
}
