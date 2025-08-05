# 🔧 Instruções para Aplicar Migração no Railway

## 📋 Problema Identificado

O erro `column leads.vaga_piloto does not exist` indica que a migração 004 não foi aplicada no banco de dados do Railway.

## 🚀 Solução Automática (Recomendada)

### ✅ Sistema de Migração Automática Implementado

O sistema agora aplica automaticamente a migração 004 na inicialização. **Basta fazer um novo deploy no Railway** que a migração será aplicada automaticamente.

**Arquivos criados/modificados:**
- `MigrationRunner.kt` - Classe para executar migrações
- `DatabaseConfig.kt` - Modificado para aplicar migrações na inicialização
- Sistema verifica se a coluna `vaga_piloto` existe antes de aplicar a migração

## 🔧 Solução Manual (Se necessário)

### Opção 1: Console SQL do Railway

1. Acesse o [Railway Dashboard](https://railway.app)
2. Vá para seu projeto
3. Clique na aba **"Database"**
4. Clique em **"Query"** ou **"Connect"**
5. Cole e execute o seguinte SQL:

```sql
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
```

### Opção 2: PowerShell Script

Execute o script `apply-migration-railway.ps1`:

```powershell
.\apply-migration-railway.ps1
```

### Opção 3: psql (Se disponível)

Se você tem `psql` instalado e a string de conexão do Railway:

```bash
psql 'sua_connection_string_railway' -f apply-migration-railway.sql
```

## 🔍 Verificação

Após aplicar a migração, verifique se foi aplicada corretamente:

```sql
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_name = 'leads' AND column_name = 'vaga_piloto';
```

**Resultado esperado:**
```
column_name | data_type | is_nullable | column_default
vaga_piloto | boolean   | YES         | false
```

## 🚀 Próximos Passos

1. **Aplicar a migração** (automática ou manual)
2. **Redeploy da aplicação** no Railway
3. **Verificar logs** para confirmar que não há mais erros
4. **Testar funcionalidades** relacionadas a leads

## 📝 Explicação Técnica

### O que a Migração 004 faz:

1. **Adiciona coluna `vaga_piloto`**: `BOOLEAN DEFAULT FALSE`
2. **Remove coluna `selected_plan`**: Não mais necessária
3. **Remove tabela `plans`**: Funcionalidade removida
4. **Remove índice `idx_plans_plan_id`**: Não mais necessário

### Por que o erro ocorreu:

- O código Kotlin define a coluna `vaga_piloto` no schema
- Mas a migração 004 não foi executada no banco do Railway
- Resultado: código tenta acessar coluna que não existe no banco

### Como a solução funciona:

- `MigrationRunner.kt` verifica se a coluna existe
- Se não existir, aplica a migração automaticamente
- Sistema robusto que não quebra se a migração já foi aplicada

## ✅ Status da Correção

- [x] **MigrationRunner.kt** criado
- [x] **DatabaseConfig.kt** modificado
- [x] **Scripts manuais** criados
- [x] **README.md** atualizado
- [x] **CHANGELOG.md** atualizado
- [x] **Sistema automático** implementado

**Próximo deploy aplicará a migração automaticamente!**