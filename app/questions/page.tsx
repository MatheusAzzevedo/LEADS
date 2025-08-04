import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { useLeadForm } from '../../hooks/useLeadForm';
import { apiService } from '../../services/api';
import toast from 'react-hot-toast';

export default function QuestionsPage() {
  const navigate = useNavigate();
  const { leadForm, setLeadForm } = useLeadForm();
  const [interest, setInterest] = useState<string[]>([]);
  const [hiringFlow, setHiringFlow] = useState('');
  const [investmentValue, setInvestmentValue] = useState('');
  const [eligibleForPilot, setEligibleForPilot] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleInterestChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { value, checked } = e.target;
    if (checked) {
      setInterest([...interest, value]);
    } else {
      setInterest(interest.filter((item) => item !== value));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    
    const finalLeadData = {
      ...leadForm,
      interest,
      hiring_flow: hiringFlow,
      investment_value: investmentValue,
      eligible_for_pilot: eligibleForPilot,
    };

    setLeadForm(finalLeadData);

    try {
      await apiService.createLead(finalLeadData);
      toast.success('Lead cadastrado com sucesso!');
      navigate('/dashboard');
    } catch (error) {
      toast.error('Erro ao cadastrar lead.');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-2xl mx-auto">
        <div className="bg-white p-8 rounded-xl shadow-lg">
          <h2 className="text-2xl font-bold mb-6 text-center text-gray-700">
            2. Perguntas Adicionais
          </h2>
          
          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="text-lg font-medium text-gray-900">Interesse em:</label>
              <div className="mt-2 space-y-2">
                {['Efetivos', 'Aprendizes', 'Estagiários'].map((item) => (
                  <label key={item} className="flex items-center">
                    <input
                      type="checkbox"
                      value={item}
                      onChange={handleInterestChange}
                      className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                    />
                    <span className="ml-3 text-gray-700">{item}</span>
                  </label>
                ))}
              </div>
            </div>

            <Input
              label="Qual é o fluxo de contratação por mês?"
              value={hiringFlow}
              onChange={(e) => setHiringFlow(e.target.value)}
              placeholder="Ex: 5 contratações/mês"
            />

            <Input
              label="Qual valor de investimento disponível?"
              value={investmentValue}
              onChange={(e) => setInvestmentValue(e.target.value)}
              placeholder="Ex: R$ 10.000"
            />

            <div className="flex items-center">
              <input
                id="eligible"
                type="radio"
                name="eligibility"
                checked={eligibleForPilot}
                onChange={() => setEligibleForPilot(true)}
                className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300"
              />
              <label htmlFor="eligible" className="ml-3 block text-sm font-medium text-gray-700">
                Elegível a Vaga Piloto
              </label>
            </div>
            
            <Button type="submit" loading={loading} className="w-full">
              Finalizar Cadastro
            </Button>
          </form>
        </div>
      </div>
    </div>
  );
}
