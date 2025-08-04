// Re-export all types from other files
export * from './auth';
export * from './lead';

// You can also define other shared types here if needed
export interface Plan {
  id?: number;
  planId: string;
  name: string;
  description: string;
  imageUrl?: string;
  isActive: boolean;
  createdAt?: string;
}

export interface DashboardStats {
  totalLeads: number;
  leadsByPlan: Record<string, number>;
  plans: Plan[];
}

export interface ApiResponse<T> {
  data?: T;
  error?: string;
}

export interface ApiError {
  message: string;
}
