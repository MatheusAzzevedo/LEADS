import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { LoginCredentials, AuthResponse, Lead, DashboardStats } from '../types';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: (import.meta as any).env?.VITE_API_URL || 'http://localhost:8080/api',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Interceptor para adicionar token
    this.api.interceptors.request.use((config) => {
      const token = localStorage.getItem('auth_token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });

    // Interceptor para tratamento de erros
    this.api.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          localStorage.removeItem('auth_token');
          localStorage.removeItem('auth_user');
          localStorage.removeItem('auth_expires');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  // Autenticação
  async login(credentials: LoginCredentials): Promise<AuthResponse> {
    const response: AxiosResponse<AuthResponse> = await this.api.post('/auth/login', credentials);
    return response.data;
  }

  async validateToken(): Promise<boolean> {
    try {
      await this.api.get('/auth/validate');
      return true;
    } catch {
      return false;
    }
  }

  // Leads
  async createLead(lead: Partial<Lead>): Promise<Lead> {
    const response: AxiosResponse<Lead> = await this.api.post('/leads', lead);
    return response.data;
  }

  async getAllLeads(): Promise<Lead[]> {
    const response: AxiosResponse<Lead[]> = await this.api.get('/leads');
    return response.data;
  }

  async getLeadById(id: string): Promise<Lead> {
    const response: AxiosResponse<Lead> = await this.api.get(`/leads/${id}`);
    return response.data;
  }

  async getMyLeads(): Promise<Lead[]> {
    const response: AxiosResponse<Lead[]> = await this.api.get('/leads/my');
    return response.data;
  }

  async deleteLead(id: string): Promise<void> {
    await this.api.delete(`/leads/${id}`);
  }

  // Dashboard
  async getDashboardStats(): Promise<DashboardStats> {
    const response: AxiosResponse<DashboardStats> = await this.api.get('/dashboard/stats');
    return response.data;
  }
}

export const apiService = new ApiService();
