// Tipos de autenticação
export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export interface User {
  id: number;
  username: string;
  name: string;
}

// Tipos de Lead
export interface Lead {
  id?: number;
  leadId: string;
  operatorId: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  position: string;
  company: string;
  question1Responses?: string[];
  question2Responses?: string[];
  question3Responses?: string[];
  question4Responses?: string[];
  question5Text?: string;
  selectedPlan?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateLeadRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  position: string;
  company: string;
  question1Responses?: string[] | null;
  question2Responses?: string[] | null;
  question3Responses?: string[] | null;
  question4Responses?: string[] | null;
  question5Text?: string | null;
  selectedPlan?: string;
}

// Tipos de Plano
export interface Plan {
  id?: number;
  planId: string;
  name: string;
  description: string;
  imageUrl?: string;
  isActive: boolean;
  createdAt?: string;
}

// Tipos de Dashboard
export interface DashboardStats {
  totalLeads: number;
  leadsByPlan: Record<string, number>;
  plans: Plan[];
}

// Tipos de API
export interface ApiResponse<T> {
  data?: T;
  error?: string;
}

export interface ApiError {
  message: string;
} 