import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { useToast } from '../context/ToastContext';

const registerSchema = z.object({
  firstName: z.string().min(2, 'First name is required'),
  lastName: z.string().min(2, 'Last name is required'),
  email: z.string().email('Invalid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
  companyName: z.string().min(2, 'Company name is required'),
});

type RegisterFormData = z.infer<typeof registerSchema>;

export const Register: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data: RegisterFormData) => {
    setIsLoading(true);
    try {
      await new Promise((r) => setTimeout(r, 800));
      showToast('Enterprise organization registered successfully. Please sign in.', 'success');
      navigate('/login');
    } catch (err) {
      showToast('Registration failed. Please try again.', 'error');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center p-6 relative overflow-hidden">
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_70%_70%,rgba(37,99,235,0.15),transparent_50%)]"></div>

      <div className="w-full max-w-lg bg-slate-900 border border-slate-800 rounded-3xl shadow-2xl p-8 relative z-10">
        <div className="text-center mb-8">
          <div className="w-14 h-14 rounded-2xl bg-blue-600 flex items-center justify-center text-white font-bold text-2xl mx-auto mb-4 shadow-xl shadow-blue-500/30">
            N
          </div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Create Enterprise Workspace</h1>
          <p className="text-sm text-slate-400 mt-1">Get started with NexusHR SaaS platform</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input label="First Name" placeholder="Alex" error={errors.firstName?.message} {...register('firstName')} />
            <Input label="Last Name" placeholder="Morgan" error={errors.lastName?.message} {...register('lastName')} />
          </div>

          <Input label="Work Email" type="email" placeholder="alex@company.com" error={errors.email?.message} {...register('email')} />

          <Input label="Company Name" placeholder="Acme Corporation" error={errors.companyName?.message} {...register('companyName')} />

          <Input label="Password" type="password" placeholder="••••••••" error={errors.password?.message} {...register('password')} />

          <Button type="submit" isLoading={isLoading} className="w-full py-3 mt-2">
            Create Organization Workspace
          </Button>
        </form>

        <div className="mt-8 pt-6 border-t border-slate-800 text-center">
          <p className="text-sm text-slate-400">
            Already have an account?{' '}
            <Link to="/login" className="text-blue-400 font-semibold hover:text-blue-300 transition-colors">
              Sign In
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};
