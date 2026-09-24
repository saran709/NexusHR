package com.nexushr.attendance.provider;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AttendanceCaptureProvider {
    CaptureResult captureEvent(UUID employeeId, String eventType, String deviceId, String location);

    class CaptureResult {
        private final boolean success;
        private final LocalDateTime timestamp;
        private final String message;

        public CaptureResult(boolean success, LocalDateTime timestamp, String message) {
            this.success = success;
            this.timestamp = timestamp;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getMessage() { return message; }
    }
}
