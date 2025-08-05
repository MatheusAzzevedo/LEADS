// Re-export all types from other files
export * from './auth';
export * from './lead';

// You can also define other shared types here if needed
export interface DashboardStats {
  totalLeads: number;
}

export interface ApiResponse<T> {
  data?: T;
  error?: string;
}

export interface ApiError {
  message: string;
}
