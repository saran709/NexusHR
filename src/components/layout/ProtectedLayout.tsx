import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export const ProtectedLayout: React.FC = () => {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
};

interface RoleBasedRouteProps {
  allowedRoles: string[];
}

export const RoleBasedRoute: React.FC<RoleBasedRouteProps> = ({ allowedRoles }) => {
  const { hasRole } = useAuth();

  if (!hasRole(allowedRoles)) {
    return (
      <div className="flex flex-col items-center justify-center h-[70vh] text-center p-8">
        <div className="w-16 h-16 rounded-2xl bg-rose-50 dark:bg-rose-950/60 text-rose-600 dark:text-rose-400 flex items-center justify-center font-bold text-2xl mb-4 border border-rose-200 dark:border-rose-800">
          403
        </div>
        <h2 className="text-2xl font-bold text-slate-900 dark:text-slate-100 mb-2">Access Denied</h2>
        <p className="text-slate-500 dark:text-slate-400 max-w-md mb-6">
          You do not have the required permissions (`{allowedRoles.join(', ')}`) to access this enterprise module.
        </p>
      </div>
    );
  }

  return <Outlet />;
};
