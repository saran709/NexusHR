# NexusHR CI/CD Pipeline Documentation

This document outlines the GitHub Actions CI/CD pipeline configuration, required secrets, stages, and troubleshooting procedures for the NexusHR platform.

---

## 1. Required GitHub Secrets
To enable publishing container images to GitHub Packages / Container Registry (`ghcr.io`), ensure the following permissions and secrets are available in your GitHub repository:
- **`GITHUB_TOKEN`**: Automatically provided by GitHub Actions with `packages: write` and `contents: read` permissions.

---

## 2. Pipeline Stages (`.github/workflows/ci-cd.yml`)

The pipeline runs sequentially across 4 robust stages:

1. **`validate-backend` (Java 21)**:
   - Sets up Temurin JDK 21 with Maven caching.
   - Compiles the Spring Boot backend (`mvn clean package -DskipTests`).
   - Executes backend unit and service tests (`mvn test`).

2. **`validate-frontend` (Node.js 20)**:
   - Sets up Node.js 20 with npm caching.
   - Installs dependencies (`npm ci`).
   - Runs TypeScript linting and type checking (`npm run lint`).
   - Builds the Vite React frontend bundle (`npm run build`).

3. **`build-and-security-check` (Docker & Secrets)**:
   - Builds optimized multi-stage Docker images (`Dockerfile.frontend` and `Dockerfile.backend`) for testing.
   - Runs **TruffleHog** secret scanning to verify no API keys or credentials are leaked in the codebase.

4. **`deploy` (Publish & Release)**:
   - **Trigger condition**: Runs only on pushes to the `main` branch **after successful completion of all validation and security stages**.
   - Logs into GitHub Container Registry (`ghcr.io`).
   - Builds and pushes tagged container images (`ghcr.io/<owner>/nexushr-frontend:latest` and `ghcr.io/<owner>/nexushr-api-gateway:latest`).

---

## 3. How Deployment is Triggered
- **Pull Requests**: Runs `validate-backend`, `validate-frontend`, and `build-and-security-check` to validate code quality and security without publishing images.
- **Push to `main`**: Runs the complete CI/CD pipeline, publishing verified production container images to GitHub Container Registry upon success.

---

## 4. Troubleshooting Failed Builds

- **Maven Test Failures**:
  - Check the JUnit test report in the GitHub Actions workflow logs. Run `mvn test` locally inside `/backend` to reproduce.
- **Node.js Lint / Typecheck Failures**:
  - Run `npm run lint` locally to spot TypeScript or ESLint errors.
- **Docker Build Errors**:
  - Ensure multi-stage build contexts are correct and all required pom.xml files are copied into `Dockerfile.backend`.
- **Registry Login / Push Failures**:
  - Ensure GitHub Actions workflow permissions have **Read and write permissions** enabled under *Repository Settings > Actions > General > Workflow permissions*.
