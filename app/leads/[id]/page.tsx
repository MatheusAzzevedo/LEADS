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
  vagaPiloto: z.boolean().optional().nullable(),
  question1Responses: z.array(z.string()).optional().nullable(),
  question2Responses: z.array(z.string()).optional().nullable(),
  question3Responses: z.array(z.string()).optional().nullable(),
  question4Responses: z.array(z.string()).optional().nullable(),
  question5Text: z.string().optional().nullable(),
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
            <span className="font-semibold">Vaga Piloto:</span> {lead.vagaPiloto ? 'Sim' : 'Não'}
          </div>
          <div>
            <span className="font-semibold">Data de Cadastro:</span> {new Date(lead.createdAt).toLocaleString('pt-BR')}
          </div>
        </div>
        <div className="mb-8">
          <h3 className="text-xl font-semibold mb-4 text-gray-700">Respostas do Formulário</h3>
          <ul className="space-y-2">
            {lead.question1Responses && lead.question1Responses.length > 0 && (
              <li><span className="font-semibold">Interesse em:</span> {lead.question1Responses.join(', ')}</li>
            )}
            {lead.question2Responses && lead.question2Responses.length > 0 && (
              <li><span className="font-semibold">Como contratam:</span> {lead.question2Responses.join(', ')}</li>
            )}
            {lead.question3Responses && lead.question3Responses.length > 0 && (
              <li><span className="font-semibold">Forma de pagamento:</span> {lead.question3Responses.join(', ')}</li>
            )}
             {lead.question4Responses && lead.question4Responses.length > 0 && (
              <li><span className="font-semibold">Investimento:</span> {lead.question4Responses.join(', ')}</li>
            )}
            {lead.question5Text && (
              <li><span className="font-semibold">Detalhes adicionais:</span> {lead.question5Text}</li>
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
