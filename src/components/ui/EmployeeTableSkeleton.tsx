import React from 'react';

export const EmployeeTableSkeleton: React.FC = () => {
  return (
    <div className="space-y-6 animate-pulse">
      {/* Header */}
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div className="space-y-2">
          <div className="h-8 bg-slate-200 dark:bg-slate-800 rounded-xl w-56"></div>
          <div className="h-4 bg-slate-200 dark:bg-slate-800 rounded-lg w-80"></div>
        </div>
        <div className="h-10 bg-slate-200 dark:bg-slate-800 rounded-xl w-36"></div>
      </div>

      {/* Search & Filter Bar */}
      <div className="p-4 bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 flex flex-col md:flex-row gap-4 justify-between">
        <div className="h-10 bg-slate-100 dark:bg-slate-800 rounded-xl w-full md:w-80"></div>
        <div className="flex gap-3">
          <div className="h-10 bg-slate-100 dark:bg-slate-800 rounded-xl w-32"></div>
          <div className="h-10 bg-slate-100 dark:bg-slate-800 rounded-xl w-32"></div>
        </div>
      </div>

      {/* Table Card */}
      <div className="bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 overflow-hidden">
        <div className="p-4 border-b border-slate-100 dark:border-slate-800 flex gap-4">
          <div className="h-4 bg-slate-200 dark:bg-slate-800 rounded w-1/4"></div>
          <div className="h-4 bg-slate-200 dark:bg-slate-800 rounded w-1/4"></div>
          <div className="h-4 bg-slate-200 dark:bg-slate-800 rounded w-1/4"></div>
          <div className="h-4 bg-slate-200 dark:bg-slate-800 rounded w-1/4"></div>
        </div>
        <div className="divide-y divide-slate-100 dark:divide-slate-800">
          {[1, 2, 3, 4, 5, 6].map((i) => (
            <div key={i} className="p-4 flex items-center justify-between gap-4">
              <div className="flex items-center space-x-3 w-1/3">
                <div className="w-10 h-10 rounded-full bg-slate-200 dark:bg-slate-800"></div>
                <div className="space-y-2 w-full">
                  <div className="h-4 bg-slate-200 dark:bg-slate-800 rounded w-3/4"></div>
                  <div className="h-3 bg-slate-100 dark:bg-slate-800/60 rounded w-1/2"></div>
                </div>
              </div>
              <div className="h-4 bg-slate-200 dark:bg-slate-800 rounded w-1/5"></div>
              <div className="h-4 bg-slate-200 dark:bg-slate-800 rounded w-1/5"></div>
              <div className="h-8 bg-slate-200 dark:bg-slate-800 rounded-lg w-20"></div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
