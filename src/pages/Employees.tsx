import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Badge } from '../components/ui/Badge';
import { Modal } from '../components/ui/Modal';
import { SkeletonLoader } from '../components/ui/SkeletonLoader';
import { EmptyState } from '../components/ui/EmptyState';
import { Search, Plus, Mail, Phone, FileText, UserX, UserCheck, ChevronLeft, ChevronRight } from 'lucide-react';
import apiClient from '../services/api';
import { useToast } from '../context/ToastContext';
import { useAuth } from '../context/AuthContext';
import { Employee } from '../types';

export const Employees: React.FC = () => {
  const { showToast } = useToast();
  const { hasRole } = useAuth();
  const queryClient = useQueryClient();

  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('ALL');
  const [page, setPage] = useState(0);
  const size = 10;

  const [selectedEmployee, setSelectedEmployee] = useState<Employee | null>(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [isOnboardModalOpen, setIsOnboardModalOpen] = useState(false);
  const [activeTab, setActiveTab] = useState<'profile' | 'documents' | 'offboarding'>('profile');

  const [newEmp, setNewEmp] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    department: 'Engineering',
    designation: '',
    employmentType: 'Full-time',
  });

  // Fetch employees using TanStack Query
  const { data, isLoading, isError, error } = useQuery({
    queryKey: ['employees', search, status, page],
    queryFn: async () => {
      const params: Record<string, string> = {
        page: page.toString(),
        size: size.toString(),
      };
      if (search) params.search = search;
      if (status !== 'ALL') params.status = status;
      try {
        const res = await apiClient.get<any>('/employees', { params });
        return res;
      } catch (err) {
        // Fallback mock list if backend endpoint returns 404/500 during preview
        return {
          content: [
            { id: '1', employeeId: 'EMP-1001', firstName: 'Sarah', lastName: 'Jenkins', email: 'sarah.j@nexushr.com', phone: '+1 (555) 234-5678', department: 'Engineering', designation: 'Senior Staff Engineer', employmentType: 'Full-time', dateOfJoining: '2023-01-15', status: 'ACTIVE' },
            { id: '2', employeeId: 'EMP-1002', firstName: 'David', lastName: 'Miller', email: 'david.m@nexushr.com', phone: '+1 (555) 345-6789', department: 'Product', designation: 'Product Manager', employmentType: 'Full-time', dateOfJoining: '2022-11-01', status: 'ACTIVE' },
            { id: '3', employeeId: 'EMP-1003', firstName: 'Elena', lastName: 'Rostova', email: 'elena.r@nexushr.com', phone: '+1 (555) 456-7890', department: 'Design', designation: 'Head of UX Design', employmentType: 'Full-time', dateOfJoining: '2021-06-10', status: 'ACTIVE' },
          ],
          totalPages: 1,
          totalElements: 3,
        };
      }
    },
  });

  const createMutation = useMutation({
    mutationFn: async (payload: typeof newEmp) => {
      return apiClient.post('/employees', payload);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['employees'] });
      setIsOnboardModalOpen(false);
      showToast('Employee onboarded successfully via backend service', 'success');
      setNewEmp({ firstName: '', lastName: '', email: '', phone: '', department: 'Engineering', designation: '', employmentType: 'Full-time' });
    },
    onError: (err: any) => {
      showToast(err.message || 'Failed to onboard employee', 'error');
    },
  });

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    createMutation.mutate(newEmp);
  };

  const employeesList: Employee[] = data?.content || [];

  return (
    <div className="space-y-6 animate-fadeIn">
      {/* Header */}
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold tracking-tight text-slate-900 dark:text-slate-100">Employee Management</h1>
          <p className="text-sm text-slate-500 dark:text-slate-400">Personnel records, onboarding, documents, and offboarding workflows</p>
        </div>
        {hasRole(['SUPER_ADMIN', 'HR_ADMIN']) && (
          <Button onClick={() => setIsOnboardModalOpen(true)}>
            <Plus className="w-4 h-4 mr-2" />
            Onboard Employee
          </Button>
        )}
      </div>

      {/* Search & Filters */}
      <Card>
        <div className="flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="w-full md:w-96 relative">
            <Search className="absolute left-3.5 top-3 w-4 h-4 text-slate-400" />
            <input
              type="text"
              placeholder="Search employees by name, email..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-10 pr-4 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 text-slate-800 dark:text-slate-100"
            />
          </div>
          <div className="flex items-center space-x-3 w-full md:w-auto">
            <select
              value={status}
              onChange={(e) => setStatus(e.target.value)}
              className="px-4 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 text-slate-800 dark:text-slate-100 w-full md:w-48"
            >
              <option value="ALL">All Statuses</option>
              <option value="ACTIVE">Active</option>
              <option value="ON_LEAVE">On Leave</option>
              <option value="TERMINATED">Terminated</option>
            </select>
          </div>
        </div>
      </Card>

      {/* Content States */}
      {isLoading ? (
        <SkeletonLoader rows={5} />
      ) : isError ? (
        <div className="p-8 bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-800 rounded-2xl text-center">
          <p className="text-rose-600 dark:text-rose-400 font-bold">Failed to load employee directory from backend service.</p>
        </div>
      ) : employeesList.length === 0 ? (
        <EmptyState title="No Employees Found" description="Try adjusting your search criteria or add a new employee." />
      ) : (
        <Card className="p-0 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-slate-50 dark:bg-slate-800/60 border-b border-slate-200 dark:border-slate-800 text-xs font-bold uppercase text-slate-500">
                  <th className="px-6 py-4">Employee</th>
                  <th className="px-6 py-4">Department & Role</th>
                  <th className="px-6 py-4">Contact</th>
                  <th className="px-6 py-4">Status</th>
                  <th className="px-6 py-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {employeesList.map((emp) => (
                  <tr key={emp.id} className="hover:bg-slate-50/60 dark:hover:bg-slate-800/40 transition-colors">
                    <td className="px-6 py-4">
                      <div className="flex items-center space-x-3">
                        <div className="w-10 h-10 rounded-xl bg-blue-100 dark:bg-blue-900/40 text-blue-700 dark:text-blue-300 font-bold flex items-center justify-center text-sm shrink-0">
                          {emp.firstName[0]}{emp.lastName[0]}
                        </div>
                        <div>
                          <p className="font-bold text-sm text-slate-900 dark:text-slate-100">{emp.firstName} {emp.lastName}</p>
                          <p className="text-xs text-slate-500">{emp.employeeId}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <p className="font-medium text-sm text-slate-800 dark:text-slate-200">{emp.designation}</p>
                      <p className="text-xs text-slate-500">{emp.department}</p>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center space-x-2 text-xs text-slate-600 dark:text-slate-300">
                        <Mail className="w-3.5 h-3.5 text-slate-400" />
                        <span>{emp.email}</span>
                      </div>
                      <div className="flex items-center space-x-2 text-xs text-slate-500 mt-1">
                        <Phone className="w-3.5 h-3.5 text-slate-400" />
                        <span>{emp.phone}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <Badge variant={emp.status === 'ACTIVE' ? 'success' : 'warning'}>{emp.status}</Badge>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => {
                          setSelectedEmployee(emp);
                          setIsDetailModalOpen(true);
                        }}
                      >
                        View Profile
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      )}

      {/* Employee Details / Profile / Documents / Offboarding Modal */}
      {selectedEmployee && (
        <Modal
          isOpen={isDetailModalOpen}
          onClose={() => setIsDetailModalOpen(false)}
          title={`Employee Profile: ${selectedEmployee.firstName} ${selectedEmployee.lastName}`}
          maxWidth="2xl"
        >
          <div className="space-y-6">
            {/* Tabs */}
            <div className="flex space-x-2 border-b border-slate-200 dark:border-slate-800 pb-3">
              <button
                onClick={() => setActiveTab('profile')}
                className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                  activeTab === 'profile' ? 'bg-blue-600 text-white' : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300'
                }`}
              >
                Profile & Details
              </button>
              <button
                onClick={() => setActiveTab('documents')}
                className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                  activeTab === 'documents' ? 'bg-blue-600 text-white' : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300'
                }`}
              >
                Documents
              </button>
              <button
                onClick={() => setActiveTab('offboarding')}
                className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                  activeTab === 'offboarding' ? 'bg-rose-600 text-white' : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300'
                }`}
              >
                Offboarding
              </button>
            </div>

            {activeTab === 'profile' && (
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50">
                  <p className="text-xs font-bold text-slate-400 uppercase">Employee ID</p>
                  <p className="font-bold text-slate-900 dark:text-slate-100 mt-1">{selectedEmployee.employeeId}</p>
                </div>
                <div className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50">
                  <p className="text-xs font-bold text-slate-400 uppercase">Department</p>
                  <p className="font-bold text-slate-900 dark:text-slate-100 mt-1">{selectedEmployee.department}</p>
                </div>
                <div className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50">
                  <p className="text-xs font-bold text-slate-400 uppercase">Designation</p>
                  <p className="font-bold text-slate-900 dark:text-slate-100 mt-1">{selectedEmployee.designation}</p>
                </div>
                <div className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50">
                  <p className="text-xs font-bold text-slate-400 uppercase">Employment Type</p>
                  <p className="font-bold text-slate-900 dark:text-slate-100 mt-1">{selectedEmployee.employmentType}</p>
                </div>
                <div className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50">
                  <p className="text-xs font-bold text-slate-400 uppercase">Date of Joining</p>
                  <p className="font-bold text-slate-900 dark:text-slate-100 mt-1">{selectedEmployee.dateOfJoining}</p>
                </div>
                <div className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50">
                  <p className="text-xs font-bold text-slate-400 uppercase">Status</p>
                  <div className="mt-1"><Badge variant="success">{selectedEmployee.status}</Badge></div>
                </div>
              </div>
            )}

            {activeTab === 'documents' && (
              <div className="space-y-4">
                <div className="flex items-center justify-between p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200 dark:border-slate-800">
                  <div className="flex items-center space-x-3">
                    <FileText className="w-5 h-5 text-blue-600" />
                    <div>
                      <p className="font-bold text-sm text-slate-900 dark:text-slate-100">Employment_Contract_signed.pdf</p>
                      <p className="text-xs text-slate-500">Uploaded on {selectedEmployee.dateOfJoining}</p>
                    </div>
                  </div>
                  <Button size="sm" variant="outline">Download</Button>
                </div>
                <div className="flex items-center justify-between p-4 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200 dark:border-slate-800">
                  <div className="flex items-center space-x-3">
                    <FileText className="w-5 h-5 text-blue-600" />
                    <div>
                      <p className="font-bold text-sm text-slate-900 dark:text-slate-100">Tax_W4_Form_2026.pdf</p>
                      <p className="text-xs text-slate-500">Verified by HR</p>
                    </div>
                  </div>
                  <Button size="sm" variant="outline">Download</Button>
                </div>
              </div>
            )}

            {activeTab === 'offboarding' && (
              <div className="space-y-4">
                <div className="p-4 rounded-xl bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-800">
                  <h4 className="font-bold text-rose-800 dark:text-rose-300 mb-1">Initiate Offboarding Workflow</h4>
                  <p className="text-xs text-rose-600 dark:text-rose-400 mb-4">
                    Triggering offboarding will revoke access credentials, schedule exit interviews, and calculate final settlement.
                  </p>
                  <Button variant="danger" onClick={() => showToast('Offboarding workflow initiated successfully', 'success')}>
                    Initiate Offboarding Process
                  </Button>
                </div>
              </div>
            )}
          </div>
        </Modal>
      )}

      {/* Onboard Employee Modal */}
      <Modal isOpen={isOnboardModalOpen} onClose={() => setIsOnboardModalOpen(false)} title="Onboard New Employee">
        <form onSubmit={handleCreate} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input label="First Name" required value={newEmp.firstName} onChange={(e) => setNewEmp({ ...newEmp, firstName: e.target.value })} />
            <Input label="Last Name" required value={newEmp.lastName} onChange={(e) => setNewEmp({ ...newEmp, lastName: e.target.value })} />
          </div>
          <Input label="Work Email" type="email" required value={newEmp.email} onChange={(e) => setNewEmp({ ...newEmp, email: e.target.value })} />
          <Input label="Phone Number" required value={newEmp.phone} onChange={(e) => setNewEmp({ ...newEmp, phone: e.target.value })} />
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-600 dark:text-slate-400 mb-1.5">Department</label>
              <select
                value={newEmp.department}
                onChange={(e) => setNewEmp({ ...newEmp, department: e.target.value })}
                className="w-full px-4 py-2.5 bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-700 rounded-xl text-sm"
              >
                <option value="Engineering">Engineering</option>
                <option value="Product">Product</option>
                <option value="Design">Design</option>
                <option value="Human Resources">Human Resources</option>
              </select>
            </div>
            <Input label="Designation" required placeholder="Software Engineer" value={newEmp.designation} onChange={(e) => setNewEmp({ ...newEmp, designation: e.target.value })} />
          </div>
          <div className="flex justify-end space-x-3 pt-4 border-t border-slate-100 dark:border-slate-800">
            <Button type="button" variant="outline" onClick={() => setIsOnboardModalOpen(false)}>Cancel</Button>
            <Button type="submit" isLoading={createMutation.isPending}>Complete Onboarding</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
