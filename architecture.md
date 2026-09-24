# NexusHR Architecture Documentation

## System Architecture

NexusHR follows an enterprise microservices architecture designed for high scalability, fault isolation, and domain segregation.

```
┌─────────────────────────────────────────────────────────────┐
│                      Frontend SPA                           │
│        (React 19, TypeScript, Vite, Tailwind, shadcn)       │
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTPS / REST
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                        API Gateway                          │
│               (Spring Cloud Gateway, Routing)               │
└──────┬───────┬───────┬───────┬───────┬───────┬───────┬──────┘
       │       │       │       │       │       │       │
       ▼       ▼       ▼       ▼       ▼       ▼       ▼
   ┌──────┐┌──────┐┌──────┐┌──────┐┌──────┐┌──────┐┌──────┐
   │ Auth ││ Empl ││ Attd ││Leave ││Payrl ││ Perf ││  AI  │
   │ Svc  ││ Svc  ││ Svc  ││ Svc  ││ Svc  ││ Svc  ││ Svc  │
   └──┬───┘└──┬───┘└──┬───┘└──┬───┘└──┬───┘└──┬───┘└──┬───┘
      │       │       │       │       │       │       │
      └───────┴───────┴───┬───┴───────┴───────┴───────┘
                          │
            ┌─────────────┴─────────────┐
            ▼                           ▼
    PostgreSQL 17                   Redis 7+
  (Relational Data)             (Cache & Sessions)
```

## Core Architectural Principles

1. **Domain-Driven Design (DDD)**: Each microservice represents a bounded context (Employee, Payroll, Leave, Attendance, Performance, AI, Auth, Notification).
2. **Database-per-Service**: Services maintain isolated schema boundaries within PostgreSQL 17 to ensure loose coupling.
3. **Decentralized Security**: API Gateway handles edge authentication validation, while downstream services verify scopes and roles via validated JWT claims.
4. **Resilience & Observability**: Centralized metrics collection via Prometheus, visualization through Grafana dashboards, and distributed tracing.
