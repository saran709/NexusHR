# NexusHR Testing Documentation

## 1. Testing Strategy
NexusHR implements a comprehensive testing pyramid covering frontend and backend components.

## 2. Test Suites
- **Backend Tests**: JUnit 5 unit tests, MockMvc controller tests, Spring Data repository tests, and Spring Boot integration tests.
- **Frontend Tests**: TypeScript type checking (`npm run lint`), React component tests, and Zod form validation tests.
- **Security Tests**: Automated vulnerability scanning, dependency audits, and penetration test simulations.

## 3. Execution Commands
- **Frontend Linting & Type-Check**:
  ```bash
  npm run lint
  ```
- **Frontend Production Build**:
  ```bash
  npm run build
  ```
- **Backend Tests**:
  ```bash
  mvn clean test
  ```
