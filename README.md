# NexusHR — Enterprise Workforce Intelligence & Human Resources Management System

## 1. Project Title & Overview
**NexusHR** is a comprehensive, microservices-based enterprise Human Resources Management System (HRMS) combined with an advanced **AI Workforce Intelligence Module**. Designed for scalable organizations, NexusHR automates employee lifecycle management, attendance tracking, leave requests, automated payroll calculations, performance reviews, secure document management, and AI-driven predictive insights.

---

## 2. Problem Statement
Modern enterprises struggle with fragmented HR tools, manual attendance and payroll processing bottlenecks, lack of proactive attrition risk visibility, skill gaps, and security/compliance vulnerabilities across distributed teams.

---

## 3. Solution
NexusHR provides a unified, secure, enterprise-grade platform integrating:
- Complete employee lifecycle & self-service portals
- Real-time attendance tracking & multi-level leave approvals
- Automated payroll calculation & secure payslip generation
- Goal-based performance reviews
- AI-driven attrition prediction, skill gap analysis, and engagement scoring
- Real-time WebSocket notifications & complete observability (Prometheus + Grafana)

---

## 4. Key Features
- **Authentication & RBAC**: Stateless JWT authentication with Role-Based Access Control (Employee, Manager, HR Admin, Payroll Admin).
- **Employee Management**: Onboarding, role assignment, departmental organization, and private S3 document management.
- **Attendance & Leave**: Check-in/check-out tracking, leave balance tracking, and multi-tier manager approvals.
- **Payroll**: Automated salary calculations, tax deductions, payroll run locks, and downloadable PDF/digital payslips.
- **Performance**: Goal setting, weighted scorecards, self-reviews, and manager evaluations.
- **AI Workforce Intelligence**: Attrition risk prediction, skill gap analysis, engagement scoring, and natural-language HR assistant.
- **Security & Compliance**: Strict DTO validation, rate limiting, secure headers, CORS policies, and audit logging.

---

## 5. Architecture
NexusHR follows an API Gateway microservices architecture:
```
Internet → AWS ALB → Ingress → API Gateway (Port 8080)
                              ├── Auth Service (8081)
                              ├── Employee Service (8082)
                              ├── Attendance Service (8083)
                              ├── Leave Service (8084)
                              ├── Payroll Service (8085)
                              ├── Performance Service (8086)
                              ├── Notification Service (8087)
                              └── AI Workforce Service (8088)
Persistence: PostgreSQL 17 + Redis 7 + AWS S3 (Documents)
```

---

## 6. Technology Stack
- **Frontend**: React 18, TypeScript, Tailwind CSS, Lucide Icons, Vite.
- **Backend**: Spring Boot 3 (Java 17), Spring Cloud Gateway, Spring Security, Spring AI, Spring Data JPA, Hibernate.
- **Database & Cache**: PostgreSQL 17, Redis 7 (Caching & Rate Limiting).
- **Infrastructure & DevOps**: Docker, Docker Compose, Kubernetes, Helm, GitHub Actions CI/CD.
- **Observability**: Spring Actuator, Prometheus, Grafana, Sentry.

---

## 7. Setup & Installation

### Prerequisites
- Node.js 20+ & npm
- Java 17 JDK & Maven 3.9+
- Docker & Docker Compose

### Local Development Setup
1. **Clone Repository**:
   ```bash
   git clone https://github.com/enterprise/nexushr.git
   cd nexushr
   ```
2. **Install Frontend Dependencies**:
   ```bash
   npm install
   ```
3. **Start Local Docker Environment (PostgreSQL & Redis)**:
   ```bash
   docker compose up -d postgres redis
   ```
4. **Run Frontend Development Server**:
   ```bash
   npm run dev
   ```

---

## 8. Environment Variables
Create a `.env` or `environment.properties` file based on `.env.example`:
```env
PORT=3000
VITE_API_BASE_URL=http://localhost:8080/api
DATABASE_URL=jdbc:postgresql://localhost:5432/nexushr
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=your_super_secret_jwt_key_min_64_characters_long
OPENAI_API_KEY=your_openai_api_key_or_local_provider
```

---

## 9. Database, Authentication & Security
- **Database**: Relational PostgreSQL schema managed via JPA/Hibernate with optimized composite indexes.
- **Authentication**: Stateless JWT bearer tokens with refresh token rotation.
- **Security**: Strict DTO validation, rate limiting via Bucket4j, secure HTTP headers, SQL injection protection via parameterized queries, and CORS enforcement.

---

## 10. AI Workforce Intelligence
- **Attrition Prediction**: Evaluates tenure, attendance trends, overtime, leave utilization, performance scores, salary bands, and feedback sentiment.
- **Skill Gap Analysis**: Compares employee skills against job-role requirements with training recommendations.
- **Engagement Scoring**: Computes organizational engagement drivers.
- **AI Assistant**: Natural-language HR query assistant obeying strict authorization rules.

---

## 11. Testing, Docker, Kubernetes & CI/CD
- **Testing**: Frontend type-check & linting (`npm run lint`), production builds (`npm run build`), and backend JUnit/integration tests.
- **Docker**: Multi-stage backend and frontend Dockerfiles with non-root security hardening.
- **Kubernetes & Helm**: Helm chart (`/charts/nexushr`) with Deployments, Services, ConfigMaps, Secrets templates, HPA, and health probes (`/actuator/health/liveness`).
- **CI/CD**: GitHub Actions workflows for CI, security vulnerability scanning (`security.yml`), and CD container pushing (`cd.yml`).

---

## 12. Monitoring & Deployment
- **Monitoring**: Spring Actuator metrics exposed to Prometheus and visualized via pre-provisioned Grafana dashboards.
- **Deployment**: Production-ready deployment guides for AWS EKS, ECR, and S3 in `/docs/`.

---

## 13. Demo Credentials
- **HR Admin**: `admin@nexushr.com` / `SecurePassword123!`
- **Manager**: `manager@nexushr.com` / `SecurePassword123!`
- **Employee**: `employee@nexushr.com` / `SecurePassword123!`

---

## 14. Screenshots & UI
NexusHR features a modern, clean, dark/light enterprise UI with responsive dashboards, real-time notification badge counts, and interactive analytics.

---

## 15. Future Roadmap
- Multi-region active-active database replication
- Advanced custom HR report builder
- Integration with external payroll providers (ADP, Gusto)
- Mobile companion app (React Native)
