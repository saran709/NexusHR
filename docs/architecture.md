# NexusHR Architecture Documentation

## 1. System Overview
NexusHR is designed around an API Gateway pattern routing requests to specialized Spring Boot 3 microservices.

```
[React SPA Frontend]
        │ HTTPS
        ▼
[Spring Cloud API Gateway (Port 8080)]
        │ (JWT Auth & Routing)
        ├──────► Auth Service (8081)
        ├──────► Employee Service (8082)
        ├──────► Attendance Service (8083)
        ├──────► Leave Service (8084)
        ├──────► Payroll Service (8085)
        ├──────► Performance Service (8086)
        ├──────► Notification Service (8087)
        └──────► AI Workforce Service (8088)
        │
        ├──────► PostgreSQL 17 (Primary Persistence)
        ├──────► Redis 7 (Caching & Rate Limiting)
        └──────► AWS S3 (Private Document Storage)
```

## 2. Microservices Responsibilities
- **Gateway**: Single entry point, JWT validation, rate limiting, and request routing.
- **Auth Service**: User registration, login, token issuance, and password management.
- **Employee Service**: Employee profiles, departmental organization, and document management.
- **Attendance Service**: Check-in/check-out tracking and attendance history.
- **Leave Service**: Leave requests, balances, and manager approvals.
- **Payroll Service**: Salary calculations, deductions, tax processing, and payslip generation.
- **Performance Service**: Goal setting, weighted scorecards, and performance reviews.
- **Notification Service**: STOMP WebSocket real-time alerts and notification history.
- **AI Service**: Attrition prediction, skill gap analysis, engagement scoring, and HR assistant.
