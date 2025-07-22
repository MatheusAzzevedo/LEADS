-- 001_create_tables.sql
-- Migration para criar as tabelas principais do sistema MenuErh

-- Criar tabela de operadores
CREATE TABLE operators (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Criar tabela de leads
CREATE TABLE leads (
    id SERIAL PRIMARY KEY,
    lead_id VARCHAR(20) UNIQUE NOT NULL,
    operator_id INTEGER NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    position VARCHAR(100) NOT NULL,
    company VARCHAR(100) NOT NULL,
    question1_responses text[] NULL,
    question2_responses text[] NULL,
    question3_responses text[] NULL,
    question4_responses text[] NULL,
    question5_text TEXT,
    selected_plan VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_operator FOREIGN KEY (operator_id) REFERENCES operators(id)
);

-- Criar tabela de planos
CREATE TABLE plans (
    id SERIAL PRIMARY KEY,
    plan_id VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    image_url VARCHAR(500),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Criar índices para performance
CREATE INDEX idx_leads_operator_id ON leads(operator_id);
CREATE INDEX idx_leads_created_at ON leads(created_at);
CREATE INDEX idx_leads_lead_id ON leads(lead_id);
CREATE INDEX idx_operators_username ON operators(username);
CREATE INDEX idx_plans_plan_id ON plans(plan_id);

-- Criar trigger para atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_operators_updated_at BEFORE UPDATE ON operators
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_leads_updated_at BEFORE UPDATE ON leads
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column(); 