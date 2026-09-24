CREATE TABLE IF NOT EXISTS performance_cycles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS performance_goals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL,
    cycle_id UUID REFERENCES performance_cycles(id) ON DELETE SET NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    target DOUBLE PRECISION NOT NULL,
    measurement VARCHAR(100) NOT NULL,
    weight DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    progress DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS performance_reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL,
    reviewer_id UUID,
    cycle_id UUID NOT NULL REFERENCES performance_cycles(id) ON DELETE CASCADE,
    stage VARCHAR(30) NOT NULL DEFAULT 'SELF_REVIEW',
    self_comments TEXT,
    manager_comments TEXT,
    hr_comments TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    weighted_score DOUBLE PRECISION DEFAULT 0.0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_review_employee_cycle UNIQUE (employee_id, cycle_id)
);

CREATE TABLE IF NOT EXISTS performance_feedback (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    review_id UUID NOT NULL REFERENCES performance_reviews(id) ON DELETE CASCADE,
    employee_id UUID NOT NULL,
    author_id UUID NOT NULL,
    author_role VARCHAR(30) NOT NULL,
    comments TEXT NOT NULL,
    rating DOUBLE PRECISION,
    is_confidential BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS performance_ratings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    review_id UUID NOT NULL REFERENCES performance_reviews(id) ON DELETE CASCADE,
    rater_id UUID NOT NULL,
    rating_type VARCHAR(30) NOT NULL,
    score DOUBLE PRECISION NOT NULL,
    comments TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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

-- Seed Default Cycle
INSERT INTO performance_cycles (name, description, start_date, end_date, status) VALUES
('Q3 2026 Performance Cycle', 'Quarterly performance review and OKR tracking', '2026-07-01', '2026-09-30', 'ACTIVE')
ON CONFLICT DO NOTHING;
