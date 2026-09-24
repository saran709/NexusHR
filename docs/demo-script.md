# NexusHR Enterprise Demo Script (3–7 Minutes)

This script outlines the exact sequence of actions for presenting the NexusHR platform during executive demonstrations and stakeholder reviews.

---

## Prerequisites & Demo Access
- **No judge account creation required**: Pre-provisioned demo credentials are injected via environment variables or accessible through the secure demo login shortcut on the login screen.
- **Demo Admin Email**: `admin@nexushr.enterprise`
- **Demo Role**: `SUPER_ADMIN`

---

## Step-by-Step Demo Flow

### 1. Secure Login & Authentication (0:00 - 0:45)
- Navigate to the NexusHR landing / login page.
- Click **"Demo Admin Login"** (or enter pre-configured admin credentials).
- Highlight JWT secure token issuance, HTTP-only secure cookie storage, and RBAC privilege enforcement.

### 2. Role-Based Executive Dashboard (0:45 - 1:15)
- View the unified executive dashboard displaying total headcount, active attendance rate, pending leave requests, and payroll summary metrics.
- Demonstrate responsive layout across desktop and tablet viewports.

### 3. Employee Lifecycle & Onboarding (1:15 - 2:00)
- Navigate to **Employees** (`/employees`).
- Review employee profiles, department filters, and role assignments (`ADMIN`, `HR`, `MANAGER`, `EMPLOYEE`).
- Open an employee profile to inspect secure document uploads (integrated with Amazon S3 storage).

### 4. Attendance & Leave Management (2:00 - 2:45)
- Switch to **Attendance** (`/attendance`) to review real-time check-ins and overtime logs.
- Switch to **Leave Management** (`/leave`), submit a sample leave request, and demonstrate manager approval workflows with automated notifications.

### 5. Payroll & Payslip Generation (2:45 - 3:30)
- Navigate to **Payroll** (`/payroll`).
- Demonstrate salary calculations, tax deductions, and instant PDF/digital payslip generation.

### 6. Performance & OKRs (3:30 - 4:15)
- Visit **Performance** (`/performance`) to review employee OKRs, quarterly goals, and structured peer/manager performance reviews.

### 7. AI Workforce Intelligence (4:15 - 5:15)
- Open **AI Workforce Insights** (`/ai-insights`).
- Showcase the predictive **Attrition Risk Watchlist** (XGBoost model metrics, ROC-AUC score, confidence levels, and explainable AI factors).
- Explore **Skill Gap Analysis** and interact with the **HR Natural Language Assistant** by asking: *"Which departments have high overtime this month?"*.

### 8. Monitoring & Observability (5:15 - 5:45)
- Briefly show the Prometheus / Grafana observability dashboards and Spring Boot Actuator health endpoints (`/actuator/health`) ensuring 99.95% availability tracking.

### 9. Conclusion & Q&A (5:45 - 6:00)
- Summarize platform readiness, enterprise security (OWASP Top 10 mitigation, RBAC, JWT), and Kubernetes/Docker production deployment architecture.
