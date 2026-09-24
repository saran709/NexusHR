# NexusHR Kubernetes Production Deployment Guide

## Prerequisites
- Kubernetes cluster (v1.26+)
- Helm 3 installed
- Ingress Controller (e.g. NGINX Ingress) configured
- Cert-Manager for TLS certificates

## Helm Chart Installation

1. **Clone and Navigate**:
   ```bash
   cd charts/nexushr
   ```

2. **Install Chart**:
   ```bash
   helm install nexushr . --namespace nexushr --create-namespace
   ```

3. **Verify Deployment**:
   ```bash
   kubectl get all -n nexushr
   ```

## Configuration
- Modify `values.yaml` for replica counts, resource requests/limits, and ingress hosts.
- Secrets are managed via Kubernetes Secrets template with placeholder injection.
