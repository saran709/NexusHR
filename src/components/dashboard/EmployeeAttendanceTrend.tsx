import React from 'react';
import { Card } from '../ui/Card';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';

// Generate simulated 30-day attendance trend data
const generateAttendanceData = () => {
  const data = [];
  const now = new Date();
  for (let i = 29; i >= 0; i--) {
    const d = new Date(now);
    d.setDate(d.getDate() - i);
    const dateStr = d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    
    // Simulate realistic weekday vs weekend variance
    const isWeekend = d.getDay() === 0 || d.getDay() === 6;
    const basePresent = isWeekend ? 180 : 1150 + Math.floor(Math.random() * 80);
    const baseRemote = isWeekend ? 40 : 320 + Math.floor(Math.random() * 50);
    const baseAbsent = isWeekend ? 10 : 25 + Math.floor(Math.random() * 15);

    data.push({
      date: dateStr,
      Present: basePresent,
      Remote: baseRemote,
      Absent: baseAbsent,
    });
  }
  return data;
};

const attendanceData = generateAttendanceData();

export const EmployeeAttendanceTrend: React.FC = () => {
  return (
    <Card title="Employee Attendance Trend (Last 30 Days)">
      <div className="space-y-4">
        <p className="text-xs text-slate-500 dark:text-slate-400 -mt-2">
          Daily breakdown of office presence, remote work, and absences across the organization
        </p>
        <div className="h-72 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={attendanceData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
              <defs>
                <linearGradient id="colorPresent" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.8}/>
                  <stop offset="95%" stopColor="#3b82f6" stopOpacity={0.05}/>
                </linearGradient>
                <linearGradient id="colorRemote" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#10b981" stopOpacity={0.8}/>
                  <stop offset="95%" stopColor="#10b981" stopOpacity={0.05}/>
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#334155" opacity={0.15} />
              <XAxis dataKey="date" stroke="#64748b" fontSize={11} tickLine={false} />
              <YAxis stroke="#64748b" fontSize={11} tickLine={false} />
              <Tooltip
                contentStyle={{
                  backgroundColor: '#1e293b',
                  borderColor: '#334155',
                  borderRadius: '0.75rem',
                  color: '#f8fafc',
                  fontSize: '12px'
                }}
              />
              <Legend wrapperStyle={{ fontSize: '12px', paddingTop: '10px' }} />
              <Area type="monotone" dataKey="Present" stroke="#3b82f6" strokeWidth={2} fillOpacity={1} fill="url(#colorPresent)" />
              <Area type="monotone" dataKey="Remote" stroke="#10b981" strokeWidth={2} fillOpacity={1} fill="url(#colorRemote)" />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </div>
    </Card>
  );
};
