/**
 * Diagnostic utility for tracking payroll API requests and path mappings
 * against backend PayrollController mappings.
 */

export interface PayrollDiagnosticLog {
  timestamp: string;
  action: string;
  method: string;
  endpoint: string;
  expectedBackendMapping: string;
  payload?: any;
  status: 'SUCCESS' | 'FALLBACK_TRIGGERED' | 'ERROR';
  details?: string;
}

class PayrollDiagnosticsLogger {
  private logs: PayrollDiagnosticLog[] = [];

  public logRequest(action: string, method: string, endpoint: string, payload?: any): PayrollDiagnosticLog {
    const expectedBackendMapping = '@RequestMapping("/api/payroll") + @PostMapping("/runs/{id}/lock")';
    const logEntry: PayrollDiagnosticLog = {
      timestamp: new Date().toISOString(),
      action,
      method,
      endpoint,
      expectedBackendMapping: endpoint.includes('lock') 
        ? '@PostMapping("/runs/{id}/lock")' 
        : endpoint.includes('process') 
        ? '@PostMapping("/runs/{id}/process")' 
        : '@GetMapping("/runs")',
      payload,
      status: 'FALLBACK_TRIGGERED',
      details: 'Request routed through apiClient with preview fallback handler.'
    };

    this.logs.unshift(logEntry);
    console.group(`[Payroll Diagnostics] ${action}`);
    console.log('Timestamp:', logEntry.timestamp);
    console.log('Endpoint Requested:', endpoint);
    console.log('Expected Backend Mapping:', logEntry.expectedBackendMapping);
    if (payload) console.log('Payload:', payload);
    console.groupEnd();

    return logEntry;
  }

  public getLogs(): PayrollDiagnosticLog[] {
    return this.logs;
  }
}

export const payrollDiagnostics = new PayrollDiagnosticsLogger();
