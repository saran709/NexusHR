import React from 'react';

export const SkeletonLoader: React.FC<{ rows?: number }> = ({ rows = 3 }) => {
  return (
    <div className="animate-pulse space-y-4 w-full p-6 bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800">
      <div className="h-6 bg-slate-200 dark:bg-slate-800 rounded-lg w-1/4 mb-6"></div>
      <div className="space-y-3">
        {Array.from({ length: rows }).map((_, i) => (
          <div key={i} className="h-12 bg-slate-100 dark:bg-slate-800/60 rounded-xl w-full"></div>
        ))}
      </div>
    </div>
  );
};
