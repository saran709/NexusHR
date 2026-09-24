# NexusHR Production AWS Deployment Guide

## Architecture Overview
```
Internet
  ↓
AWS Application Load Balancer (ALB)
  ↓
AWS Load Balancer Controller / Ingress (Nginx Ingress Controller)
  ↓
API Gateway Service (`nexushr-gateway`)
  ↓
Backend Microservices (Auth, Employee, Attendance, Leave, Payroll, Performance, Notification, AI)
  ↓
Amazon RDS PostgreSQL & Amazon ElastiCache Redis
  ↓
Amazon S3 (Private Employee Documents) & External AI Providers
```

---

## 1. Prerequisites & Tooling
- AWS CLI v2 configured with appropriate credentials
- `kubectl` (compatible with target EKS version 1.28+)
- `helm` v3
- Terraform or AWS CloudFormation (for provisioning infrastructure)

---

## 2. AWS ECR (Elastic Container Registry) Setup
Create repositories for each microservice and frontend:
```bash
aws ecr create-repository --repository-name nexushr/gateway --region us-east-1
aws ecr create-repository --repository-name nexushr/auth-service --region us-east-1
aws ecr create-repository --repository-name nexushr/employee-service --region us-east-1
aws ecr create-repository --repository-name nexushr/attendance-service --region us-east-1
aws ecr create-repository --repository-name nexushr/leave-service --region us-east-1
aws ecr create-repository --repository-name nexushr/payroll-service --region us-east-1
aws ecr create-repository --repository-name nexushr/performance-service --region us-east-1
aws ecr create-repository --repository-name nexushr/notification-service --region us-east-1
aws ecr create-repository --repository-name nexushr/ai-service --region us-east-1
aws ecr create-repository --repository-name nexushr/frontend --region us-east-1
```

---

## 3. Amazon S3 Private Bucket Setup
Create a private S3 bucket for secure employee document storage:
```bash
aws s3api create-bucket --bucket nexushr-employee-documents-production --region us-east-1
aws s3api put-public-access-block --bucket nexushr-employee-documents-production \
    --public-access-block-configuration BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true
aws s3api put-bucket-encryption --bucket nexushr-employee-documents-production \
    --server-side-encryption-configuration '{"Rules":[{"ApplyServerSideEncryptionByDefault":{"SSEAlgorithm":"AES256"}}]}'
```
- **Pre-signed URLs**: The backend generates temporary pre-signed URLs (valid for 15 minutes) for authorized document retrieval without exposing raw S3 bucket credentials to clients.

---

## 4. AWS IAM & IRSA (IAM Roles for Service Accounts)
1. Create an IAM OIDC provider for your EKS cluster.
2. Create an IAM Role for Kubernetes service accounts using the policy template in `/infrastructure/aws/iam-policy-template.json`.
3. Annotate Kubernetes service accounts:
   ```yaml
   metadata:
     annotations:
       eks.amazonaws.com/role-arn: arn:aws:iam::123456789012:role/nexushr-eks-pod-execution-role
   ```

---

## 5. AWS Secrets Manager Configuration
Store production secrets in AWS Secrets Manager under `nexushr/production/secrets`:
- Database credentials
- JWT secret keys
- AI provider API keys
Secrets are mounted into Kubernetes pods via External Secrets Operator or AWS Secrets and Configuration Provider (ASCP).

---

## 6. EKS Deployment & Helm Configuration
Deploy the application using the Helm chart located in `/charts/nexushr`:
```bash
helm upgrade --install nexushr ./charts/nexushr \
  --namespace nexushr \
  --create-namespace \
  --set global.environment=production
```

---

## 7. Ingress, TLS, & Route53 DNS
- **Ingress**: Uses AWS ALB Ingress Controller routing external HTTPS traffic on port 443 to the API gateway.
- **TLS**: Managed via AWS Certificate Manager (ACM) attached to the ALB.
- **DNS**: Route 53 CNAME record pointing `hrms.company.com` to the ALB DNS name.

---

## 8. Horizontal Pod Autoscaling (HPA)
Scales microservice replicas dynamically based on CPU utilization (target 70%):
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: nexushr-employee-hpa
spec:
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

---

## 9. Verification & Status Notice
*Note: Since live AWS credentials are not active in this preview environment, deployment execution is verified through deployment-ready configuration artifacts (`/infrastructure/aws/`) and zero-error builds.*
