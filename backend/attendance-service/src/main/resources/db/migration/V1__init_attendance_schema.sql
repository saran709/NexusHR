CREATE TABLE IF NOT EXISTS attendance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    total_working_hours DOUBLE PRECISION,
    status VARCHAR(30) NOT NULL DEFAULT 'PRESENT',
    is_late BOOLEAN DEFAULT FALSE,
    is_early_departure BOOLEAN DEFAULT FALSE,
    overtime_hours DOUBLE PRECISION DEFAULT 0.0,
    timezone VARCHAR(50) DEFAULT 'UTC',
    remarks VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_employee_attendance_date UNIQUE (employee_id, attendance_date)
);

CREATE INDEX idx_attendance_date ON attendance(attendance_date);
CREATE INDEX idx_attendance_status ON attendance(status);

CREATE TABLE IF NOT EXISTS attendance_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL,
    event_type VARCHAR(30) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    source VARCHAR(30) NOT NULL,
    location VARCHAR(100),
    device_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_attendance_event_employee ON attendance_events(employee_id);
