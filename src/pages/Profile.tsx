import React from 'react';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { User, Mail, Shield, Building } from 'lucide-react';

export const Profile: React.FC = () => {
  const { user } = useAuth();
  const { showToast } = useToast();

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    showToast('Profile updated successfully', 'success');
  };

  return (
    <div className="space-y-6 animate-fadeIn max-w-4xl">
      <div>
        <h1 className="text-2xl font-extrabold tracking-tight text-slate-900 dark:text-slate-100">My Profile</h1>
        <p className="text-sm text-slate-500 dark:text-slate-400">Manage personal account details and security settings</p>
      </div>

      <Card title="Personal Information">
        <form onSubmit={handleSave} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input label="First Name" defaultValue={user?.firstName || 'Alex'} />
            <Input label="Last Name" defaultValue={user?.lastName || 'Morgan'} />
          </div>
          <Input label="Work Email" type="email" defaultValue={user?.email || 'admin@nexushr.com'} />
          <div className="grid grid-cols-2 gap-4">
            <Input label="Department" defaultValue={user?.department || 'Human Resources'} disabled />
            <Input label="Designation" defaultValue={user?.position || 'HR Director'} disabled />
          </div>
          <div className="flex justify-end pt-4 border-t border-slate-100 dark:border-slate-800">
            <Button type="submit">Save Changes</Button>
          </div>
        </form>
      </Card>
    </div>
  );
};
