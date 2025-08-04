import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { z } from 'zod';
import { apiService } from '../../../services/api';
import { Button } from '../../../components/ui/Button';
import { Home, Plus, ArrowLeft } from 'lucide-react';

const leadDetailSchema = z.object({
  leadId: z.string(),
  firstName: z.string(),
  lastName: z.string(),
  email: z.string().email(),
  phone: z.string(),
  position: z.string(),
  company: z.string(),
  createdAt: z.string(),
  selectedPlan: z.string().optional().nullable(),
  interest: z.array(z.string()).optional().nullable(),
  hiring_flow: z.string().optional().nullable(),
  investment_value: z.string().optional().nullable(),
  eligible_for_pilot: z.boolean().optional().nullable(),
});

type LeadDetail = z.infer<typeof leadDetailSchema>;

export default function LeadDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [lead, setLead] = useState<LeadDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const handleNewLead = () => {
    navigate('/leads');
  };

  const handleGoHome = () => {
    navigate('/dashboard');
  };

  useEffect(() => {
    const fetchLead = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await apiService.getLeadById(id!);
        console.log('Lead recebido da API:', data);
        const parsed = leadDetailSchema.safeParse(data);
        if (!parsed.success) {
          setError('Dados do lead inválidos.');
          return;
        }
        setLead(parsed.data);
      } catch (err: any) {
        setError('Erro ao buscar detalhes do lead.');
      } finally {
        setLoading(false);
      }
    };
    if (id) fetchLead();
  }, [id]);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Carregando detalhes do lead...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-gray-50">
        <p className="text-red-500 mb-6">{error}</p>
        <div className="flex gap-3">
          <Button variant="outline" onClick={() => navigate(-1)} className="flex items-center space-x-2">
            <ArrowLeft className="h-4 w-4" />
            <span>Voltar</span>
          </Button>
          <Button variant="outline" onClick={handleGoHome} className="flex items-center space-x-2">
            <Home className="h-4 w-4" />
            <span>Início</span>
          </Button>
          <Button onClick={handleNewLead} className="flex items-center space-x-2">
            <Plus className="h-4 w-4" />
            <span>Novo Lead</span>
          </Button>
        </div>
      </div>
    );
      }

  if (!lead) return null;

  return (
    <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-2xl mx-auto bg-white p-8 rounded-xl shadow-lg">
        <h2 className="text-3xl font-bold mb-8 text-gray-800">Detalhes do Lead</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <div>
            <span className="font-semibold">Nome:</span> {lead.firstName} {lead.lastName}
          </div>
          <div>
            <span className="font-semibold">Email:</span> {lead.email}
          </div>
          <div>
            <span className="font-semibold">Telefone:</span> {lead.phone}
          </div>
          <div>
            <span className="font-semibold">Empresa:</span> {lead.company}
          </div>
          <div>
            <span className="font-semibold">Cargo:</span> {lead.position}
          </div>
          <div>
            <span className="font-semibold">Plano Selecionado:</span> {lead.selectedPlan || 'Não informado'}
          </div>
          <div>
            <span className="font-semibold">Data de Cadastro:</span> {new Date(lead.createdAt).toLocaleString('pt-BR')}
          </div>
        </div>
        <div className="mb-8">
          <h3 className="text-xl font-semibold mb-4 text-gray-700">Informações Adicionais</h3>
          <ul className="space-y-2">
            {lead.interest && lead.interest.length > 0 && (
              <li><span className="font-semibold">Interesse em:</span> {lead.interest.join(', ')}</li>
            )}
            {lead.hiring_flow && (
              <li><span className="font-semibold">Fluxo de Contratação:</span> {lead.hiring_flow}</li>
            )}
            {lead.investment_value && (
              <li><span className="font-semibold">Investimento Disponível:</span> {lead.investment_value}</li>
            )}
            {lead.eligible_for_pilot !== null && lead.eligible_for_pilot !== undefined && (
              <li><span className="font-semibold">Elegível para Vaga Piloto:</span> {lead.eligible_for_pilot ? 'Sim' : 'Não'}</li>
            )}
            {(!lead.interest || lead.interest.length === 0) && !lead.hiring_flow && !lead.investment_value && lead.eligible_for_pilot === null && (
              <li className="text-gray-500">Nenhuma informação adicional registrada.</li>
            )}
          </ul>
        </div>
        
        {/* Botões de Navegação */}
        <div className="flex flex-col sm:flex-row gap-3 justify-between">
          <div className="flex gap-3">
            <Button 
              variant="outline" 
              onClick={() => navigate(-1)}
              className="flex items-center space-x-2"
            >
              <ArrowLeft className="h-4 w-4" />
              <span>Voltar</span>
            </Button>
            <Button 
              variant="outline" 
              onClick={handleGoHome}
              className="flex items-center space-x-2"
            >
              <Home className="h-4 w-4" />
              <span>Início</span>
            </Button>
          </div>
          <Button 
            onClick={handleNewLead}
            className="flex items-center space-x-2"
          >
            <Plus className="h-4 w-4" />
            <span>Novo Lead</span>
          </Button>
        </div>
      </div>
    </div>
  );
} 