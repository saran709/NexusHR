import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metric';

export const errorRate = new Rate('errors');

export const options = {
  stages: [
    { duration: '30s', target: 100 },  // Ramp up to 100 users
    { duration: '1m', target: 500 },   // Ramp up to 500 users
    { duration: '30s', target: 0 },    // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<300'], // Core API target <300ms
    errors: ['rate<0.01'],            // Error rate below 1%
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  // 1. Authentication Login Test
  const loginRes = http.post(`${BASE_URL}/api/auth/login`, JSON.stringify({
    username: 'admin@nexushr.com',
    password: 'SecurePassword123!'
  }), {
    headers: { 'Content-Type': 'application/json' },
  });

  const loginSuccess = check(loginRes, {
    'login status is 200': (r) => r.status === 200,
    'login duration < 300ms': (r) => r.timings.duration < 300,
  });
  errorRate.add(!loginSuccess);

  let token = '';
  try {
    const body = JSON.parse(loginRes.body);
    token = body.token || '';
  } catch (e) {
    // Fallback token if mock
  }

  const authHeaders = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
  };

  // 2. Employee API Test
  const empRes = http.get(`${BASE_URL}/api/employees?page=0&size=20`, authHeaders);
  const empSuccess = check(empRes, {
    'employees status is 200': (r) => r.status === 200,
    'employees duration < 300ms': (r) => r.timings.duration < 300,
  });
  errorRate.add(!empSuccess);

  // 3. Attendance API Test
  const attRes = http.get(`${BASE_URL}/api/attendance/today`, authHeaders);
  check(attRes, { 'attendance status 200 or 404': (r) => r.status === 200 || r.status === 404 });

  // 4. Leave API Test
  const leaveRes = http.get(`${BASE_URL}/api/leave/requests`, authHeaders);
  check(leaveRes, { 'leave status 200': (r) => r.status === 200 });

  // 5. Payroll Calculation Test
  const payrollRes = http.get(`${BASE_URL}/api/payroll/summary`, authHeaders);
  check(payrollRes, { 'payroll status 200': (r) => r.status === 200 });

  // 6. AI Insights API Test
  const aiRes = http.get(`${BASE_URL}/api/ai/insights/attrition`, authHeaders);
  check(aiRes, { 'ai insights status 200': (r) => r.status === 200 });

  sleep(1);
}
