export interface User {
  id: string
  username: string
  email: string
  role: 'operator' | 'admin'
  createdAt: string
  updatedAt: string
}

export interface LoginCredentials {
  username: string
  password: string
}

export interface RegisterData {
  username: string
  email: string
  password: string
  confirmPassword: string
}

export interface AuthResponse {
  user: User
  token: string
  expiresAt: string
}

export interface AuthContextType {
  user: User | null
  login: (credentials: LoginCredentials) => Promise<void>
  logout: () => void
  isLoading: boolean
  isAuthenticated: boolean
} 