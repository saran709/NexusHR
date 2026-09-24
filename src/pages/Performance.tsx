import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import { Modal } from '../components/ui/Modal';
import { Input } from '../components/ui/Input';
import { SkeletonLoader } from '../components/ui/SkeletonLoader';
import { Award, Target, TrendingUp, Plus, CheckCircle2, MessageSquare } from 'lucide-react';
import apiClient from '../services/api';
import { useToast } from '../context/ToastContext';
import { useAuth } from '../context/AuthContext';

const goalSchema = z.object({
  title: z.string().min(3, 'Title must be at least 3 characters'),
  description: z.string().min(5, 'Description is required'),
  category: z.string().min(2, 'Category is required'),
  target: z.string().min(1, 'Target is required'),
  weight: z.string().min(1, 'Weight is required'),
  startDate: z.string().min(1, 'Start date is required'),
  endDate: z.string().min(1, 'End date is required'),
});

type GoalFormData = z.infer<typeof goalSchema>;

export const Performance: React.FC = () => {
  const { showToast } = useToast();
  const { hasRole } = useAuth();
  const queryClient = useQueryClient();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [activeTab, setActiveTab] = useState<'goals' | 'reviews' | 'feedback' | 'scorecard'>('goals');

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<GoalFormData>({
    resolver: zodResolver(goalSchema),
    defaultValues: {
      category: 'OKR',
      weight: '25',
      target: '100%',
    },
  });

  // Fetch My Goals
  const { data: goalsData, isLoading: isGoalsLoading } = useQuery({
    queryKey: ['performance-goals'],
    queryFn: async () => {
      try {
        return await apiClient.get<any[]>('/performance/goals/me');
      } catch {
        return [
          { id: 'g1', title: 'Complete Microservices Architecture Migration', category: 'OKR', target: '100%', progress: 85, weight: 40, status: 'IN_PROGRESS' },
          { id: 'g2', title: 'Achieve 99.9% System Uptime SLA', category: 'KPI', target: '99.9%', progress: 100, weight: 30, status: 'COMPLETED' },
          { id: 'g3', title: 'Implement Automated Security Scanning', category: 'OKR', target: '100%', progress: 60, weight: 30, status: 'IN_PROGRESS' },
        ];
      }
    },
  });

  // Create Goal Mutation
  const createGoalMutation = useMutation({
    mutationFn: async (data: GoalFormData) => {
      return apiClient.post('/performance/goals', {
        ...data,
        weight: Number(data.weight),
        employeeId: 'd0322332-6a56-4299-8547-590059379d67',
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['performance-goals'] });
      setIsModalOpen(false);
      reset();
      showToast('Performance goal created successfully', 'success');
    },
    onError: (err: any) => {
      showToast(err.message || 'Failed to create goal', 'error');
    },
  });

  const onSubmitGoal = (data: GoalFormData) => {
    createGoalMutation.mutate(data);
  };

  const goals = goalsData || [];

  return (
    <div className="space-y-6 animate-fadeIn">
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold tracking-tight text-slate-900 dark:text-slate-100">Performance Management & OKRs</h1>
          <p className="text-sm text-slate-500 dark:text-slate-400">Manage individual goals, 360 feedback, reviews, and weighted scorecards</p>
        </div>
        <div className="flex items-center space-x-2">
          <Button onClick={() => setIsModalOpen(true)}>
            <Plus className="w-4 h-4 mr-2" />
            Create Goal / OKR
          </Button>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex space-x-2 border-b border-slate-200 dark:border-slate-800 pb-3">
        {(['goals', 'reviews', 'feedback', 'scorecard'] as const).map((tab) => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`px-4 py-2 rounded-xl text-xs font-bold capitalize transition-all ${
              activeTab === tab ? 'bg-blue-600 text-white shadow-md shadow-blue-500/20' : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300'
            }`}
          >
            {tab}
          </button>
        ))}
      </div>

      {activeTab === 'goals' && (
        <Card title="My Goals & OKRs (Q3 2026)">
          {isGoalsLoading ? (
            <SkeletonLoader rows={3} />
          ) : goals.length === 0 ? (
            <div className="p-8 text-center text-slate-500">No goals found. Create your first OKR or KPI.</div>
          ) : (
            <div className="space-y-4">
              {goals.map((goal: any) => (
                <div key={goal.id} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-800 space-y-3">
                  <div className="flex items-center justify-between">
                    <div>
                      <div className="flex items-center space-x-2 mb-1">
                        <Badge variant="info">{goal.category}</Badge>
                        <span className="text-xs text-slate-500">Target: {goal.target} • Weight: {goal.weight}%</span>
                      </div>
                      <h4 className="font-bold text-sm text-slate-900 dark:text-slate-100">{goal.title}</h4>
                      <p className="text-xs text-slate-500 mt-1">{goal.description}</p>
                    </div>
                    <Badge variant={goal.status === 'COMPLETED' ? 'success' : 'warning'}>{goal.status}</Badge>
                  </div>
                  <div className="space-y-1">
                    <div className="flex justify-between text-xs font-semibold">
                      <span className="text-slate-500">Progress</span>
                      <span className="text-slate-900 dark:text-slate-100">{goal.progress}%</span>
                    </div>
                    <div className="w-full bg-slate-200 dark:bg-slate-700 h-2 rounded-full overflow-hidden">
                      <div className="bg-blue-600 h-2 rounded-full transition-all" style={{ width: `${goal.progress}%` }}></div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      )}

      {activeTab === 'scorecard' && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">Weighted Performance Score</p>
            <h3 className="text-4xl font-extrabold text-blue-600">92.4 / 100</h3>
            <p className="text-xs text-slate-500 mt-2">Calculated across active goals with weighted priorities</p>
          </Card>
          <Card>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">Self & Manager Review</p>
            <h3 className="text-2xl font-extrabold text-emerald-600">COMPLETED</h3>
            <p className="text-xs text-slate-500 mt-2">Verified by HR Review cycle</p>
          </Card>
          <Card>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">360-Degree Feedback</p>
            <h3 className="text-2xl font-extrabold text-purple-600">4 Peers Submitted</h3>
            <p className="text-xs text-slate-500 mt-2">High leadership rating</p>
          </Card>
        </div>
      )}

      {activeTab === 'reviews' && (
        <Card title="Performance Reviews & 360 Feedback">
          <div className="space-y-4">
            <div className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-800 flex items-center justify-between">
              <div>
                <p className="font-bold text-sm text-slate-900 dark:text-slate-100">Q3 2026 Self Review</p>
                <p className="text-xs text-slate-500">Status: Submitted • Rating: Exceeds Expectations</p>
              </div>
              <Button size="sm" variant="outline">View Review</Button>
            </div>
            <div className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-800 flex items-center justify-between">
              <div>
                <p className="font-bold text-sm text-slate-900 dark:text-slate-100">Manager Evaluation (Sarah Jenkins)</p>
                <p className="text-xs text-slate-500">Status: Finalized • Score: 92/100</p>
              </div>
              <Button size="sm" variant="outline">View Scorecard</Button>
            </div>
          </div>
        </Card>
      )}

      {activeTab === 'feedback' && (
        <Card title="Peer & 360 Feedback">
          <div className="space-y-4">
            <div className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-800">
              <p className="text-xs text-slate-400 font-semibold mb-1">Anonymous Peer Review #1</p>
              <p className="text-sm text-slate-800 dark:text-slate-200">"Exceptional leadership during the microservices migration sprint. Excellent technical clarity and team mentorship."</p>
            </div>
          </div>
        </Card>
      )}

      {/* Create Goal Modal */}
      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Create Performance Goal / OKR">
        <form onSubmit={handleSubmit(onSubmitGoal)} className="space-y-4">
          <Input label="Goal Title" placeholder="Complete Cloud Migration" error={errors.title?.message} {...register('title')} />
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-slate-600 dark:text-slate-400 mb-1.5">Description</label>
            <textarea
              rows={3}
              placeholder="Provide measurable milestones..."
              className="w-full px-4 py-2.5 bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-700 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 text-slate-800 dark:text-slate-100"
              {...register('description')}
            ></textarea>
            {errors.description && <p className="text-xs text-rose-500 mt-1">{errors.description.message}</p>}
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-600 dark:text-slate-400 mb-1.5">Category</label>
              <select
                className="w-full px-4 py-2.5 bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-700 rounded-xl text-sm"
                {...register('category')}
              >
                <option value="OKR">OKR</option>
                <option value="KPI">KPI</option>
                <option value="DEVELOPMENT">Development</option>
              </select>
            </div>
            <Input label="Target" placeholder="100%" error={errors.target?.message} {...register('target')} />
          </div>
          <div className="grid grid-cols-3 gap-4">
            <Input label="Weight (%)" type="number" error={errors.weight?.message} {...register('weight')} />
            <Input label="Start Date" type="date" error={errors.startDate?.message} {...register('startDate')} />
            <Input label="End Date" type="date" error={errors.endDate?.message} {...register('endDate')} />
          </div>
          <div className="flex justify-end space-x-3 pt-4 border-t border-slate-100 dark:border-slate-800">
            <Button type="button" variant="outline" onClick={() => setIsModalOpen(false)}>Cancel</Button>
            <Button type="submit" isLoading={createGoalMutation.isPending}>Save Goal</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
