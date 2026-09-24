# NexusHR API Documentation

## 1. Base URL & Authentication
- **Base URL**: `https://api.nexushr.com/api` (or `http://localhost:8080/api`)
- **Authentication**: Bearer token required in Authorization header (`Authorization: Bearer <JWT>`).

## 2. Core API Endpoints

### Authentication (`/api/auth`)
- `POST /api/auth/register` - Register a new user account
- `POST /api/auth/login` - Authenticate and obtain JWT token
- `POST /api/auth/refresh` - Refresh access token

### Employees (`/api/employees`)
- `GET /api/employees` - Retrieve paginated employee list (HR Admin)
- `POST /api/employees` - Onboard a new employee (HR Admin)
- `GET /api/employees/{id}` - Get employee details

### Attendance (`/api/attendance`)
- `POST /api/attendance/check-in` - Record employee check-in
- `POST /api/attendance/check-out` - Record employee check-out
- `GET /api/attendance/today` - Get today's attendance status

### Leave (`/api/leave`)
- `POST /api/leave/request` - Submit leave request
- `GET /api/leave/requests` - List leave requests
- `PUT /api/leave/requests/{id}/approve` - Manager approval

### Payroll (`/api/payroll`)
- `GET /api/payroll/summary` - Get payroll summary & calculations
- `POST /api/payroll/run` - Execute payroll run (Payroll Admin)

### AI Workforce Intelligence (`/api/ai/insights`)
- `GET /api/ai/insights/attrition` - Attrition risk predictions
- `GET /api/ai/insights/skill-gaps` - Departmental skill gap analysis
- `GET /api/ai/insights/engagement` - Engagement scoring
- `POST /api/ai/assistant/query` - Authorized natural-language HR assistant query
