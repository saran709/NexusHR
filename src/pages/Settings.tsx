import React from 'react';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { useToast } from '../context/ToastContext';

export const Settings: React.FC = () => {
  const { showToast } = useToast();

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    showToast('Enterprise settings saved successfully', 'success');
  };

  return (
    <div className="space-y-6 animate-fadeIn max-w-4xl">
      <div>
        <h1 className="text-2xl font-extrabold tracking-tight text-slate-900 dark:text-slate-100">Enterprise Settings</h1>
        <p className="text-sm text-slate-500 dark:text-slate-400">Manage organization configuration, tax rules, and security policies</p>
      </div>

      <Card title="Company Details">
        <form onSubmit={handleSave} className="space-y-4">
          <Input label="Company Name" defaultValue="NexusHR Technologies Inc." />
          <Input label="Tax ID / EIN" defaultValue="XX-XXXXXXX" />
          <Input label="HQ Address" defaultValue="100 Innovation Way, Silicon Valley, CA" />
          <div className="flex justify-end pt-4 border-t border-slate-100 dark:border-slate-800">
            <Button type="submit">Save Settings</Button>
          </div>
        </form>
      </Card>
    </div>
  );
};
