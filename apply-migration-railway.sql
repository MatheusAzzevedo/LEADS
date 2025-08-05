-- Script para aplicar migração 004 no Railway
-- Execute este script diretamente no banco de dados do Railway

-- Verificar se a coluna já existe antes de adicionar
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'leads' AND column_name = 'vaga_piloto'
    ) THEN
        -- Adicionar a coluna vaga_piloto na tabela de leads
        ALTER TABLE leads ADD COLUMN vaga_piloto BOOLEAN DEFAULT FALSE;
        
        -- Remover a coluna selected_plan se ainda existir
        IF EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'leads' AND column_name = 'selected_plan'
        ) THEN
            ALTER TABLE leads DROP COLUMN selected_plan;
        END IF;
        
        -- Remover a tabela plans se ainda existir
        DROP TABLE IF EXISTS plans CASCADE;
        
        -- Remover o índice relacionado a plans se ainda existir
        DROP INDEX IF EXISTS idx_plans_plan_id;
        
        RAISE NOTICE 'Migração 004 aplicada com sucesso!';
    ELSE
        RAISE NOTICE 'Coluna vaga_piloto já existe. Migração já foi aplicada.';
    END IF;
END $$;