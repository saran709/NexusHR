package com.nexushr.attendance.service;

import com.nexushr.attendance.dto.AttendanceResponse;
import com.nexushr.attendance.dto.CheckInRequest;
import com.nexushr.attendance.dto.CheckOutRequest;
import com.nexushr.attendance.entity.Attendance;
import com.nexushr.attendance.entity.AttendanceEvent;
import com.nexushr.attendance.provider.AttendanceCaptureProvider;
import com.nexushr.attendance.repository.AttendanceEventRepository;
import com.nexushr.attendance.repository.AttendanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceEventRepository eventRepository;
    private final AttendanceCaptureProvider captureProvider;
    private final AuditService auditService;
    private final RedisTemplate<String, Object> redisTemplate;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             AttendanceEventRepository eventRepository,
                             AttendanceCaptureProvider captureProvider,
                             AuditService auditService,
                             RedisTemplate<String, Object> redisTemplate) {
        this.attendanceRepository = attendanceRepository;
        this.eventRepository = eventRepository;
        this.captureProvider = captureProvider;
        this.auditService = auditService;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public AttendanceResponse checkIn(CheckInRequest request, String userEmail, String ip) {
        LocalDate today = LocalDate.now();

        // Prevent duplicate check-in
        attendanceRepository.findByEmployeeIdAndAttendanceDate(request.getEmployeeId(), today)
                .ifPresent(existing -> {
                    if (existing.getCheckInTime() != null) {
                        throw new IllegalStateException("Employee has already checked in for today.");
                    }
                });

        AttendanceCaptureProvider.CaptureResult captureResult = captureProvider.captureEvent(
                request.getEmployeeId(), "CHECK_IN", request.getDeviceId(), request.getLocation()
        );

        if (!captureResult.isSuccess()) {
            throw new IllegalArgumentException("Biometric attendance capture failed: " + captureResult.getMessage());
        }

        LocalDateTime checkInTime = captureResult.getTimestamp();

        // Record Attendance Event
        AttendanceEvent event = new AttendanceEvent(
                request.getEmployeeId(), "CHECK_IN", checkInTime,
                request.getSource() != null ? request.getSource() : "BIOMETRIC",
                request.getLocation(), request.getDeviceId()
        );
        eventRepository.save(event);

        Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(request.getEmployeeId(), today)
                .orElse(new Attendance());

        attendance.setEmployeeId(request.getEmployeeId());
        attendance.setAttendanceDate(today);
        attendance.setCheckInTime(checkInTime);
        attendance.setTimezone(request.getTimezone() != null ? request.getTimezone() : "UTC");

        // Late detection (e.g. standard start time 09:30 AM)
        LocalTime standardStart = LocalTime.of(9, 30);
        boolean isLate = checkInTime.toLocalTime().isAfter(standardStart);
        attendance.setIsLate(isLate);
        attendance.setStatus(isLate ? "LATE" : "PRESENT");

        Attendance saved = attendanceRepository.save(attendance);
        auditService.log(userEmail, "CHECK_IN", "Attendance", "Employee checked in successfully", ip);
        return new AttendanceResponse(saved);
    }

    @Transactional
    public AttendanceResponse checkOut(CheckOutRequest request, String userEmail, String ip) {
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(request.getEmployeeId(), today)
                .orElseThrow(() -> new IllegalArgumentException("No check-in record found for today. Check-out requires valid check-in."));

        if (attendance.getCheckInTime() == null) {
            throw new IllegalStateException("Check-out requires valid check-in.");
        }

        if (attendance.getCheckOutTime() != null) {
            throw new IllegalStateException("Employee has already checked out for today.");
        }

        AttendanceCaptureProvider.CaptureResult captureResult = captureProvider.captureEvent(
                request.getEmployeeId(), "CHECK_OUT", request.getDeviceId(), request.getLocation()
        );

        LocalDateTime checkOutTime = captureResult.getTimestamp();

        if (checkOutTime.isBefore(attendance.getCheckInTime())) {
            throw new IllegalArgumentException("Invalid timestamp: Check-out time cannot be before check-in time.");
        }

        AttendanceEvent event = new AttendanceEvent(
                request.getEmployeeId(), "CHECK_OUT", checkOutTime,
                request.getSource() != null ? request.getSource() : "BIOMETRIC",
                request.getLocation(), request.getDeviceId()
        );
        eventRepository.save(event);

        attendance.setCheckOutTime(checkOutTime);

        // Calculate working hours & overtime
        Duration duration = Duration.between(attendance.getCheckInTime(), checkOutTime);
        double workingHours = duration.toMinutes() / 60.0;
        attendance.setTotalWorkingHours(Math.round(workingHours * 100.0) / 100.0);

        // Standard 8 hours
        if (workingHours > 8.0) {
            attendance.setOvertimeHours(Math.round((workingHours - 8.0) * 100.0) / 100.0);
        } else {
            attendance.setOvertimeHours(0.0);
        }

        LocalTime standardEnd = LocalTime.of(17, 30);
        boolean earlyDeparture = checkOutTime.toLocalTime().isBefore(standardEnd);
        attendance.setIsEarlyDeparture(earlyDeparture);

        Attendance saved = attendanceRepository.save(attendance);
        auditService.log(userEmail, "CHECK_OUT", "Attendance", "Employee checked out successfully", ip);
        return new AttendanceResponse(saved);
    }

    public Page<Attendance> getAttendanceHistory(UUID employeeId, LocalDate startDate, LocalDate endDate, String status, Pageable pageable) {
        return attendanceRepository.searchAttendance(employeeId, startDate, endDate, status, pageable);
    }

    public List<AttendanceResponse> getTeamAttendance(LocalDate date) {
        if (date == null) date = LocalDate.now();
        return attendanceRepository.findByAttendanceDateBetween(date, date)
                .stream().map(AttendanceResponse::new).collect(Collectors.toList());
    }

    public Object getDashboardSummary(LocalDate date) {
        if (date == null) date = LocalDate.now();
        List<Attendance> records = attendanceRepository.findByAttendanceDateBetween(date, date);
        long present = records.stream().filter(r -> "PRESENT".equals(r.getStatus()) || "LATE".equals(r.getStatus())).count();
        long late = records.stream().filter(r -> Boolean.TRUE.equals(r.getIsLate())).count();
        long absent = records.stream().filter(r -> "ABSENT".equals(r.getStatus())).count();

        return java.util.Map.of(
                "date", date,
                "totalPresent", present,
                "totalLate", late,
                "totalAbsent", absent,
                "recordsCount", records.size()
        );
    }
}
