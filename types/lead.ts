export interface Lead {
  id: string
  firstName: string
  lastName: string
  email: string
  phone: string
  position: string
  company: string
  operatorId: string
  operatorName: string
  questions: LeadQuestions
  selectedPlan?: string
  status: 'new' | 'qualified' | 'contacted' | 'converted' | 'lost'
  createdAt: string
  updatedAt: string
}

export interface LeadQuestions {
  hasBudget: boolean
  isDecisionMaker: boolean
  hasUrgency: boolean
  isInterested: boolean
  hasTeam: boolean
  additionalInfo: string
}

export interface CreateLeadData {
  firstName: string
  lastName: string
  email: string
  phone: string
  position: string
  company: string
}

export interface LeadStats {
  total: number
  new: number
  qualified: number
  contacted: number
  converted: number
  lost: number
  today: number
  thisWeek: number
  thisMonth: number
} 