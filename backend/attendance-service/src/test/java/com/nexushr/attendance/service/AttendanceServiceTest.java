package com.nexushr.attendance.service;

import com.nexushr.attendance.dto.AttendanceResponse;
import com.nexushr.attendance.dto.CheckInRequest;
import com.nexushr.attendance.dto.CheckOutRequest;
import com.nexushr.attendance.entity.Attendance;
import com.nexushr.attendance.provider.AttendanceCaptureProvider;
import com.nexushr.attendance.repository.AttendanceEventRepository;
import com.nexushr.attendance.repository.AttendanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private AttendanceEventRepository eventRepository;

    @Mock
    private AttendanceCaptureProvider captureProvider;

    @Mock
    private AuditService auditService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private AttendanceService attendanceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckInSuccess() {
        UUID employeeId = UUID.randomUUID();
        CheckInRequest request = new CheckInRequest();
        request.setEmployeeId(employeeId);
        request.setDeviceId("BIO-01");
        request.setLocation("HQ");

        when(attendanceRepository.findByEmployeeIdAndAttendanceDate(eq(employeeId), any())).thenReturn(Optional.empty());

        LocalDateTime now = LocalDateTime.now();
        when(captureProvider.captureEvent(eq(employeeId), eq("CHECK_IN"), any(), any()))
                .thenReturn(new AttendanceCaptureProvider.CaptureResult(true, now, "Success"));

        Attendance saved = new Attendance();
        saved.setId(UUID.randomUUID());
        saved.setEmployeeId(employeeId);
        saved.setAttendanceDate(LocalDate.now());
        saved.setCheckInTime(now);
        saved.setStatus("PRESENT");

        when(attendanceRepository.save(any())).thenReturn(saved);

        AttendanceResponse response = attendanceService.checkIn(request, "emp@nexushr.com", "127.0.0.1");

        assertNotNull(response);
        assertEquals(employeeId, response.getEmployeeId());
        verify(eventRepository, times(1)).save(any());
    }

    @Test
    void testDuplicateCheckInThrowsException() {
        UUID employeeId = UUID.randomUUID();
        CheckInRequest request = new CheckInRequest();
        request.setEmployeeId(employeeId);

        Attendance existing = new Attendance();
        existing.setCheckInTime(LocalDateTime.now().minusHours(1));

        when(attendanceRepository.findByEmployeeIdAndAttendanceDate(eq(employeeId), any()))
                .thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class, () -> {
            attendanceService.checkIn(request, "emp@nexushr.com", "127.0.0.1");
        });
    }

    @Test
    void testCheckOutWithoutCheckInThrowsException() {
        UUID employeeId = UUID.randomUUID();
        CheckOutRequest request = new CheckOutRequest();
        request.setEmployeeId(employeeId);

        when(attendanceRepository.findByEmployeeIdAndAttendanceDate(eq(employeeId), any()))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            attendanceService.checkOut(request, "emp@nexushr.com", "127.0.0.1");
        });
    }
}
