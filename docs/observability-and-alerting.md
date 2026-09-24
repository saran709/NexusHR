# NexusHR Complete Observability & Alerting Guide

## Overview
NexusHR implements enterprise-grade observability across all microservices using:
- **Spring Boot Actuator**: Health probes (`/actuator/health`, `/actuator/health/liveness`, `/actuator/health/readiness`), metrics (`/actuator/metrics`), and Prometheus export (`/actuator/prometheus`).
- **Structured Logging**: JSON-formatted logs enriched with unique request IDs, correlation IDs, request duration, HTTP status, and service names.
- **Prometheus**: Automated scraping of all microservice endpoints every 15 seconds.
- **Grafana**: Pre-provisioned enterprise dashboards for API performance, service health, JVM metrics, database connection pools (HikariCP), Redis, and HTTP error rates.
- **Sentry Integration**: Configurable error tracking and exception reporting with PII scrubbing.

---

## 1. Backend Observability Configuration

### Spring Actuator Properties (`application.yml`)
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus
  endpoint:
  health:
      probes:
        enabled: true
      show-details: always
  metrics:
    tags:
      application: ${spring.application.name}
```

### Structured Logging & Correlation ID Filter
All incoming HTTP requests pass through an enterprise logging filter that generates or extracts:
- `X-Request-ID`
- `X-Correlation-ID`
- Request duration (ms)
- HTTP Method & Status
- Service Name

---

## 2. Prometheus Configuration
Prometheus scrapes all 10 microservices via the configuration file at `infrastructure/prometheus/prometheus.yml`:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'nexushr-gateway'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['api-gateway:8080']

  - job_name: 'nexushr-auth'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['auth-service:8081']

  - job_name: 'nexushr-employee'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['employee-service:8082']

  - job_name: 'nexushr-attendance'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['attendance-service:8083']

  - job_name: 'nexushr-leave'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['leave-service:8084']

  - job_name: 'nexushr-payroll'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['payroll-service:8085']

  - job_name: 'nexushr-performance'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['performance-service:8086']

  - job_name: 'nexushr-notification'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['notification-service:8087']

  - job_name: 'nexushr-ai'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['ai-service:8088']
```

---

## 3. Grafana Dashboards & Datasources
Grafana is fully provisioned with:
- **Datasource**: Automatically connects to Prometheus (`http://prometheus:9090`).
- **Dashboards**: Pre-loaded dashboard located at `infrastructure/grafana/dashboards/nexushr-overview.json` monitoring:
  - API Request Rate & Latency (`http_server_requests_seconds_count`)
  - Service Availability (`up{job=~"nexushr-.*"}`)
  - JVM Heap & Non-Heap Memory (`jvm_memory_used_bytes`)
  - Database Connection Pools (`hikaricp_connections_active`) & Redis
  - HTTP 5xx Error Rates

---

## 4. Sentry Error Tracking Configuration
Sentry is integrated via environment variables to capture unhandled exceptions without leaking sensitive employee PII or credentials:

```properties
sentry.dsn=https://examplePublicKey@o0.ingest.sentry.io/0
sentry.environment=production
sentry.traces-sample-rate=0.2
sentry.send-default-pii=false
```

### PII Scrubbing Rules
- Passwords, JWT tokens, and OAuth headers are automatically redacted before error payloads are transmitted to Sentry.

---

## 5. Alerting Documentation & Rules
Production alerting rules should be configured in Prometheus Alertmanager or Grafana Alerting:
1. **Service Down Alert**: Triggered when `up{job=~"nexushr-.*"} == 0` for over 1 minute.
2. **High Error Rate Alert**: Triggered when `sum(rate(http_server_requests_seconds_count{status=~"5.*"}[5m])) > 5`.
3. **Database Connection Saturation**: Triggered when HikariCP active connections exceed 85% of maximum pool size.
4. **JVM Memory Warning**: Triggered when JVM heap usage exceeds 90% for over 3 minutes.

---

## 6. Verification Steps
1. **Actuator Health Check**: Verify `curl http://localhost:8080/actuator/health` returns `{"status":"UP"}` with liveness and readiness indicators.
2. **Prometheus Scraping**: Access Prometheus UI at `http://localhost:9090/targets` and confirm all targets show state `UP`.
3. **Grafana Datasource**: Open Grafana at `http://localhost:3000` (or configured port), log in, and verify the "NexusHR Enterprise Observability" dashboard displays real-time metrics.
