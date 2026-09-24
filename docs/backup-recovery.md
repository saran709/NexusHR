# NexusHR Enterprise Backup and Disaster Recovery Guide

This document defines the backup, retention, and disaster recovery procedures for NexusHR production environments running on AWS EKS, RDS PostgreSQL, Redis, and Amazon S3.

---

## 1. PostgreSQL (Amazon RDS) Backup & Recovery
- **Automated Snapshots**: Daily automated snapshots configured with a 30-day retention period.
- **Point-in-Time Recovery (PITR)**: Continuous transaction log (WAL) archiving enables restoration to any second within the retention window.
- **Manual Snapshots**: Mandatory before any major schema migration or Flyway upgrade:
  ```bash
  aws rds create-db-snapshot \
    --db-instance-identifier nexushr-postgres-prod \
    --db-snapshot-identifier nexushr-postgres-manual-pre-migration-$(date +%Y%m%d)
  ```
- **Restoration Procedure**:
  ```bash
  aws rds restore-db-instance-from-db-snapshot \
    --db-instance-identifier nexushr-postgres-restored \
    --db-snapshot-identifier nexushr-postgres-manual-pre-migration-YYYYMMDD
  ```

---

## 2. Redis Cache Backup & Recovery
- **Persistence Configuration**: Redis is configured with both RDB snapshots (`save 60 1`) and Append-Only File (`appendonly yes`) enabled.
- **Data Sensitivity**: Session tokens and temporary pub/sub states are stored in Redis; no permanent transactional data relies solely on Redis.
- **Recovery**: Automatic restart via Kubernetes liveness probe / Deployment restart reloads the latest AOF/RDB state from persistent EBS volumes.

---

## 3. Amazon S3 Document & Payslip Storage
- **Versioning**: Bucket versioning is enabled on all production S3 buckets (`nexushr-enterprise-docs`) to protect against accidental deletion or overwriting.
- **Cross-Region Replication**: Configured for disaster recovery across AWS regions.
- **Lifecycle Policies**: Transition older documents to S3 Glacier after 365 days for cost-optimized long-term compliance storage.

---

## 4. Application & Kubernetes Rollback Strategy
- **Deployment Rollback**: Helm and Kubernetes native rollback mechanisms allow instant reversion to the previous stable release:
  ```bash
  helm rollback nexushr-release 1
  # Or via kubectl
  kubectl rollout undo deployment/nexushr-api-gateway -n nexushr-prod
  ```
- **ConfigMap & Secret Management**: Managed via sealed secrets or external secrets operator with gitops history tracking.
