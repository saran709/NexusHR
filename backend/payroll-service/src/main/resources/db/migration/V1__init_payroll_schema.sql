CREATE TABLE IF NOT EXISTS salary_structures (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL UNIQUE,
    basic_salary DOUBLE PRECISION NOT NULL,
    allowances DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    deductions DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS salary_components (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tax_configurations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tax_bracket_name VARCHAR(100) NOT NULL,
    min_income DOUBLE PRECISION NOT NULL,
    max_income DOUBLE PRECISION,
    tax_percentage DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payroll_runs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    total_employees INT DEFAULT 0,
    total_payout DOUBLE PRECISION DEFAULT 0.0,
    processed_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_payroll_period UNIQUE (period_start, period_end)
);

CREATE TABLE IF NOT EXISTS payroll_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payroll_run_id UUID NOT NULL REFERENCES payroll_runs(id) ON DELETE CASCADE,
    employee_id UUID NOT NULL,
    basic_salary DOUBLE PRECISION NOT NULL,
    allowances DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    bonuses DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    overtime_pay DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    gross_salary DOUBLE PRECISION NOT NULL,
    tax_deduction DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    leave_deduction DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    other_deductions DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    net_salary DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_payroll_run_employee UNIQUE (payroll_run_id, employee_id)
);

CREATE TABLE IF NOT EXISTS payroll_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payroll_record_id UUID NOT NULL REFERENCES payroll_records(id) ON DELETE CASCADE,
    component_name VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_email VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Seed Tax Configurations
INSERT INTO tax_configurations (tax_bracket_name, min_income, max_income, tax_percentage) VALUES
('Low Bracket', 0.0, 30000.0, 5.0),
('Medium Bracket', 30001.0, 80000.0, 15.0),
('High Bracket', 80001.0, NULL, 25.0)
ON CONFLICT DO NOTHING;

-- Seed Salary Components
INSERT INTO salary_components (name, type, description) VALUES
('Basic Salary', 'EARNING', 'Base monthly salary'),
('Allowances', 'EARNING', 'Standard corporate allowances'),
('Bonus', 'EARNING', 'Performance or special bonus'),
('Overtime', 'EARNING', 'Overtime compensation'),
('Tax', 'DEDUCTION', 'Income tax deduction'),
('Leave Deduction', 'DEDUCTION', 'Deduction for unpaid/excess leaves'),
('Other Deductions', 'DEDUCTION', 'Miscellaneous deductions')
ON CONFLICT (name) DO NOTHING;
