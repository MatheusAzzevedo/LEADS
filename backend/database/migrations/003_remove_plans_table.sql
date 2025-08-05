-- Remover tabela de planos
DROP TABLE IF EXISTS plans CASCADE;

-- Remover qualquer referência a planos na tabela de leads se existir
ALTER TABLE leads DROP COLUMN IF EXISTS selected_plan;
ALTER TABLE leads DROP COLUMN IF EXISTS plan_id;