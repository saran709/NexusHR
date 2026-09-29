import React, { useState } from 'react';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';
import { Activity, ChevronDown, ChevronUp, RefreshCw, ShieldCheck } from 'lucide-react';
import { payrollDiagnostics, PayrollDiagnosticLog } from '../../utils/payrollDiagnostics';

interface PayrollLoggerProps {
  runs: any[];
}

export const PayrollLogger: React.FC<PayrollLoggerProps> = ({ runs }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [logs, setLogs] = useState<PayrollDiagnosticLog[]>(payrollDiagnostics.getLogs());

  const refreshLogs = () => {
    setLogs([...payrollDiagnostics.getLogs()]);
  };

  return (
    <Card className="border border-slate-200 dark:border-slate-800 shadow-sm bg-slate-900 text-slate-100">
      <div className="flex items-center justify-between cursor-pointer" onClick={() => setIsOpen(!isOpen)}>
        <div className="flex items-center space-x-3">
          <div className="p-2 bg-blue-500/20 text-blue-400 rounded-lg">
            <Activity className="w-5 h-5 animate-pulse" />
          </div>
          <div>
            <h3 className="font-bold text-sm text-white flex items-center gap-2">
              Payroll Diagnostics & Lock State Inspector
              <Badge variant="success">Active</Badge>
            </h3>
            <p className="text-xs text-slate-400">Tracks API requests, backend mappings, and lock status transitions</p>
          </div>
        </div>
        <div className="flex items-center space-x-2">
          <Button
            size="sm"
            variant="outline"
            className="text-slate-300 border-slate-700 hover:bg-slate-800"
            onClick={(e) => {
              e.stopPropagation();
              refreshLogs();
            }}
          >
            <RefreshCw className="w-3.5 h-3.5 mr-1" /> Refresh
          </Button>
          {isOpen ? <ChevronUp className="w-5 h-5 text-slate-400" /> : <ChevronDown className="w-5 h-5 text-slate-400" />}
        </div>
      </div>

      {isOpen && (
        <div className="mt-4 pt-4 border-t border-slate-800 space-y-4 animate-fadeIn">
          {/* Current State Summary */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="p-3 bg-slate-800/60 rounded-lg border border-slate-700/60">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Payroll Runs Loaded</span>
              <div className="text-lg font-bold mt-1 text-emerald-400">{runs.length} Runs</div>
              <div className="text-xs text-slate-400 mt-1">
                Active Locked: {runs.filter(r => r.status === 'LOCKED').length} | Completed: {runs.filter(r => r.status === 'COMPLETED').length}
              </div>
            </div>
            <div className="p-3 bg-slate-800/60 rounded-lg border border-slate-700/60">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Backend Route Mappings</span>
              <div className="text-xs font-mono text-blue-400 mt-1">POST /api/payroll/runs/{'{id}'}/lock</div>
              <div className="text-xs text-slate-400 mt-1 flex items-center gap-1">
                <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" /> PreAuthorize: SUPER_ADMIN, HR_ADMIN, PAYROLL_ADMIN
              </div>
            </div>
          </div>

          {/* Request Audit Trail */}
          <div>
            <h4 className="text-xs font-bold uppercase text-slate-400 tracking-wider mb-2">Audit Trail & Action Logs</h4>
            {logs.length === 0 ? (
              <div className="text-xs text-slate-500 py-4 text-center bg-slate-800/30 rounded-lg border border-dashed border-slate-700">
                No payroll actions recorded yet. Click 'Process' or 'Lock' on any payroll run to trigger request logging.
              </div>
            ) : (
              <div className="space-y-2 max-h-60 overflow-y-auto pr-2">
                {logs.map((log, index) => (
                  <div key={index} className="p-3 bg-slate-800/80 rounded-lg border border-slate-700 text-xs space-y-1">
                    <div className="flex justify-between items-center">
                      <span className="font-bold text-blue-400">{log.action}</span>
                      <span className="text-slate-400 font-mono text-[10px]">{log.timestamp}</span>
                    </div>
                    <div className="flex items-center space-x-2 text-slate-300">
                      <span className="px-1.5 py-0.5 rounded bg-slate-900 font-mono text-[10px] text-emerald-400">{log.method}</span>
                      <span className="font-mono text-slate-300">{log.endpoint}</span>
                    </div>
                    <div className="text-slate-400 text-[11px] font-mono">
                      Mapping: <span className="text-amber-400">{log.expectedBackendMapping}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </Card>
  );
};
