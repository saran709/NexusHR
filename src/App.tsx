import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ThemeProvider, useTheme } from './context/ThemeContext';
import { ToastProvider } from './context/ToastContext';
import { MainLayout } from './components/layout/MainLayout';
import { ProtectedLayout, RoleBasedRoute } from './components/layout/ProtectedLayout';

import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { ForgotPassword } from './pages/ForgotPassword';
import { ResetPassword } from './pages/ResetPassword';
import { Dashboard } from './pages/Dashboard';
import { Employees } from './pages/Employees';
import { Attendance } from './pages/Attendance';
import { Leave } from './pages/Leave';
import { Payroll } from './pages/Payroll';
import { Performance } from './pages/Performance';
import { AiInsights } from './pages/AiInsights';
import { Notifications } from './pages/Notifications';
import { Profile } from './pages/Profile';
import { Settings } from './pages/Settings';

const queryClient = new QueryClient();

function ThemeObserver({ children }: { children: React.ReactNode }) {
  const { theme } = useTheme();
  
  React.useEffect(() => {
    const root = document.documentElement;
    const body = document.body;
    if (theme === 'dark') {
      root.classList.add('dark');
      body.classList.add('dark');
      root.style.colorScheme = 'dark';
    } else {
      root.classList.remove('dark');
      body.classList.remove('dark');
      root.style.colorScheme = 'light';
    }
    localStorage.setItem('nexushr_theme', theme);
  }, [theme]);

  return <>{children}</>;
}

function RootRedirect() {
  const { isAuthenticated } = useAuth();
  return <Navigate to={isAuthenticated ? "/dashboard" : "/login"} replace />;
}

export function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider>
        <ThemeObserver>
          <AuthProvider>
            <ToastProvider>
              <BrowserRouter>
                <Routes>
                  {/* Root / Entry redirect */}
                  <Route path="/" element={<RootRedirect />} />

                  {/* Public Auth Routes */}
                  <Route path="/login" element={<Login />} />
                  <Route path="/register" element={<Register />} />
                  <Route path="/forgot-password" element={<ForgotPassword />} />
                  <Route path="/reset-password" element={<ResetPassword />} />

                  {/* Protected Enterprise Routes */}
                  <Route element={<ProtectedLayout />}>
                    <Route element={<MainLayout />}>
                      <Route path="/dashboard" element={<Dashboard />} />
                      <Route path="/employees" element={<Employees />} />
                      <Route path="/attendance" element={<Attendance />} />
                      <Route path="/leave" element={<Leave />} />
                      <Route path="/payroll" element={<Payroll />} />
                      <Route path="/performance" element={<Performance />} />
                      <Route path="/ai-insights" element={<AiInsights />} />
                      <Route path="/notifications" element={<Notifications />} />
                      <Route path="/profile" element={<Profile />} />
                      <Route path="/settings" element={<Settings />} />
                    </Route>
                  </Route>

                  {/* Fallback */}
                  <Route path="*" element={<RootRedirect />} />
                </Routes>
              </BrowserRouter>
            </ToastProvider>
          </AuthProvider>
        </ThemeObserver>
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export default App;
