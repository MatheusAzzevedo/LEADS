import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { useLeadForm } from '../../hooks/useLeadForm';
import { useState } from 'react';

const leadSchema = z.object({
  firstName: z.string().min(1, 'Nome é obrigatório'),
  lastName: z.string().min(1, 'Sobrenome é obrigatório'),
  email: z.string().email('Email inválido'),
  phone: z.string().min(1, 'Telefone é obrigatório'),
  position: z.string().min(1, 'Cargo é obrigatório'),
  company: z.string().min(1, 'Empresa é obrigatória'),
});

type LeadFormData = z.infer<typeof leadSchema>;

export default function LeadRegistrationPage() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { leadForm, setLeadForm } = useLeadForm();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LeadFormData>({
    resolver: zodResolver(leadSchema),
  });

  const onSubmit = async (data: LeadFormData) => {
    setLeadForm(data);
    navigate('/questions');
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
} 