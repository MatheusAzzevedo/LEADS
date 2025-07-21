import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '../../components/ui/Button';
import { useLeadForm } from '../../hooks/useLeadForm';

interface QuestionData {
  question1Responses: string[];
  question2Responses: string[];
  question3Responses: string[];
  question4Responses: string[];
  question5Text: string;
}

export default function QuestionsPage() {
  const [formData, setFormData] = useState<QuestionData>({
    question1Responses: [],
    question2Responses: [],
    question3Responses: [],
    question4Responses: [],
    question5Text: '',
  });
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { leadForm, setLeadForm } = useLeadForm();

  const questions = [
    {
      id: 1,
      title: 'Qual é o tamanho da sua empresa?',
      options: ['1-10 funcionários', '11-50 funcionários', '51-200 funcionários', '200+ funcionários'],
      type: 'checkbox'
    },
    {
      id: 2,
      title: 'Qual setor sua empresa atua?',
      options: ['Tecnologia', 'Saúde', 'Educação', 'Varejo', 'Serviços', 'Outros'],
      type: 'checkbox'
    },
    {
      id: 3,
      title: 'Qual é o principal desafio atual?',
      options: ['Recrutamento', 'Retenção', 'Treinamento', 'Gestão de performance', 'Compliance'],
      type: 'checkbox'
    },
    {
      id: 4,
      title: 'Qual é o orçamento disponível?',
      options: ['Até R$ 1.000/mês', 'R$ 1.000 - R$ 5.000/mês', 'R$ 5.000 - R$ 10.000/mês', 'Acima de R$ 10.000/mês'],
      type: 'checkbox'
    }
  ];

  const handleCheckboxChange = (questionId: number, option: string) => {
    const fieldName = `question${questionId}Responses` as keyof QuestionData;
    const currentResponses = formData[fieldName] as string[];
    
    if (currentResponses.includes(option)) {
      setFormData(prev => ({
        ...prev,
        [fieldName]: currentResponses.filter(r => r !== option)
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [fieldName]: [...currentResponses, option]
      }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLeadForm(formData);
    navigate('/plans');
  };

  return (
    <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-2xl mx-auto">
        <div className="bg-white p-8 rounded-xl shadow-lg">
          <h2 className="text-2xl font-bold mb-6 text-center text-gray-700">
            2. Perguntas de Qualificação
          </h2>
          
          <form onSubmit={handleSubmit} className="space-y-6">
            {questions.map((question) => (
              <div key={question.id} className="space-y-3">
                <h3 className="text-lg font-medium text-gray-900">
                  {question.title}
                </h3>
                <div className="space-y-2">
                  {question.options.map((option) => (
                    <label key={option} className="flex items-center space-x-3">
                      <input
                        type="checkbox"
                        checked={formData[`question${question.id}Responses` as keyof QuestionData]?.includes(option)}
                        onChange={() => handleCheckboxChange(question.id, option)}
                        className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                      />
                      <span className="text-gray-700">{option}</span>
                    </label>
                  ))}
                </div>
              </div>
            ))}

            <div className="space-y-3">
              <h3 className="text-lg font-medium text-gray-900">
                5. Há algo mais que gostaria de nos contar?
              </h3>
              <textarea
                value={formData.question5Text}
                onChange={(e) => setFormData(prev => ({ ...prev, question5Text: e.target.value }))}
                rows={4}
                className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                placeholder="Conte-nos mais sobre suas necessidades..."
              />
            </div>
            
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
} 