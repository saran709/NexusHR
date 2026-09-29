// LocalStorage persistent store for backend simulation
const STORAGE_KEYS = {
  EMPLOYEES: 'nexushr_store_employees',
  PAYROLL_RUNS: 'nexushr_store_payroll_runs',
  PAYSLIPS: 'nexushr_store_payslips',
  LEAVE_REQUESTS: 'nexushr_store_leave_requests',
  PERFORMANCE: 'nexushr_store_performance',
  RECRUITMENT: 'nexushr_store_recruitment',
};

const DEFAULT_EMPLOYEES = [
  { id: '1', employeeId: 'EMP-1001', firstName: 'Sarah', lastName: 'Jenkins', email: 'sarah.j@nexushr.com', phone: '+1 (555) 234-5678', department: 'Engineering', designation: 'Senior Staff Engineer', employmentType: 'Full-time', dateOfJoining: '2023-01-15', status: 'ACTIVE' },
  { id: '2', employeeId: 'EMP-1002', firstName: 'David', lastName: 'Miller', email: 'david.m@nexushr.com', phone: '+1 (555) 345-6789', department: 'Product', designation: 'Product Manager', employmentType: 'Full-time', dateOfJoining: '2022-11-01', status: 'ACTIVE' },
  { id: '3', employeeId: 'EMP-1003', firstName: 'Elena', lastName: 'Rostova', email: 'elena.r@nexushr.com', phone: '+1 (555) 456-7890', department: 'Design', designation: 'Head of UX Design', employmentType: 'Full-time', dateOfJoining: '2021-06-10', status: 'ACTIVE' },
  { id: '4', employeeId: 'EMP-1004', firstName: 'Marcus', lastName: 'Aurelius', email: 'marcus.a@nexushr.com', phone: '+1 (555) 789-0123', department: 'Finance', designation: 'Chief Financial Officer', employmentType: 'Full-time', dateOfJoining: '2020-03-01', status: 'ACTIVE' },
  { id: '5', employeeId: 'EMP-1005', firstName: 'Aisha', lastName: 'Patel', email: 'aisha.p@nexushr.com', phone: '+1 (555) 987-6543', department: 'HR', designation: 'HR Director', employmentType: 'Full-time', dateOfJoining: '2022-05-15', status: 'ACTIVE' },
];

const DEFAULT_PAYROLL_RUNS = [
  { id: '1', periodStart: '2026-09-01', periodEnd: '2026-09-30', totalPayout: 4245800, employeeCount: 1248, status: 'COMPLETED' },
  { id: '2', periodStart: '2026-08-01', periodEnd: '2026-08-31', totalPayout: 4190200, employeeCount: 1235, status: 'LOCKED' },
];

const DEFAULT_LEAVE_REQUESTS = [
  { id: 'l1', employeeName: 'Sarah Jenkins', type: 'Annual Leave', startDate: '2026-10-10', endDate: '2026-10-15', reason: 'Family vacation', status: 'PENDING' },
  { id: 'l2', employeeName: 'David Miller', type: 'Sick Leave', startDate: '2026-09-20', endDate: '2026-09-21', reason: 'Flu recovery', status: 'APPROVED' },
  { id: 'l3', employeeName: 'Elena Rostova', type: 'Remote Work', startDate: '2026-09-25', endDate: '2026-09-26', reason: 'Home renovation', status: 'APPROVED' },
];

export const storageService = {
  getEmployees: () => {
    const data = localStorage.getItem(STORAGE_KEYS.EMPLOYEES);
    if (!data) {
      localStorage.setItem(STORAGE_KEYS.EMPLOYEES, JSON.stringify(DEFAULT_EMPLOYEES));
      return DEFAULT_EMPLOYEES;
    }
    return JSON.parse(data);
  },
  saveEmployee: (emp: any) => {
    const employees = storageService.getEmployees();
    const newEmp = { ...emp, id: Date.now().toString(), employeeId: `EMP-${Math.floor(1000 + Math.random() * 9000)}`, status: 'ACTIVE', dateOfJoining: new Date().toISOString().split('T')[0] };
    employees.unshift(newEmp);
    localStorage.setItem(STORAGE_KEYS.EMPLOYEES, JSON.stringify(employees));
    return newEmp;
  },
  updateEmployee: (id: string, updates: any) => {
    const employees = storageService.getEmployees();
    const updated = employees.map((e: any) => e.id === id ? { ...e, ...updates } : e);
    localStorage.setItem(STORAGE_KEYS.EMPLOYEES, JSON.stringify(updated));
    return updated.find((e: any) => e.id === id);
  },

  getPayrollRuns: () => {
    const data = localStorage.getItem(STORAGE_KEYS.PAYROLL_RUNS);
    if (!data) {
      localStorage.setItem(STORAGE_KEYS.PAYROLL_RUNS, JSON.stringify(DEFAULT_PAYROLL_RUNS));
      return DEFAULT_PAYROLL_RUNS;
    }
    return JSON.parse(data);
  },
  savePayrollRuns: (runs: any[]) => {
    localStorage.setItem(STORAGE_KEYS.PAYROLL_RUNS, JSON.stringify(runs));
  },

  getLeaveRequests: () => {
    const data = localStorage.getItem(STORAGE_KEYS.LEAVE_REQUESTS);
    if (!data) {
      localStorage.setItem(STORAGE_KEYS.LEAVE_REQUESTS, JSON.stringify(DEFAULT_LEAVE_REQUESTS));
      return DEFAULT_LEAVE_REQUESTS;
    }
    return JSON.parse(data);
  },
  saveLeaveRequest: (req: any) => {
    const reqs = storageService.getLeaveRequests();
    const newReq = { ...req, id: `l-${Date.now()}`, status: 'PENDING' };
    reqs.unshift(newReq);
    localStorage.setItem(STORAGE_KEYS.LEAVE_REQUESTS, JSON.stringify(reqs));
    return newReq;
  },
  updateLeaveStatus: (id: string, status: string) => {
    const reqs = storageService.getLeaveRequests();
    const updated = reqs.map((r: any) => r.id === id ? { ...r, status } : r);
    localStorage.setItem(STORAGE_KEYS.LEAVE_REQUESTS, JSON.stringify(updated));
    return updated;
  }
};
