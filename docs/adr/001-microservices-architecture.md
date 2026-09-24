# ADR 001: Microservices Architecture for NexusHR

## Status
Accepted

## Context
NexusHR is an enterprise-grade HR and Workforce Intelligence platform requiring high scalability, independent deployability of business domains (Payroll, Attendance, Performance, AI), fault isolation, and specialized team ownership.

## Decision
We adopt a microservices architecture built with Java 21 and Spring Boot 3.3, organized as a Maven multi-module project. 
Key modules include:
- API Gateway (Spring Cloud Gateway)
- Auth Service (Spring Security 6 + JWT + Argon2)
- Domain Services (Employee, Attendance, Leave, Payroll, Performance, Notification, AI)
- Common shared libraries

## Consequences
- **Pros**: Independent scaling, isolated database schemas per service (PostgreSQL 17), isolated failure domains, ability to leverage Spring AI independently.
- **Cons**: Distributed transaction management complexity, service discovery overhead, distributed tracing requirements.
