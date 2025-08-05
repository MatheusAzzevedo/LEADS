-- 004_remove_plans_and_add_pilot_flag.sql
-- Migration para remover a funcionalidade de Planos e adicionar a flag vaga_piloto.

-- 1. Remover a tabela de planos
DROP TABLE IF EXISTS plans CASCADE;

-- 2. Remover a coluna selected_plan da tabela de leads
ALTER TABLE leads DROP COLUMN IF EXISTS selected_plan;

-- 3. Adicionar a coluna vaga_piloto na tabela de leads
ALTER TABLE leads ADD COLUMN vaga_piloto BOOLEAN DEFAULT FALSE;

-- 4. Remover o índice relacionado a plans
DROP INDEX IF EXISTS idx_plans_plan_id;
