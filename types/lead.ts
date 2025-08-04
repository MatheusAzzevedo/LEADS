export interface Lead {
  id: string;
  leadId: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  position: string;
  company: string;
  operatorId: string;
  operatorName?: string;
  interest?: string[];
  hiring_flow?: string;
  investment_value?: string;
  eligible_for_pilot?: boolean;
  selectedPlan?: string;
  status?: 'new' | 'qualified' | 'contacted' | 'converted' | 'lost';
  createdAt: string;
  updatedAt: string;
}

export interface CreateLeadRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  position: string;
  company: string;
  interest?: string[];
  hiring_flow?: string;
  investment_value?: string;
  eligible_for_pilot?: boolean;
}

export interface LeadStats {
  total: number;
  new: number;
  qualified: number;
  contacted: number;
  converted: number;
  lost: number;
  today: number;
  thisWeek: number;
  thisMonth: number;
}
