import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '../../components/ui/Button';
import { apiService } from '../../services/api';
import { Plan } from '../../types';
import { toast } from 'react-hot-toast';
import { ChevronLeft, ChevronRight, Check } from 'lucide-react';
import { useLeadForm } from '../../hooks/useLeadForm';

export default function PlansPage() {
  const [plans, setPlans] = useState<Plan[]>([]);
  const [selectedPlan, setSelectedPlan] = useState<string>('');
  const [currentIndex, setCurrentIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const { leadForm, setLeadForm, resetLeadForm } = useLeadForm();

  useEffect(() => {
    loadPlans();
  }, []);

  const loadPlans = async () => {
    try {
      const plansData = await apiService.getAllPlans();
      setPlans(plansData);
    } catch (error) {
      // Fallback para dados de exemplo se a API não estiver disponível
      setPlans([
        {
          planId: 'basic',
          name: 'Plano Básico',
          description: 'Ideal para pequenas empresas que estão começando',
          isActive: true,
          imageUrl: '/images/basic-plan.jpg'
        },
        {
          planId: 'pro',
          name: 'Plano Pro',
          description: 'Perfeito para empresas em crescimento',
          isActive: true,
          imageUrl: '/images/pro-plan.jpg'
        },
        {
          planId: 'enterprise',
          name: 'Plano Enterprise',
          description: 'Solução completa para grandes empresas',
          isActive: true,
          imageUrl: '/images/enterprise-plan.jpg'
        }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const nextSlide = () => {
    setCurrentIndex((prevIndex) => 
      prevIndex === plans.length - 1 ? 0 : prevIndex + 1
    );
  };

  const prevSlide = () => {
    setCurrentIndex((prevIndex) => 
      prevIndex === 0 ? plans.length - 1 : prevIndex - 1
    );
  };

  const handlePlanSelect = (planId: string) => {
    setSelectedPlan(planId);
    setLeadForm({ selectedPlan: planId });
  };

  const handleSubmit = async () => {
    if (!selectedPlan) {
      toast.error('Por favor, selecione um plano');
      return;
    }
    try {
      setLeadForm({ selectedPlan });
      // Montar payload compatível com o backend
      const payload = {
        firstName: leadForm.firstName,
        lastName: leadForm.lastName,
        email: leadForm.email,
        phone: leadForm.phone,
        position: leadForm.position,
        company: leadForm.company,
        question1Responses: leadForm.question1Responses.length > 0 ? leadForm.question1Responses : null,
        question2Responses: leadForm.question2Responses.length > 0 ? leadForm.question2Responses : null,
        question3Responses: leadForm.question3Responses.length > 0 ? leadForm.question3Responses : null,
        question4Responses: leadForm.question4Responses.length > 0 ? leadForm.question4Responses : null,
        question5Text: leadForm.question5Text && leadForm.question5Text.trim() !== '' ? leadForm.question5Text : null,
        selectedPlan: selectedPlan,
      };
      console.log('Payload enviado para API:', payload);
      const response = await apiService.createLead(payload);
      toast.success('Lead cadastrado com sucesso!');
      resetLeadForm();
      navigate(`/leads/${response.leadId}`);
    } catch (error) {
      toast.error('Erro ao cadastrar lead');
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Carregando planos...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto">
        <div className="text-center mb-8">
          <h2 className="text-3xl font-bold text-gray-900 mb-4">
            3. Escolha seu Plano
          </h2>
          <p className="text-lg text-gray-600">
            Selecione o plano que melhor atende às suas necessidades
          </p>
        </div>

        <div className="relative">
          {/* Carrossel */}
          <div className="overflow-hidden rounded-xl">
            <div 
              className="flex transition-transform duration-300 ease-in-out"
              style={{ transform: `translateX(-${currentIndex * 100}%)` }}
            >
              {plans.map((plan) => (
                <div key={plan.planId} className="w-full flex-shrink-0">
                  <div className="bg-white p-8 rounded-xl shadow-lg mx-4">
                    <div className="text-center">
                      <h3 className="text-2xl font-bold text-gray-900 mb-4">
                        {plan.name}
                      </h3>
                      <p className="text-gray-600 mb-6">
                        {plan.description}
                      </p>
                      
                      {/* Features do plano */}
                      <div className="space-y-3 mb-8">
                        {getPlanFeatures(plan.planId).map((feature) => (
                          <div key={feature} className="flex items-center justify-center space-x-2">
                            <Check className="h-5 w-5 text-green-500" />
                            <span className="text-gray-700">{feature}</span>
                          </div>
                        ))}
                      </div>

                      {/* Botão de seleção */}
                      <Button
                        variant={selectedPlan === plan.planId ? 'primary' : 'outline'}
                        onClick={() => handlePlanSelect(plan.planId)}
                        className="w-full"
                      >
                        {selectedPlan === plan.planId ? 'Selecionado' : 'Selecionar'}
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Navegação do carrossel */}
          <button
            onClick={prevSlide}
            className="absolute left-0 top-1/2 transform -translate-y-1/2 bg-white p-2 rounded-full shadow-lg hover:bg-gray-50"
          >
            <ChevronLeft className="h-6 w-6 text-gray-600" />
          </button>
          
          <button
            onClick={nextSlide}
            className="absolute right-0 top-1/2 transform -translate-y-1/2 bg-white p-2 rounded-full shadow-lg hover:bg-gray-50"
          >
            <ChevronRight className="h-6 w-6 text-gray-600" />
          </button>

          {/* Indicadores */}
          <div className="flex justify-center mt-6 space-x-2">
            {plans.map((_, index) => (
              <button
                key={index}
                onClick={() => setCurrentIndex(index)}
                className={`w-3 h-3 rounded-full ${
                  index === currentIndex ? 'bg-primary-600' : 'bg-gray-300'
                }`}
              />
            ))}
          </div>
        </div>

        {/* Botão de continuar */}
        <div className="mt-8 text-center">
          <Button
            onClick={handleSubmit}
            disabled={!selectedPlan}
            className="px-8 py-3"
          >
            Continuar
          </Button>
        </div>
      </div>
    </div>
  );
}

function getPlanFeatures(planId: string): string[] {
  const features = {
    basic: [
      'Até 10 usuários',
      'Recrutamento básico',
      'Suporte por email',
      'Relatórios básicos'
    ],
    pro: [
      'Até 50 usuários',
      'Recrutamento avançado',
      'Suporte prioritário',
      'Relatórios detalhados',
      'Integração com ATS'
    ],
    enterprise: [
      'Usuários ilimitados',
      'Recrutamento completo',
      'Suporte 24/7',
      'Relatórios personalizados',
      'Integração completa',
      'API dedicada',
      'Treinamento personalizado'
    ]
  };

  return features[planId as keyof typeof features] || [];
} 