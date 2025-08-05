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
  question1Responses?: string[];
  question2Responses?: string[];
  question3Responses?: string[];
  question4Responses?: string[];
  question5Text?: string;
  vagaPiloto?: boolean;
  status?: 'new' | 'qualified' | 'contacted' | 'converted' | 'lost';
  createdAt: string;
  updatedAt: string;
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
