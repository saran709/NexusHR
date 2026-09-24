import React from 'react';

export default function App() {
  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 p-8">
      <div className="max-w-4xl mx-auto space-y-6">
        <header className="border-b border-slate-200 pb-6">
          <h1 className="text-3xl font-bold tracking-tight text-slate-900">NexusHR Foundation</h1>
          <p className="text-slate-600 mt-1">AI-Enabled Enterprise HR & Workforce Intelligence Platform — Module 0 Foundation</p>
        </header>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-3">
            <h2 className="text-xl font-semibold text-slate-800">Backend Architecture</h2>
            <p className="text-sm text-slate-600">
              Java 21, Spring Boot 3.3 Maven Multi-Module Project configured with API Gateway, Auth, Employee, Attendance, Leave, Payroll, Performance, Notification, and AI services.
            </p>
          </div>

          <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-3">
            <h2 className="text-xl font-semibold text-slate-800">Infrastructure & DevOps</h2>
            <p className="text-sm text-slate-600">
              PostgreSQL 17, Redis 7+, Prometheus, Grafana, Docker Compose, Kubernetes manifests, and Helm charts.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
