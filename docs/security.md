# NexusHR Security Documentation

## 1. Authentication & JWT
- **Stateless Tokens**: Issued upon successful authentication (`/api/auth/login`), containing claims for subject, roles, and tenant ID.
- **Token Expiry**: Access tokens expire in 15 minutes; refresh tokens use secure HTTP-only cookies with rotation.

## 2. Authorization & RBAC
- Role-Based Access Control enforced via Spring Security `@PreAuthorize("hasRole('HR_ADMIN')")`.
- Tenant and principal ownership checks prevent Insecure Direct Object References (IDOR).

## 3. Data Protection & Encryption
- Passwords hashed using BCrypt (cost factor 12).
- Transport Layer Security (TLS 1.3) enforced on all incoming connections.
- Private employee documents stored in encrypted AWS S3 buckets accessed via temporary pre-signed URLs.

## 4. Input Validation & OWASP Top 10 Mitigation
- Strict DTO validation via Jakarta Bean Validation.
- Parameterized JPA queries prevent SQL Injection.
- React JSX auto-escaping prevents Cross-Site Scripting (XSS).
- Redis-backed rate limiting protects authentication endpoints against brute-force attacks.
- Security headers (`X-Content-Type-Options`, `X-Frame-Options`, `Content-Security-Policy`, `HSTS`) enforced.
