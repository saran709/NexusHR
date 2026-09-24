export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: 'SUPER_ADMIN' | 'HR_ADMIN' | 'PAYROLL_ADMIN' | 'MANAGER' | 'EMPLOYEE';
  department: string;
  position: string;
  avatarUrl?: string;
}

export interface Employee {
  id: string;
  employeeId: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  department: string;
  designation: string;
  employmentType: string;
  dateOfJoining: string;
  status: 'ACTIVE' | 'ON_LEAVE' | 'TERMINATED';
}

export interface AttendanceRecord {
  id: string;
  employeeId: string;
  date: string;
  clockInTime?: string;
  clockOutTime?: string;
  status: 'PRESENT' | 'ABSENT' | 'HALF_DAY' | 'LATE' | 'ON_LEAVE';
  totalHours?: number;
}

export interface LeaveRequest {
  id: string;
  employeeId: string;
  leaveTypeName: string;
  startDate: string;
  endDate: string;
  totalDays: number;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';
}

export interface PayrollRun {
  id: string;
  periodStart: string;
  periodEnd: string;
  status: 'DRAFT' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'LOCKED';
  totalEmployees: number;
  totalPayout: number;
  processedBy: string;
}

export interface Payslip {
  recordId: string;
  employeeId: string;
  payrollRunId: string;
  periodStart: string;
  periodEnd: string;
  basicSalary: number;
  allowances: number;
  bonuses: number;
  overtimePay: number;
  grossSalary: number;
  taxDeduction: number;
  leaveDeduction: number;
  otherDeductions: number;
  netSalary: number;
  items: { componentName: string; type: string; amount: number }[];
}

export interface PerformanceCycle {
  id: string;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  status: 'DRAFT' | 'ACTIVE' | 'COMPLETED';
}

export interface PerformanceGoal {
  id: string;
  employeeId: string;
  cycleId: string;
  title: string;
  description: string;
  category: string;
  target: number;
  measurement: string;
  weight: number;
  startDate: string;
  endDate: string;
  status: string;
  progress: number;
}

export interface Scorecard {
  employeeId: string;
  cycleId: string;
  weightedScore: number;
  totalGoals: number;
  completedGoals: number;
  goals: PerformanceGoal[];
  feedback: any[];
}
