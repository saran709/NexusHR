# NexusHR CI/CD Pipeline Documentation

## Overview
NexusHR uses GitHub Actions for continuous integration, automated security scanning, and continuous deployment.

## Workflows

### 1. CI Pipeline (`.github/workflows/ci.yml`)
- **Backend CI**: Sets up JDK 17, builds all Maven microservices, and runs unit tests.
- **Frontend CI**: Sets up Node.js 20, runs `npm ci`, executes linter & typecheck (`npm run lint`), and builds the React frontend.

### 2. Security Scan (`.github/workflows/security.yml`)
- Scans dependencies for vulnerabilities.
- Performs secret scanning using Trufflehog to prevent credential leaks.

### 3. CD Pipeline (`.github/workflows/cd.yml`)
- Triggers on push to `main`.
- Builds multi-stage Docker images for frontend and backend services.
- Pushes images to GitHub Container Registry (`ghcr.io`).

## Required GitHub Secrets
- Managed securely via GitHub Secrets (e.g. `GITHUB_TOKEN`). No secrets are hardcoded in workflows.
