import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { useToast } from '../context/ToastContext';
import { ArrowLeft } from 'lucide-react';

const schema = z.object({ email: z.string().email('Invalid email address') });

export const ForgotPassword: React.FC = () => {
  const { showToast } = useToast();
  const [isLoading, setIsLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: zodResolver(schema),
  });

  const onSubmit = async () => {
    setIsLoading(true);
    await new Promise((r) => setTimeout(r, 800));
    setIsLoading(false);
    setSubmitted(true);
    showToast('Password reset instructions sent to your email.', 'success');
  };

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center p-6 relative">
      <div className="w-full max-w-md bg-slate-900 border border-slate-800 rounded-3xl shadow-2xl p-8">
        <div className="text-center mb-6">
          <h1 className="text-2xl font-bold text-white tracking-tight">Reset Password</h1>
          <p className="text-sm text-slate-400 mt-1">Enter your work email to receive a secure recovery link</p>
        </div>

        {!submitted ? (
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            <Input label="Work Email" type="email" placeholder="name@company.com" error={errors.email?.message as string} {...register('email')} />
            <Button type="submit" isLoading={isLoading} className="w-full py-3">Send Reset Link</Button>
          </form>
        ) : (
          <div className="text-center py-4 space-y-4">
            <p className="text-sm text-emerald-400 font-medium">Check your inbox for password reset instructions.</p>
          </div>
        )}

        <div className="mt-6 text-center">
          <Link to="/login" className="inline-flex items-center space-x-2 text-sm text-blue-400 hover:text-blue-300">
            <ArrowLeft className="w-4 h-4" />
            <span>Back to Sign In</span>
          </Link>
        </div>
      </div>
    </div>
  );
};
