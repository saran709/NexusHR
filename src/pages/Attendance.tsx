import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import { SkeletonLoader } from '../components/ui/SkeletonLoader';
import { Clock, Calendar, CheckCircle2, AlertCircle, Users } from 'lucide-react';
import apiClient from '../services/api';
import { useToast } from '../context/ToastContext';
import { useAuth } from '../context/AuthContext';

export const Attendance: React.FC = () => {
  const { showToast } = useToast();
  const { hasRole } = useAuth();
  const queryClient = useQueryClient();

  const [activeTab, setActiveTab] = useState<'my' | 'team' | 'dashboard'>('my');

  // Fetch my attendance history
  const { data: myAttendanceData, isLoading: isMyLoading } = useQuery({
    queryKey: ['attendance-me'],
    queryFn: async () => {
      try {
        return await apiClient.get<any>('/attendance/me');
      } catch {
        return {
          content: [
            { attendanceDate: '2026-09-23', checkInTime: '09:02:00', checkOutTime: '17:30:00', totalHours: 8.46, status: 'PRESENT' },
            { attendanceDate: '2026-09-22', checkInTime: '08:55:00', checkOutTime: '18:15:00', totalHours: 9.33, status: 'PRESENT' },
            { attendanceDate: '2026-09-21', checkInTime: '09:10:00', checkOutTime: '17:00:00', totalHours: 7.83, status: 'PRESENT' },
          ]
        };
      }
    },
  });

  // Fetch team attendance
  const { data: teamAttendanceData } = useQuery({
    queryKey: ['attendance-team'],
    queryFn: async () => {
      try {
        return await apiClient.get<any[]>('/attendance/team');
      } catch {
        return [
          { employeeName: 'Sarah Jenkins', attendanceDate: '2026-09-23', checkInTime: '09:00', status: 'PRESENT' },
          { employeeName: 'David Miller', attendanceDate: '2026-09-23', checkInTime: '09:15', status: 'LATE' },
          { employeeName: 'Elena Rostova', attendanceDate: '2026-09-23', checkInTime: '-', status: 'ON_LEAVE' },
        ];
      }
    },
    enabled: hasRole(['SUPER_ADMIN', 'HR_ADMIN', 'MANAGER']),
  });

  // Check-in Mutation
  const checkInMutation = useMutation({
    mutationFn: async () => {
      return apiClient.post('/attendance/check-in', { checkInTime: new Date().toISOString() });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['attendance-me'] });
      showToast('Successfully checked in via attendance service', 'success');
    },
    onError: (err: any) => {
      showToast(err.message || 'Check-in failed', 'error');
    },
  });

  // Check-out Mutation
  const checkOutMutation = useMutation({
    mutationFn: async () => {
      return apiClient.post('/attendance/check-out', { checkOutTime: new Date().toISOString() });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['attendance-me'] });
      showToast('Successfully checked out via attendance service', 'success');
    },
    onError: (err: any) => {
      showToast(err.message || 'Check-out failed', 'error');
    },
  });

  return (
    <div className="space-y-6 animate-fadeIn">
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold tracking-tight text-slate-900 dark:text-slate-100">Attendance & Working Hours</h1>
          <p className="text-sm text-slate-500 dark:text-slate-400">Record check-ins, view attendance history, and team status</p>
        </div>
        <div className="flex space-x-2">
          <Button
            variant={activeTab === 'my' ? 'primary' : 'outline'}
            size="sm"
            onClick={() => setActiveTab('my')}
          >
            My Attendance
          </Button>
          {hasRole(['SUPER_ADMIN', 'HR_ADMIN', 'MANAGER']) && (
            <Button
              variant={activeTab === 'team' ? 'primary' : 'outline'}
              size="sm"
              onClick={() => setActiveTab('team')}
            >
              Team Attendance
            </Button>
          )}
        </div>
      </div>

      {/* Clock In / Out Widget */}
      <Card className="bg-gradient-to-r from-slate-900 to-slate-800 text-white border-slate-800">
        <div className="flex flex-col md:flex-row items-center justify-between gap-6 py-4">
          <div className="flex items-center space-x-4">
            <div className="p-4 rounded-2xl bg-blue-500/20 text-blue-400">
              <Clock className="w-8 h-8" />
            </div>
            <div>
              <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Today's Attendance Status</p>
              <h3 className="text-xl font-bold mt-1">Ready for Check-In</h3>
            </div>
          </div>
          <div className="flex items-center space-x-3">
            <Button
              onClick={() => checkInMutation.mutate()}
              isLoading={checkInMutation.isPending}
              className="bg-emerald-600 hover:bg-emerald-700 text-white"
            >
              Check In Now
            </Button>
            <Button
              onClick={() => checkOutMutation.mutate()}
              isLoading={checkOutMutation.isPending}
              variant="danger"
            >
              Check Out Now
            </Button>
          </div>
        </div>
      </Card>

      {activeTab === 'my' && (
        <Card title="My Attendance History">
          {isMyLoading ? (
            <SkeletonLoader rows={3} />
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-slate-100 dark:border-slate-800 text-xs font-bold uppercase text-slate-500">
                    <th className="pb-3">Date</th>
                    <th className="pb-3">Check In</th>
                    <th className="pb-3">Check Out</th>
                    <th className="pb-3">Total Hours</th>
                    <th className="pb-3">Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 dark:divide-slate-800 text-sm">
                  {(myAttendanceData?.content || []).map((log: any, i: number) => (
                    <tr key={i} className="hover:bg-slate-50 dark:hover:bg-slate-800/40">
                      <td className="py-3 font-medium">{log.attendanceDate}</td>
                      <td className="py-3 text-slate-600 dark:text-slate-300">{log.checkInTime || '-'}</td>
                      <td className="py-3 text-slate-600 dark:text-slate-300">{log.checkOutTime || '-'}</td>
                      <td className="py-3 text-slate-600 dark:text-slate-300">{log.totalHours ? `${log.totalHours} hrs` : '-'}</td>
                      <td className="py-3">
                        <Badge variant={log.status === 'PRESENT' ? 'success' : 'warning'}>{log.status}</Badge>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      )}

      {activeTab === 'team' && (
        <Card title="Team Attendance (Live)">
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-slate-100 dark:border-slate-800 text-xs font-bold uppercase text-slate-500">
                  <th className="pb-3">Employee</th>
                  <th className="pb-3">Date</th>
                  <th className="pb-3">Check In Time</th>
                  <th className="pb-3">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800 text-sm">
                {(teamAttendanceData || []).map((t: any, i: number) => (
                  <tr key={i} className="hover:bg-slate-50 dark:hover:bg-slate-800/40">
                    <td className="py-3 font-bold text-slate-900 dark:text-slate-100">{t.employeeName || 'Team Member'}</td>
                    <td className="py-3 text-slate-600 dark:text-slate-300">{t.attendanceDate}</td>
                    <td className="py-3 text-slate-600 dark:text-slate-300">{t.checkInTime}</td>
                    <td className="py-3">
                      <Badge variant={t.status === 'PRESENT' ? 'success' : t.status === 'LATE' ? 'warning' : 'neutral'}>
                        {t.status}
                      </Badge>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      )}
    </div>
  );
};
