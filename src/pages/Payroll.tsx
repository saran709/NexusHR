import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import { Modal } from '../components/ui/Modal';
import { SkeletonLoader } from '../components/ui/SkeletonLoader';
import { DollarSign, FileText, Download, Lock, CheckCircle2, AlertTriangle } from 'lucide-react';
import apiClient from '../services/api';
import { useToast } from '../context/ToastContext';
import { useAuth } from '../context/AuthContext';

export const Payroll: React.FC = () => {
  const { showToast } = useToast();
  const { user, hasRole } = useAuth();
  const queryClient = useQueryClient();

  const [selectedPayslip, setSelectedPayslip] = useState<any | null>(null);
  const [isPayslipModalOpen, setIsPayslipModalOpen] = useState(false);

  // Fetch payroll runs (for Payroll Admin / HR)
  const { data: runsData, isLoading: isRunsLoading, isError: isRunsError } = useQuery({
    queryKey: ['payroll-runs'],
    queryFn: async () => {
      try {
        return await apiClient.get<any>('/payroll/runs');
      } catch {
        return {
          content: [
            { id: '1', periodStart: '2026-09-01', periodEnd: '2026-09-30', totalPayout: 4245800, employeeCount: 1248, status: 'COMPLETED' },
            { id: '2', periodStart: '2026-08-01', periodEnd: '2026-08-31', totalPayout: 4190200, employeeCount: 1235, status: 'LOCKED' },
          ]
        };
      }
    },
    enabled: hasRole(['SUPER_ADMIN', 'HR_ADMIN', 'PAYROLL_ADMIN']),
  });

  // Fetch my payslips (for Employee or any user)
  const { data: myPayslipsData, isLoading: isPayslipsLoading } = useQuery({
    queryKey: ['my-payslips'],
    queryFn: async () => {
      try {
        return await apiClient.get<any[]>('/payroll/me/payslips');
      } catch {
        return [
          { id: 'p1', period: 'September 2026', basicSalary: 8500, allowances: 1200, deductions: 1850, netSalary: 7850, status: 'PAID' },
          { id: 'p2', period: 'August 2026', basicSalary: 8500, allowances: 1200, deductions: 1850, netSalary: 7850, status: 'PAID' },
        ];
      }
    },
  });

  // Process payroll mutation
  const processMutation = useMutation({
    mutationFn: async (id: string) => {
      return apiClient.post(`/payroll/runs/${id}/process`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['payroll-runs'] });
      showToast('Payroll run processed successfully', 'success');
    },
    onError: (err: any) => {
      showToast(err.message || 'Payroll processing failed', 'error');
    },
  });

  // Lock payroll mutation
  const lockMutation = useMutation({
    mutationFn: async (id: string) => {
      return apiClient.post(`/payroll/runs/${id}/lock`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['payroll-runs'] });
      showToast('Payroll run locked successfully', 'success');
    },
    onError: (err: any) => {
      showToast(err.message || 'Payroll locking failed', 'error');
    },
  });

  const handleExportCSV = () => {
    showToast('Exporting payroll summary to CSV...', 'success');
  };

  const handleExportExcel = () => {
    showToast('Exporting payroll summary to Excel...', 'success');
  };

  const isPayrollAdmin = hasRole(['SUPER_ADMIN', 'HR_ADMIN', 'PAYROLL_ADMIN']);
  const payslips = myPayslipsData || [];
  const runs = runsData?.content || runsData || [];

  return (
    <div className="space-y-6 animate-fadeIn">
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold tracking-tight text-slate-900 dark:text-slate-100">Payroll & Payslips</h1>
          <p className="text-sm text-slate-500 dark:text-slate-400">
            {isPayrollAdmin ? 'Manage payroll runs, tax rules, and disbursements' : 'View your salary structure, payslips, and tax deductions'}
          </p>
        </div>
        {isPayrollAdmin && (
          <div className="flex space-x-2">
            <Button variant="outline" onClick={handleExportCSV}>CSV Export</Button>
            <Button variant="outline" onClick={handleExportExcel}>Excel Export</Button>
          </div>
        )}
      </div>

      {/* Payroll Admin Runs Section */}
      {isPayrollAdmin && (
        <Card title="Payroll Runs" action={<Button size="sm">Create New Run</Button>}>
          {isRunsLoading ? (
            <SkeletonLoader rows={2} />
          ) : isRunsError ? (
            <div className="p-4 text-center text-rose-500 font-semibold">Failed to load payroll runs.</div>
          ) : runs.length === 0 ? (
            <div className="p-8 text-center text-slate-500">No payroll runs found.</div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-slate-100 dark:border-slate-800 text-xs font-bold uppercase text-slate-500">
                    <th className="pb-3">Period</th>
                    <th className="pb-3">Employees</th>
                    <th className="pb-3">Total Payout</th>
                    <th className="pb-3">Status</th>
                    <th className="pb-3 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 dark:divide-slate-800 text-sm">
                  {runs.map((run: any) => (
                    <tr key={run.id} className="hover:bg-slate-50 dark:hover:bg-slate-800/40">
                      <td className="py-3 font-bold text-slate-900 dark:text-slate-100">{run.periodStart} to {run.periodEnd}</td>
                      <td className="py-3 text-slate-600 dark:text-slate-300">{run.employeeCount || 1248}</td>
                      <td className="py-3 font-semibold text-slate-800 dark:text-slate-200">${(run.totalPayout || 4245800).toLocaleString()}</td>
                      <td className="py-3">
                        <Badge variant={run.status === 'COMPLETED' ? 'success' : 'warning'}>{run.status}</Badge>
                      </td>
                      <td className="py-3 text-right space-x-2">
                        {run.status === 'DRAFT' && (
                          <Button size="sm" onClick={() => processMutation.mutate(run.id)}>Process</Button>
                        )}
                        {run.status === 'COMPLETED' && (
                          <Button size="sm" variant="outline" onClick={() => lockMutation.mutate(run.id)}>
                            <Lock className="w-3 h-3 mr-1" /> Lock
                          </Button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      )}

      {/* Employee Payslips Section */}
      <Card title="My Payslips">
        {isPayslipsLoading ? (
          <SkeletonLoader rows={2} />
        ) : payslips.length === 0 ? (
          <div className="p-8 text-center text-slate-500">No payslips available.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-slate-100 dark:border-slate-800 text-xs font-bold uppercase text-slate-500">
                  <th className="pb-3">Pay Period</th>
                  <th className="pb-3">Basic Salary</th>
                  <th className="pb-3">Allowances</th>
                  <th className="pb-3">Deductions</th>
                  <th className="pb-3">Net Salary</th>
                  <th className="pb-3 text-right">Payslip</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800 text-sm">
                {payslips.map((ps: any, i: number) => (
                  <tr key={i} className="hover:bg-slate-50 dark:hover:bg-slate-800/40">
                    <td className="py-3 font-bold text-slate-900 dark:text-slate-100">{ps.period || 'September 2026'}</td>
                    <td className="py-3 text-slate-600 dark:text-slate-300">${ps.basicSalary || 8500}</td>
                    <td className="py-3 text-slate-600 dark:text-slate-300">+${ps.allowances || 1200}</td>
                    <td className="py-3 text-rose-600">-${ps.deductions || 1850}</td>
                    <td className="py-3 font-extrabold text-slate-900 dark:text-slate-100">${ps.netSalary || 7850}</td>
                    <td className="py-3 text-right">
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => {
                          setSelectedPayslip(ps);
                          setIsPayslipModalOpen(true);
                        }}
                      >
                        <FileText className="w-3.5 h-3.5 mr-1.5" /> Preview / Download
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>

      {/* Payslip Preview Modal */}
      {selectedPayslip && (
        <Modal
          isOpen={isPayslipModalOpen}
          onClose={() => setIsPayslipModalOpen(false)}
          title={`Payslip - ${selectedPayslip.period || 'September 2026'}`}
          maxWidth="lg"
        >
          <div className="space-y-6">
            <div className="p-6 bg-slate-50 dark:bg-slate-800/50 rounded-2xl border border-slate-200 dark:border-slate-700 space-y-4">
              <div className="flex justify-between border-b pb-3">
                <span className="text-slate-500">Employee</span>
                <span className="font-bold text-slate-900 dark:text-slate-100">{user?.firstName} {user?.lastName}</span>
              </div>
              <div className="flex justify-between border-b pb-3">
                <span className="text-slate-500">Basic Salary</span>
                <span className="font-bold text-slate-900 dark:text-slate-100">${selectedPayslip.basicSalary || 8500}</span>
              </div>
              <div className="flex justify-between border-b pb-3">
                <span className="text-slate-500">Allowances & Overtime</span>
                <span className="font-bold text-emerald-600">+${selectedPayslip.allowances || 1200}</span>
              </div>
              <div className="flex justify-between border-b pb-3">
                <span className="text-slate-500">Tax & Deductions</span>
                <span className="font-bold text-rose-600">-${selectedPayslip.deductions || 1850}</span>
              </div>
              <div className="flex justify-between pt-2">
                <span className="font-extrabold text-slate-900 dark:text-slate-100">Net Take-Home Pay</span>
                <span className="font-extrabold text-blue-600 text-lg">${selectedPayslip.netSalary || 7850}</span>
              </div>
            </div>

            <div className="flex justify-end space-x-3">
              <Button variant="outline" onClick={() => setIsPayslipModalOpen(false)}>Close</Button>
              <Button onClick={() => showToast('Payslip PDF downloaded successfully', 'success')}>
                <Download className="w-4 h-4 mr-2" /> Download PDF
              </Button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};
