// LocalStorage persistent store for live clean state
const STORAGE_KEYS = {
  EMPLOYEES: 'nexushr_store_employees',
  PAYROLL_RUNS: 'nexushr_store_payroll_runs',
  PAYSLIPS: 'nexushr_store_payslips',
  LEAVE_REQUESTS: 'nexushr_store_leave_requests',
  PERFORMANCE: 'nexushr_store_performance',
  RECRUITMENT: 'nexushr_store_recruitment',
};

// Clean up old default seed data on first load for live mode
if (!localStorage.getItem('nexushr_live_v2')) {
  localStorage.removeItem(STORAGE_KEYS.EMPLOYEES);
  localStorage.removeItem(STORAGE_KEYS.PAYROLL_RUNS);
  localStorage.removeItem(STORAGE_KEYS.LEAVE_REQUESTS);
  localStorage.setItem('nexushr_live_v2', 'true');
}

const DEFAULT_EMPLOYEES: any[] = [];
const DEFAULT_PAYROLL_RUNS: any[] = [];
const DEFAULT_LEAVE_REQUESTS: any[] = [];

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
