import React, { createContext, useContext, useState } from 'react';

export interface LeadFormData {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  position: string;
  company: string;
  question1Responses: string[];
  question2Responses: string[];
  question3Responses: string[];
  question4Responses: string[];
  question5Text: string;
  selectedPlan: string;
}

const defaultLeadFormData: LeadFormData = {
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  position: '',
  company: '',
  question1Responses: [],
  question2Responses: [],
  question3Responses: [],
  question4Responses: [],
  question5Text: '',
  selectedPlan: '',
};

interface LeadFormContextType {
  leadForm: LeadFormData;
  setLeadForm: (data: Partial<LeadFormData>) => void;
  resetLeadForm: () => void;
}

const LeadFormContext = createContext<LeadFormContextType | undefined>(undefined);

export const LeadFormProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [leadForm, setLeadFormState] = useState<LeadFormData>(defaultLeadFormData);

  const setLeadForm = (data: Partial<LeadFormData>) => {
    setLeadFormState((prev) => ({ ...prev, ...data }));
  };

  const resetLeadForm = () => setLeadFormState(defaultLeadFormData);

  return (
    <LeadFormContext.Provider value={{ leadForm, setLeadForm, resetLeadForm }}>
      {children}
    </LeadFormContext.Provider>
  );
};

export function useLeadForm() {
  const context = useContext(LeadFormContext);
  if (!context) {
    throw new Error('useLeadForm must be used within a LeadFormProvider');
  }
  return context;
} 