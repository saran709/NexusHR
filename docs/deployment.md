# NexusHR Deployment Documentation

## 1. Local Deployment (Docker Compose)
To run the complete stack locally:
```bash
docker compose up --build
```
This provisions PostgreSQL, Redis, API Gateway, all 8 microservices, Prometheus, Grafana, and the Nginx-hosted React frontend.

## 2. Production Kubernetes Deployment (AWS EKS)
- **Helm Chart**: Located in `/charts/nexushr`.
- **Installation**:
  ```bash
  helm upgrade --install nexushr ./charts/nexushr --namespace nexushr --create-namespace
  ```
- **Probes**: Liveness and readiness probes configured on `/actuator/health/liveness` and `/actuator/health/readiness`.
- **HPA**: Horizontal Pod Autoscalers configured to scale pods based on 70% CPU utilization.

## 3. AWS S3 & Secrets
- Private S3 bucket `nexushr-employee-documents-production` for secure document storage.
- AWS Secrets Manager integration for secure injection of database credentials and JWT keys.
