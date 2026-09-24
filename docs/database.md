# NexusHR Database Design Documentation

## 1. Relational Database (PostgreSQL 17)
NexusHR utilizes PostgreSQL with Spring Data JPA and Hibernate ORM.

### Core Tables & Schemas
- **`users` / `roles`**: User credentials, email, password hashes, and RBAC roles (`ROLE_EMPLOYEE`, `ROLE_MANAGER`, `ROLE_HR_ADMIN`, `ROLE_PAYROLL_ADMIN`).
- **`employees`**: Personal info, department, designation, salary band, joining date, and employment status.
- **`attendance_records`**: Check-in timestamp, check-out timestamp, work hours, and status.
- **`leave_requests`**: Leave type (annual, sick, personal), start date, end date, status (pending, approved, rejected), and approver ID.
- **`payroll_records`**: Pay period, base salary, deductions, tax, net pay, status (draft, locked, paid), and generation timestamp.
- **`performance_reviews`**: Goals, self-review scores, manager review scores, weighted final score, and feedback.
- **`audit_logs`**: Action type, actor ID, timestamp, and metadata for security compliance.

## 2. Indexing & Optimization
- Composite indexes added for high-frequency queries:
  - `idx_employee_department ON employees(department_id, status)`
  - `idx_attendance_date ON attendance_records(employee_id, check_in_date)`
  - `idx_payroll_cycle ON payroll_records(pay_period, status)`

## 3. Caching (Redis 7)
- Redis caches static reference data (departments, salary bands) and expensive AI inference results with a 30-minute TTL.
