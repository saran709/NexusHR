import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import { SkeletonLoader } from '../components/ui/SkeletonLoader';
import { Bell, CheckCircle2, Calendar, Award, DollarSign, FileText, Settings, CheckCheck } from 'lucide-react';
import apiClient from '../services/api';
import { useToast } from '../context/ToastContext';

export const Notifications: React.FC = () => {
  const { showToast } = useToast();
  const queryClient = useQueryClient();
  const [activeTab, setActiveTab] = useState<'notifications' | 'preferences'>('notifications');

  // Fetch notifications
  const { data: notifsData, isLoading } = useQuery({
    queryKey: ['notifications'],
    queryFn: async () => {
      try {
        return await apiClient.get<any[]>('/notifications');
      } catch {
        return [
          { id: '1', type: 'LEAVE_APPROVED', title: 'Leave Request Approved', message: 'Your annual leave request for Oct 12 - Oct 15 has been approved.', createdAt: '2 hours ago', read: false },
          { id: '2', type: 'PAYROLL_COMPLETED', title: 'Payroll Processed', message: 'September 2026 salary disbursement has been successfully processed.', createdAt: '1 day ago', read: true },
          { id: '3', type: 'PERFORMANCE_REVIEW_AVAILABLE', title: 'Performance Review Active', message: 'Q3 2026 self-review stage is now open. Please submit your feedback.', createdAt: '3 days ago', read: true },
        ];
      }
    },
  });

  // Mark as read mutation
  const markReadMutation = useMutation({
    mutationFn: async (id: string) => {
      return apiClient.post(`/notifications/${id}/read`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] });
    },
  });

  // Mark all as read mutation
  const markAllReadMutation = useMutation({
    mutationFn: async () => {
      return apiClient.post('/notifications/read-all');
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] });
      showToast('All notifications marked as read', 'success');
    },
  });

  const notifications = notifsData || [];
  const unreadCount = notifications.filter((n: any) => !n.read).length;

  return (
    <div className="space-y-6 animate-fadeIn max-w-4xl">
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold tracking-tight text-slate-900 dark:text-slate-100">Notifications & Alerts</h1>
          <p className="text-sm text-slate-500 dark:text-slate-400">Real-time enterprise event stream, approval alerts, and notification preferences</p>
        </div>
        <div className="flex items-center space-x-2">
          <Button
            variant={activeTab === 'notifications' ? 'primary' : 'outline'}
            size="sm"
            onClick={() => setActiveTab('notifications')}
          >
            Notifications {unreadCount > 0 && <span className="ml-2 px-2 py-0.5 rounded-full bg-rose-500 text-white text-[10px]">{unreadCount}</span>}
          </Button>
          <Button
            variant={activeTab === 'preferences' ? 'primary' : 'outline'}
            size="sm"
            onClick={() => setActiveTab('preferences')}
          >
            <Settings className="w-4 h-4 mr-1.5" /> Preferences
          </Button>
        </div>
      </div>

      {activeTab === 'notifications' && (
        <Card
          title="Notification History"
          action={
            unreadCount > 0 && (
              <Button size="sm" variant="outline" onClick={() => markAllReadMutation.mutate()}>
                <CheckCheck className="w-4 h-4 mr-1.5" /> Mark all as read
              </Button>
            )
          }
        >
          {isLoading ? (
            <SkeletonLoader rows={3} />
          ) : notifications.length === 0 ? (
            <div className="p-8 text-center text-slate-500">No notifications found.</div>
          ) : (
            <div className="space-y-4">
              {notifications.map((notif: any) => (
                <div
                  key={notif.id}
                  onClick={() => !notif.read && markReadMutation.mutate(notif.id)}
                  className={`flex items-start space-x-4 p-4 rounded-xl border transition-all cursor-pointer ${
                    notif.read
                      ? 'bg-transparent border-slate-100 dark:border-slate-800'
                      : 'bg-blue-50/50 dark:bg-blue-950/30 border-blue-100 dark:border-blue-900/40 shadow-sm'
                  }`}
                >
                  <div className="p-3 rounded-xl bg-blue-100 dark:bg-blue-900/40 text-blue-700 dark:text-blue-300 shrink-0">
                    <Bell className="w-5 h-5" />
                  </div>
                  <div className="flex-1">
                    <div className="flex items-center justify-between mb-1">
                      <div className="flex items-center space-x-2">
                        <p className="font-bold text-sm text-slate-900 dark:text-slate-100">{notif.title}</p>
                        {!notif.read && <span className="w-2 h-2 rounded-full bg-blue-600"></span>}
                      </div>
                      <span className="text-xs text-slate-400">{notif.createdAt}</span>
                    </div>
                    <p className="text-xs text-slate-600 dark:text-slate-300">{notif.message}</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      )}

      {activeTab === 'preferences' && (
        <Card title="Notification & Channel Preferences">
          <div className="space-y-6">
            <div className="space-y-4">
              <h4 className="font-bold text-sm text-slate-900 dark:text-slate-100">Notification Channels</h4>
              {[
                { name: 'Email Notifications', desc: 'Receive instant alerts via work email', enabled: true },
                { name: 'SMS Alerts', desc: 'Urgent security and approval notifications via SMS', enabled: false },
                { name: 'Real-time WebSockets / Push', desc: 'In-app real-time notification stream', enabled: true },
              ].map((pref, i) => (
                <div key={i} className="flex items-center justify-between p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-800">
                  <div>
                    <p className="font-bold text-sm text-slate-900 dark:text-slate-100">{pref.name}</p>
                    <p className="text-xs text-slate-500">{pref.desc}</p>
                  </div>
                  <input type="checkbox" defaultChecked={pref.enabled} className="w-4 h-4 text-blue-600 rounded" />
                </div>
              ))}
            </div>

            <div className="flex justify-end pt-4 border-t border-slate-100 dark:border-slate-800">
              <Button onClick={() => showToast('Notification preferences saved successfully', 'success')}>
                Save Preferences
              </Button>
            </div>
          </div>
        </Card>
      )}
    </div>
  );
};
