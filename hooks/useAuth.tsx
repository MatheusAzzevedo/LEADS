import { useState, useEffect, createContext, useContext, ReactNode } from 'react';
import { apiService } from '../services/api';

const AuthContext = createContext<{
  user: { id: number; username: string; name: string } | null;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => void;
  isLoading: boolean;
} | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<{ id: number; username: string; name: string } | null>(null);
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
      } else {
        // Decodificar token para obter informações do usuário
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          setUser({
            id: parseInt(payload.sub),
            username: payload.username,
            name: payload.name,
          });
        } catch (error) {
          localStorage.removeItem('token');
        }
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