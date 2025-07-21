# FASE 04 - FRONTEND REACT

## Objetivo
Implementar o frontend React com TypeScript, Tailwind CSS e todas as telas do sistema MenuErh.

## 4.1 Estrutura do Frontend

```
menuerh-system/
├── app/                      # Páginas React (App Router)
│   ├── (auth)/              # Grupo de rotas de autenticação
│   │   ├── login/
│   │   │   ├── page.tsx     # Página de login
│   │   │   └── layout.tsx   # Layout da autenticação
│   │   └── register/
│   │       ├── page.tsx     # Página de registro
│   │       └── layout.tsx
│   │
│   ├── dashboard/           # Dashboard principal
│   │   ├── page.tsx
│   │   └── layout.tsx
│   │
│   ├── leads/               # Cadastro de leads
│   │   ├── page.tsx
│   │   └── layout.tsx
│   │
│   ├── plans/               # Apresentação de planos
│   │   ├── page.tsx
│   │   └── layout.tsx
│   │
│   ├── jobs/                # Publicação de vagas
│   │   ├── page.tsx
│   │   └── layout.tsx
│   │
│   └── layout.tsx           # Layout global
│
├── components/              # Componentes React
│   ├── ui/                  # Componentes de UI básicos
│   │   ├── button.tsx
│   │   ├── input.tsx
│   │   ├── modal.tsx
│   │   └── card.tsx
│   ├── layout/              # Componentes de layout
│   │   ├── sidebar.tsx
│   │   ├── header.tsx
│   │   └── main-layout.tsx
│   └── auth/                # Componentes de autenticação
│       ├── login-form.tsx
│       └── logout-button.tsx
│
├── lib/                     # Utilitários e configurações
│   ├── auth.ts              # Configuração de autenticação
│   ├── db.ts                # Conexão com API
│   ├── utils.ts             # Funções utilitárias
│   └── validations.ts       # Schemas de validação (Zod)
│
├── types/                   # Tipos TypeScript
│   ├── auth.ts
│   ├── lead.ts
│   └── plan.ts
│
├── hooks/                   # Hooks customizados
│   ├── useAuth.ts
│   ├── useLeads.ts
│   └── useWebSocket.ts
│
├── services/                # Serviços de API
│   ├── api.ts
│   ├── authService.ts
│   ├── leadService.ts
│   └── planService.ts
│
├── package.json             # Configuração Node.js
├── tailwind.config.js       # Configuração Tailwind
├── vite.config.ts           # Configuração Vite
└── tsconfig.json            # Configuração TypeScript
```

## 4.2 Configuração Inicial

### package.json
```json
{
  "name": "menuerh-frontend",
  "version": "1.0.0",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.20.1",
    "axios": "^1.6.2",
    "zod": "^3.22.4",
    "react-hook-form": "^7.48.2",
    "@hookform/resolvers": "^3.3.2",
    "socket.io-client": "^4.7.4",
    "lucide-react": "^0.294.0",
    "clsx": "^2.0.0",
    "tailwind-merge": "^2.0.0",
    "react-hot-toast": "^2.4.1"
  },
  "devDependencies": {
    "@types/react": "^18.2.45",
    "@types/react-dom": "^18.2.18",
    "@types/node": "^20.10.4",
    "@vitejs/plugin-react": "^4.2.1",
    "autoprefixer": "^10.4.16",
    "postcss": "^8.4.32",
    "tailwindcss": "^3.3.6",
    "typescript": "^5.3.3",
    "vite": "^5.0.8",
    "@typescript-eslint/eslint-plugin": "^6.14.0",
    "@typescript-eslint/parser": "^6.14.0",
    "eslint": "^8.55.0",
    "eslint-plugin-react-hooks": "^4.6.0",
    "eslint-plugin-react-refresh": "^0.4.5"
  }
}
```

### vite.config.ts
```typescript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```

### tailwind.config.js
```javascript
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#eff6ff',
          100: '#dbeafe',
          500: '#3b82f6',
          600: '#2563eb',
          700: '#1d4ed8',
          900: '#1e3a8a',
        }
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
```

## 4.3 Tipos TypeScript

### types/index.ts
```typescript
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
  question1Responses?: string[];
  question2Responses?: string[];
  question3Responses?: string[];
  question4Responses?: string[];
  question5Text?: string;
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
```

## 4.4 Serviços de API

### services/api.ts
```typescript
import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { LoginRequest, LoginResponse, Lead, CreateLeadRequest, Plan, DashboardStats } from '../types';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: '/api',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Interceptor para adicionar token
    this.api.interceptors.request.use((config) => {
      const token = localStorage.getItem('token');
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
          localStorage.removeItem('token');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  // Autenticação
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response: AxiosResponse<LoginResponse> = await this.api.post('/auth/login', credentials);
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
  async createLead(lead: CreateLeadRequest): Promise<Lead> {
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

  // Planos
  async getAllPlans(): Promise<Plan[]> {
    const response: AxiosResponse<Plan[]> = await this.api.get('/plans');
    return response.data;
  }

  // Dashboard
  async getDashboardStats(): Promise<DashboardStats> {
    const response: AxiosResponse<DashboardStats> = await this.api.get('/dashboard/stats');
    return response.data;
  }
}

export const apiService = new ApiService();
```

## 4.5 Hooks Customizados

### hooks/useAuth.ts
```typescript
import { useState, useEffect, createContext, useContext } from 'react';
import { apiService } from '../services/api';
import { User } from '../types';

interface AuthContextType {
  user: User | null;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => void;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    const token = localStorage.getItem('token');
    if (token) {
      const isValid = await apiService.validateToken();
      if (!isValid) {
        localStorage.removeItem('token');
      }
    }
    setIsLoading(false);
  };

  const login = async (username: string, password: string): Promise<boolean> => {
    try {
      const response = await apiService.login({ username, password });
      localStorage.setItem('token', response.token);
      
      // Decodificar token para obter informações do usuário
      const payload = JSON.parse(atob(response.token.split('.')[1]));
      setUser({
        id: parseInt(payload.sub),
        username: payload.username,
        name: payload.name,
      });
      
      return true;
    } catch (error) {
      return false;
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
```

### hooks/useWebSocket.ts
```typescript
import { useEffect, useRef } from 'react';
import { io, Socket } from 'socket.io-client';

export function useWebSocket(url: string, onMessage?: (data: any) => void) {
  const socketRef = useRef<Socket | null>(null);

  useEffect(() => {
    socketRef.current = io(url);

    if (onMessage) {
      socketRef.current.on('message', onMessage);
    }

    return () => {
      if (socketRef.current) {
        socketRef.current.disconnect();
      }
    };
  }, [url, onMessage]);

  const sendMessage = (data: any) => {
    if (socketRef.current) {
      socketRef.current.emit('message', data);
    }
  };

  return { sendMessage };
}
```

## 4.6 Componentes de UI

### components/ui/Button.tsx
```typescript
import React from 'react';
import { clsx } from 'clsx';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
  loading?: boolean;
}

export const Button: React.FC<ButtonProps> = ({
  children,
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled,
  className,
  ...props
}) => {
  const baseClasses = 'inline-flex items-center justify-center font-medium rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2';
  
  const variantClasses = {
    primary: 'bg-primary-600 text-white hover:bg-primary-700 focus:ring-primary-500',
    secondary: 'bg-gray-600 text-white hover:bg-gray-700 focus:ring-gray-500',
    outline: 'border border-gray-300 text-gray-700 hover:bg-gray-50 focus:ring-primary-500',
    ghost: 'text-gray-700 hover:bg-gray-100 focus:ring-primary-500',
  };

  const sizeClasses = {
    sm: 'px-3 py-1.5 text-sm',
    md: 'px-4 py-2 text-sm',
    lg: 'px-6 py-3 text-base',
  };

  return (
    <button
      className={clsx(
        baseClasses,
        variantClasses[variant],
        sizeClasses[size],
        (disabled || loading) && 'opacity-50 cursor-not-allowed',
        className
      )}
      disabled={disabled || loading}
      {...props}
    >
      {loading && (
        <svg className="animate-spin -ml-1 mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
        </svg>
      )}
      {children}
    </button>
  );
};
```

### components/ui/Input.tsx
```typescript
import React from 'react';
import { clsx } from 'clsx';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
}

export const Input: React.FC<InputProps> = ({
  label,
  error,
  className,
  ...props
}) => {
  return (
    <div className="space-y-1">
      {label && (
        <label className="block text-sm font-medium text-gray-700">
          {label}
        </label>
      )}
      <input
        className={clsx(
          'block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500',
          error && 'border-red-300 focus:ring-red-500 focus:border-red-500',
          className
        )}
        {...props}
      />
      {error && (
        <p className="text-sm text-red-600">{error}</p>
      )}
    </div>
  );
};
```

## 4.7 Páginas Principais

### pages/LoginPage.tsx
```typescript
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { toast } from 'react-hot-toast';

export const LoginPage: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      const success = await login(username, password);
      if (success) {
        toast.success('Login realizado com sucesso!');
        navigate('/leads');
      } else {
        toast.error('Credenciais inválidas');
      }
    } catch (error) {
      toast.error('Erro ao fazer login');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            MenuErh
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Sistema de Gestão de Leads
          </p>
        </div>
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="space-y-4">
            <Input
              label="Usuário"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              placeholder="operador1"
            />
            <Input
              label="Senha"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              placeholder="admin123"
            />
          </div>
          <Button
            type="submit"
            loading={loading}
            className="w-full"
          >
            Entrar
          </Button>
        </form>
      </div>
    </div>
  );
};
```

### pages/LeadRegistrationPage.tsx
```typescript
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { apiService } from '../services/api';
import { toast } from 'react-hot-toast';

const leadSchema = z.object({
  firstName: z.string().min(1, 'Nome é obrigatório'),
  lastName: z.string().min(1, 'Sobrenome é obrigatório'),
  email: z.string().email('Email inválido'),
  phone: z.string().min(1, 'Telefone é obrigatório'),
  position: z.string().min(1, 'Cargo é obrigatório'),
  company: z.string().min(1, 'Empresa é obrigatória'),
});

type LeadFormData = z.infer<typeof leadSchema>;

export const LeadRegistrationPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LeadFormData>({
    resolver: zodResolver(leadSchema),
  });

  const onSubmit = async (data: LeadFormData) => {
    setLoading(true);
    try {
      await apiService.createLead(data);
      toast.success('Lead cadastrado com sucesso!');
      navigate('/questions');
    } catch (error) {
      toast.error('Erro ao cadastrar lead');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-2xl mx-auto">
        <div className="bg-white p-8 rounded-xl shadow-lg">
          <h2 className="text-2xl font-bold mb-6 text-center text-gray-700">
            1. Cadastro do Lead
          </h2>
          
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Nome"
                {...register('firstName')}
                error={errors.firstName?.message}
                placeholder="João"
              />
              <Input
                label="Sobrenome"
                {...register('lastName')}
                error={errors.lastName?.message}
                placeholder="Silva"
              />
            </div>
            
            <Input
              label="Email"
              type="email"
              {...register('email')}
              error={errors.email?.message}
              placeholder="joao@email.com"
            />
            
            <Input
              label="Telefone"
              {...register('phone')}
              error={errors.phone?.message}
              placeholder="(11) 99999-9999"
            />
            
            <Input
              label="Cargo"
              {...register('position')}
              error={errors.position?.message}
              placeholder="Gerente"
            />
            
            <Input
              label="Empresa"
              {...register('company')}
              error={errors.company?.message}
              placeholder="Empresa ABC"
            />
            
            <Button
              type="submit"
              loading={loading}
              className="w-full"
            >
              Próximo
            </Button>
          </form>
        </div>
      </div>
    </div>
  );
};
```

## 4.8 App Principal

### App.tsx
```typescript
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider, useAuth } from './hooks/useAuth';
import { LoginPage } from './pages/LoginPage';
import { LeadRegistrationPage } from './pages/LeadRegistrationPage';
import { QuestionsPage } from './pages/QuestionsPage';
import { PlansPage } from './pages/PlansPage';
import { DashboardPage } from './pages/DashboardPage';
import { Layout } from './components/layout/Layout';

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const { user, isLoading } = useAuth();
  
  if (isLoading) {
    return <div>Carregando...</div>;
  }
  
  return user ? <>{children}</> : <Navigate to="/login" />;
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/leads"
        element={
          <PrivateRoute>
            <Layout>
              <LeadRegistrationPage />
            </Layout>
          </PrivateRoute>
        }
      />
      <Route
        path="/questions"
        element={
          <PrivateRoute>
            <Layout>
              <QuestionsPage />
            </Layout>
          </PrivateRoute>
        }
      />
      <Route
        path="/plans"
        element={
          <PrivateRoute>
            <Layout>
              <PlansPage />
            </Layout>
          </PrivateRoute>
        }
      />
      <Route
        path="/dashboard"
        element={
          <PrivateRoute>
            <Layout>
              <DashboardPage />
            </Layout>
          </PrivateRoute>
        }
      />
      <Route path="/" element={<Navigate to="/leads" />} />
    </Routes>
  );
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <AppRoutes />
        <Toaster position="top-right" />
      </Router>
    </AuthProvider>
  );
}

export default App;
```

## 4.9 Comandos de Execução

```bash
# Instalar dependências
npm install

# Executar em desenvolvimento
npm run dev

# Build para produção
npm run build

# Preview do build
npm run preview
```

## 4.10 Próximos Passos
- [ ] Implementar QuestionsPage
- [ ] Implementar PlansPage com carrossel
- [ ] Implementar DashboardPage
- [ ] Adicionar validação de formulários
- [ ] Implementar WebSocket para tempo real
- [ ] Adicionar testes unitários
- [ ] Otimizar performance

## Tempo Estimado: 8-10 horas 