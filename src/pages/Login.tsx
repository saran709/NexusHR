import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { ShieldCheck, Lock, Mail } from 'lucide-react';

const loginSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

type LoginFormData = z.infer<typeof loginSchema>;

export const Login: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const { showToast } = useToast();
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: 'admin@nexushr.com', password: 'password123' },
  });

  const onSubmit = async (data: LoginFormData) => {
    setIsLoading(true);
    try {
      // Simulate API login authentication
      await new Promise((r) => setTimeout(r, 800));
      const mockToken = 'mock-jwt-token-nexushr-12345';
      const mockUser = {
        id: 'd0322332-6a56-4299-8547-590059379d67',
        email: data.email,
        firstName: data.email.includes('admin') ? 'Alex' : 'Jordan',
        lastName: 'Morgan',
        role: data.email.includes('admin') ? ('HR_ADMIN' as const) : ('EMPLOYEE' as const),
        department: 'Human Resources',
        position: 'HR Director',
      };
      login(mockToken, mockUser);
      showToast('Successfully logged into NexusHR Enterprise SaaS', 'success');
      navigate('/dashboard');
    } catch (err) {
      showToast('Invalid credentials. Please try again.', 'error');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center p-6 relative overflow-hidden">
      {/* Background decoration */}
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_30%_30%,rgba(37,99,235,0.15),transparent_50%)]"></div>
      
      <div className="w-full max-w-md bg-slate-900 border border-slate-800 rounded-3xl shadow-2xl p-8 relative z-10">
        <div className="text-center mb-8">
          <div className="w-14 h-14 rounded-2xl bg-blue-600 flex items-center justify-center text-white font-bold text-2xl mx-auto mb-4 shadow-xl shadow-blue-500/30">
            N
          </div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Welcome to NexusHR</h1>
          <p className="text-sm text-slate-400 mt-1">Enterprise Human Resource SaaS Platform</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          <div className="space-y-1">
            <Input
              label="Work Email"
              type="email"
              placeholder="name@nexushr.com"
              error={errors.email?.message}
              {...register('email')}
            />
          </div>

          <div className="space-y-1">
            <div className="flex items-center justify-between">
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400">Password</label>
              <Link to="/forgot-password" className="text-xs text-blue-400 hover:text-blue-300 transition-colors">
                Forgot password?
              </Link>
            </div>
            <Input
              type="password"
              placeholder="••••••••"
              error={errors.password?.message}
              {...register('password')}
            />
          </div>

          <Button type="submit" isLoading={isLoading} className="w-full py-3">
            Sign In to Dashboard
          </Button>
        </form>

        <div className="mt-8 pt-6 border-t border-slate-800 text-center">
          <p className="text-sm text-slate-400">
            Don't have an enterprise account?{' '}
            <Link to="/register" className="text-blue-400 font-semibold hover:text-blue-300 transition-colors">
              Request Access
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};
