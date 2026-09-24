import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { User } from '../types';

interface AuthContextData {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (token: string, user: User) => void;
  logout: () => void;
  hasRole: (roles: string[]) => boolean;
}

const AuthContext = createContext<AuthContextData | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('nexushr_token'));
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('nexushr_user');
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch (e) {
        return null;
      }
    }
    // Default demo user if none stored
    return {
      id: 'd0322332-6a56-4299-8547-590059379d67',
      email: 'admin@nexushr.com',
      firstName: 'Alex',
      lastName: 'Morgan',
      role: 'HR_ADMIN',
      department: 'Human Resources',
      position: 'HR Director',
    };
  });

  useEffect(() => {
    if (token) {
      localStorage.setItem('nexushr_token', token);
    } else {
      localStorage.removeItem('nexushr_token');
    }
  }, [token]);

  useEffect(() => {
    if (user) {
      localStorage.setItem('nexushr_user', JSON.stringify(user));
    } else {
      localStorage.removeItem('nexushr_user');
    }
  }, [user]);

  const login = (newToken: string, newUser: User) => {
    setToken(newToken);
    setUser(newUser);
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('nexushr_token');
    localStorage.removeItem('nexushr_user');
  };

  const hasRole = (roles: string[]) => {
    if (!user) return false;
    if (user.role === 'SUPER_ADMIN') return true;
    return roles.includes(user.role);
  };

  return (
    <AuthContext.Provider value={{ user, token, isAuthenticated: !!user, login, logout, hasRole }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
