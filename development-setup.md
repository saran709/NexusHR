# NexusHR Development Setup Guide

This guide outlines the prerequisites and steps to run NexusHR locally.

## Prerequisites

- Java 21 JDK (`mvn`)
- Node.js 20+ & npm
- Docker & Docker Compose

## Environment Variables

Copy `.env.example` to `.env` and configure required keys (e.g. database credentials, Gemini API key for AI service).

```bash
cp .env.example .env
```

## Running Infrastructure via Docker Compose

```bash
docker compose up -d postgres redis prometheus grafana
```

## Building and Running Backend Services

Navigate to `backend/` and build the parent Maven project:

```bash
cd backend
mvn clean install
```

## Running Frontend

Navigate to `frontend/nexushr-web`:

```bash
cd frontend/nexushr-web
npm install
npm run build
npm run dev
```
