import axios from 'axios'
import { LoginCredentials, AuthResponse, User } from '../types/auth'

const isLocal = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
const API_BASE_URL = isLocal ? 'http://localhost:8080/api' : '/api'

// Configurar axios com interceptors para token
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Interceptor para adicionar token em todas as requisições
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Interceptor para tratar erros de autenticação
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('auth_token')
      localStorage.removeItem('auth_expires')
      localStorage.removeItem('auth_user')
      // Não redirecionar automaticamente, deixar o componente lidar
    }
    return Promise.reject(error)
  }
)

export const authService = {
  async login(credentials: LoginCredentials): Promise<AuthResponse> {
    try {
      const response = await api.post('/auth/login', {
        username: credentials.username,
        password: credentials.password
      })
      
      const { token } = response.data
      
      // Criar objeto user baseado no token JWT
      const user = {
        id: '1',
        username: credentials.username,
        email: credentials.username + '@menuerh.com',
        role: 'operator' as const,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
      
      localStorage.setItem('auth_token', token)
      localStorage.setItem('auth_user', JSON.stringify(user))
      
      return {
        token,
        user,
        expiresAt: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString() // 24 horas
      }
    } catch (error) {
      throw new Error('Credenciais inválidas')
    }
  },

  async validateToken(): Promise<User> {
    // Por enquanto, retornar o usuário do localStorage
    const userStr = localStorage.getItem('auth_user')
    if (userStr) {
      return JSON.parse(userStr)
    }
    throw new Error('Token inválido')
  },

  async logout(): Promise<void> {
    try {
      await api.post('/api/auth/logout')
    } catch (error) {
      // Ignorar erro de logout, apenas limpar local storage
    } finally {
      localStorage.removeItem('auth_token')
      localStorage.removeItem('auth_expires')
    }
  },

  async register(userData: any): Promise<AuthResponse> {
    const response = await api.post('/api/auth/register', userData)
    return response.data
  },
} 