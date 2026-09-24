import React from 'react';
import { Sun, Moon, Bell, Search, ShieldAlert } from 'lucide-react';
import { useTheme } from '../../context/ThemeContext';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export const TopNav: React.FC = () => {
  const { theme, toggleTheme } = useTheme();
  const { user } = useAuth();
  const navigate = useNavigate();

  return (
    <header className="h-16 px-8 bg-white dark:bg-slate-900 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between sticky top-0 z-20">
      {/* Search Bar */}
      <div className="flex items-center space-x-3 w-96 bg-slate-100 dark:bg-slate-800/60 px-4 py-2 rounded-xl border border-transparent focus-within:border-blue-500 transition-all">
        <Search className="w-4 h-4 text-slate-400" />
        <input
          type="text"
          placeholder="Search employees, payroll, leave..."
          className="bg-transparent border-none focus:outline-none text-sm text-slate-800 dark:text-slate-100 placeholder-slate-400 w-full"
        />
      </div>

      {/* Right Actions */}
      <div className="flex items-center space-x-4">
        {/* Role Badge */}
        <div className="hidden md:flex items-center space-x-1.5 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-950/60 text-blue-700 dark:text-blue-300 border border-blue-200 dark:border-blue-800 text-xs font-semibold">
          <ShieldAlert className="w-3.5 h-3.5" />
          <span>{user?.role || 'HR_ADMIN'}</span>
        </div>

        {/* Theme Toggle */}
        <button
          onClick={toggleTheme}
          className="p-2.5 rounded-xl text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
          aria-label="Toggle theme"
        >
          {theme === 'dark' ? <Sun className="w-5 h-5 text-amber-400" /> : <Moon className="w-5 h-5" />}
        </button>

        {/* Notifications */}
        <button
          onClick={() => navigate('/notifications')}
          className="p-2.5 rounded-xl text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors relative"
          aria-label="Notifications"
        >
          <Bell className="w-5 h-5" />
          <span className="absolute top-2 right-2 w-2 h-2 rounded-full bg-rose-500 animate-pulse"></span>
        </button>

        {/* Profile Avatar */}
        <div
          onClick={() => navigate('/profile')}
          className="flex items-center space-x-3 cursor-pointer pl-2 border-l border-slate-200 dark:border-slate-800"
        >
          <div className="w-9 h-9 rounded-xl bg-blue-600 text-white font-bold flex items-center justify-center text-sm shadow-sm shadow-blue-500/20">
            {user?.firstName?.[0] || 'A'}
          </div>
        </div>
      </div>
    </header>
  );
};
