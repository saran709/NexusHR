import React from 'react';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Shield, Clock, User, CheckCircle2, Lock, FileText } from 'lucide-react';

export interface AuditLogEntry {
  id: string;
  timestamp: string;
  action: string;
  performedBy: string;
  targetCycle: string;
  status: 'SUCCESS' | 'LOCKED' | 'PROCESSED' | 'WARNING';
  details: string;
}

interface PayrollAuditLogProps {
  entries?: AuditLogEntry[];
}

const DEFAULT_AUDIT_ENTRIES: AuditLogEntry[] = [
  {
    id: 'log-1',
    timestamp: '2026-08-31 18:30 UTC',
    action: 'LOCK_PAYROLL_RUN',
    performedBy: 'Super Admin (admin@nexushr.com)',
    targetCycle: 'August 2026 (08/01 - 08/31)',
    status: 'LOCKED',
    details: 'Payroll run locked and finalized for bank disbursement.'
  },
  {
    id: 'log-2',
    timestamp: '2026-08-30 14:15 UTC',
    action: 'PROCESS_PAYROLL_RUN',
    performedBy: 'HR Admin (hr@nexushr.com)',
    targetCycle: 'August 2026 (08/01 - 08/31)',
    status: 'PROCESSED',
    details: 'Calculated gross salaries, overtime allowances, and statutory tax deductions for 1,235 employees.'
  },
  {
    id: 'log-3',
    timestamp: '2026-08-01 09:00 UTC',
    action: 'CREATE_PAYROLL_RUN',
    performedBy: 'System Auto',
    targetCycle: 'August 2026 (08/01 - 08/31)',
    status: 'SUCCESS',
    details: 'Initial payroll cycle instantiated from attendance and leave records.'
  }
];

export const PayrollAuditLog: React.FC<PayrollAuditLogProps> = ({ entries = DEFAULT_AUDIT_ENTRIES }) => {
  return (
    <Card title="Payroll Audit Log & History">
      <div className="space-y-4">
        <p className="text-xs text-slate-500 dark:text-slate-400 -mt-2 mb-4">
          Chronological audit trail of payroll status changes, system locks, and administrative actions
        </p>
        <div className="relative border-l-2 border-slate-200 dark:border-slate-800 ml-4 space-y-6 py-2">
          {entries.map((entry) => (
            <div key={entry.id} className="relative pl-6 group">
              {/* Timeline Node Icon */}
              <div className="absolute -left-[17px] top-0 w-8 h-8 rounded-full bg-slate-100 dark:bg-slate-800 border-2 border-blue-500 flex items-center justify-center text-blue-600 dark:text-blue-400 shadow-sm">
                {entry.status === 'LOCKED' ? (
                  <Lock className="w-3.5 h-3.5" />
                ) : entry.status === 'PROCESSED' ? (
                  <CheckCircle2 className="w-3.5 h-3.5" />
                ) : (
                  <FileText className="w-3.5 h-3.5" />
                )}
              </div>

              {/* Audit Content Card */}
              <div className="p-4 bg-slate-50 dark:bg-slate-800/40 rounded-xl border border-slate-200 dark:border-slate-700/80 hover:shadow-md transition-shadow space-y-2">
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                  <div className="flex items-center space-x-2">
                    <span className="font-bold text-slate-900 dark:text-slate-100 text-sm">{entry.action}</span>
                    <Badge variant={entry.status === 'LOCKED' ? 'neutral' : entry.status === 'PROCESSED' ? 'success' : 'info'}>
                      {entry.status}
                    </Badge>
                  </div>
                  <div className="flex items-center text-xs text-slate-500 dark:text-slate-400">
                    <Clock className="w-3.5 h-3.5 mr-1" />
                    {entry.timestamp}
                  </div>
                </div>

                <div className="text-xs font-semibold text-blue-600 dark:text-blue-400">
                  Target Cycle: {entry.targetCycle}
                </div>

                <p className="text-xs text-slate-600 dark:text-slate-300">
                  {entry.details}
                </p>

                <div className="flex items-center text-[11px] text-slate-500 dark:text-slate-400 pt-1 border-t border-slate-200/60 dark:border-slate-700/60">
                  <User className="w-3 h-3 mr-1 text-slate-400" />
                  Performed by: <span className="font-medium text-slate-700 dark:text-slate-300 ml-1">{entry.performedBy}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </Card>
  );
};
