-- 002_insert_initial_data.sql
-- Migration para inserir dados iniciais do sistema MenuErh

-- Inserir 10 operadores (senha: admin123 - hash bcrypt)
INSERT INTO operators (username, password_hash, name) VALUES
('operador1', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 1'),
('operador2', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 2'),
('operador3', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 3'),
('operador4', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 4'),
('operador5', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 5'),
('operador6', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 6'),
('operador7', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 7'),
('operador8', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 8'),
('operador9', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 9'),
('operador10', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 10');

-- Inserir planos
INSERT INTO plans (plan_id, name, description, image_url) VALUES
('basico', 'Plano Básico', 'Ideal para pequenas equipes que estão começando. Inclui funcionalidades essenciais para gestão de talentos.', 'https://placehold.co/600x400/3B82F6/FFFFFF?text=Básico'),
('pro', 'Plano Profissional', 'A solução mais popular, com ferramentas avançadas de automação, relatórios detalhados e integrações.', 'https://placehold.co/600x400/10B981/FFFFFF?text=Pro'),
('enterprise', 'Plano Enterprise', 'Para grandes corporações que necessitam de segurança robusta, suporte dedicado e personalização completa.', 'https://placehold.co/600x400/8B5CF6/FFFFFF?text=Enterprise'); 