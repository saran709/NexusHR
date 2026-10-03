import React from 'react';

export const DashboardSkeleton: React.FC = () => {
  return (
    <div className="space-y-6 animate-pulse">
      {/* Header Skeleton */}
      <div className="flex justify-between items-center">
        <div className="space-y-2">
          <div className="h-8 bg-slate-200 dark:bg-slate-800 rounded-xl w-64"></div>
          <div className="h-4 bg-slate-200 dark:bg-slate-800 rounded-lg w-96"></div>
        </div>
        <div className="h-10 bg-slate-200 dark:bg-slate-800 rounded-xl w-32"></div>
      </div>

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {[1, 2, 3, 4].map((i) => (
          <div key={i} className="p-6 bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 space-y-4">
            <div className="flex justify-between items-center">
              <div className="h-4 bg-slate-200 dark:bg-slate-800 rounded w-24"></div>
              <div className="w-10 h-10 bg-slate-200 dark:bg-slate-800 rounded-xl"></div>
            </div>
            <div className="h-8 bg-slate-200 dark:bg-slate-800 rounded w-32"></div>
            <div className="h-3 bg-slate-200 dark:bg-slate-800 rounded w-20"></div>
          </div>
        ))}
      </div>

      {/* Charts & Analytics Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 p-6 bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 space-y-4">
          <div className="h-6 bg-slate-200 dark:bg-slate-800 rounded w-48 mb-6"></div>
          <div className="h-64 bg-slate-100 dark:bg-slate-800/60 rounded-xl w-full"></div>
        </div>
        <div className="p-6 bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 space-y-4">
          <div className="h-6 bg-slate-200 dark:bg-slate-800 rounded w-40 mb-6"></div>
          <div className="space-y-3">
            {[1, 2, 3, 4].map((i) => (
              <div key={i} className="h-14 bg-slate-100 dark:bg-slate-800/60 rounded-xl w-full"></div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
