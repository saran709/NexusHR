package com.nexushr.attendance.provider;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class SimulatedAttendanceProvider implements AttendanceCaptureProvider {

    @Override
    public CaptureResult captureEvent(UUID employeeId, String eventType, String deviceId, String location) {
        // Simulates biometric fingerprint / facial recognition hardware capture
        LocalDateTime now = LocalDateTime.now();
        if (deviceId == null || deviceId.isBlank()) {
            deviceId = "BIO-DEV-SIM-01";
        }
        if (location == null || location.isBlank()) {
            location = "Main Corporate HQ Entrance";
        }
        return new CaptureResult(true, now, "Biometric capture successful via " + deviceId + " at " + location);
    }
}
