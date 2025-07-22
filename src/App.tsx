import { Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider, useAuth } from '../lib/auth';
import LoginPage from '../app/(auth)/login/page';
import RegisterPage from '../app/(auth)/register/page';
import DashboardPage from '../app/dashboard/page';
import LeadsPage from '../app/leads/page';
import QuestionsPage from '../app/questions/page';
import PlansPage from '../app/plans/page';
import JobsPage from '../app/jobs/page';
import LeadDetailPage from '../app/leads/[id]/page';
import { LeadFormProvider } from '../hooks/useLeadForm';

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth();
  
  if (isLoading) {
    return <div>Carregando...</div>;
  }
  
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/leads"
        element={
          <PrivateRoute>
            <LeadsPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/leads/:id"
        element={
          <PrivateRoute>
            <LeadDetailPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/questions"
        element={
          <PrivateRoute>
            <QuestionsPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/plans"
        element={
          <PrivateRoute>
            <PlansPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/jobs"
        element={
          <PrivateRoute>
            <JobsPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/dashboard"
        element={
          <PrivateRoute>
            <DashboardPage />
          </PrivateRoute>
        }
      />
      <Route path="/" element={<Navigate to="/dashboard" />} />
    </Routes>
  );
}

function App() {
  return (
    <AuthProvider>
      <LeadFormProvider>
        <AppRoutes />
      </LeadFormProvider>
      <Toaster position="top-right" />
    </AuthProvider>
  );
}

export default App; 