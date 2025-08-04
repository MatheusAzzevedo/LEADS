# Script PowerShell para configurar o banco de dados MenuErh
# Uso: .\setup-database.ps1 [database_name] [username] [password]

param(
    [string]$DB_NAME = "menuerh_db",
    [string]$DB_USER = "postgres",
    [string]$DB_PASSWORD = "admin123",
    [string]$DB_HOST = "localhost",
    [string]$DB_PORT = "5432"
)

Write-Host "Configurando banco de dados MenuErh..." -ForegroundColor Green
Write-Host "Database: $DB_NAME" -ForegroundColor Cyan
Write-Host "User: $DB_USER" -ForegroundColor Cyan
Write-Host "Host: $DB_HOST`:$DB_PORT" -ForegroundColor Cyan

# Verificar se PostgreSQL está rodando
Write-Host "Verificando conexão com PostgreSQL..." -ForegroundColor Yellow
try {
    $testConnection = & psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d postgres -c "SELECT 1;" 2>$null
    if ($LASTEXITCODE -ne 0) {
        throw "Falha na conexão"
    }
    Write-Host "PostgreSQL está acessível" -ForegroundColor Green
} catch {
    Write-Host "PostgreSQL não está rodando ou não está acessível" -ForegroundColor Red
    Write-Host "Certifique-se de que o PostgreSQL está instalado e rodando" -ForegroundColor Yellow
    exit 1
}

# Criar banco de dados se não existir
Write-Host "Criando banco de dados..." -ForegroundColor Yellow
try {
    & createdb -h $DB_HOST -p $DB_PORT -U $DB_USER $DB_NAME 2>$null
    Write-Host "Banco de dados criado ou já existe" -ForegroundColor Green
} catch {
    Write-Host "Erro ao criar banco de dados (pode já existir)" -ForegroundColor Yellow
}

# Executar migrações
Write-Host "Executando migrações..." -ForegroundColor Yellow

# Migration 001 - Criar tabelas
Write-Host "Executando migration 001 - Criar tabelas..." -ForegroundColor Cyan
try {
    & psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f "database/migrations/001_create_tables.sql"
    Write-Host "Migration 001 executada com sucesso" -ForegroundColor Green
} catch {
    Write-Host "Erro ao executar migration 001" -ForegroundColor Red
    exit 1
}

# Migration 002 - Inserir dados iniciais
Write-Host "Executando migration 002 - Inserir dados iniciais..." -ForegroundColor Cyan
try {
    & psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f "database/migrations/002_insert_initial_data.sql"
    Write-Host "Migration 002 executada com sucesso" -ForegroundColor Green
} catch {
    Write-Host "Erro ao executar migration 002" -ForegroundColor Red
    exit 1
}

# Migration 003 - Alterar tabela de leads
Write-Host "Executando migration 003 - Alterar tabela de leads..." -ForegroundColor Cyan
try {
    & psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f "database/migrations/003_alter_leads_table.sql"
    Write-Host "Migration 003 executada com sucesso" -ForegroundColor Green
} catch {
    Write-Host "Erro ao executar migration 003" -ForegroundColor Red
    exit 1
}

# Verificar dados inseridos
Write-Host "Verificando dados inseridos..." -ForegroundColor Yellow
Write-Host "Operadores cadastrados:" -ForegroundColor Cyan
& psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "SELECT username, name FROM operators;"

Write-Host "Planos cadastrados:" -ForegroundColor Cyan
& psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "SELECT plan_id, name FROM plans;"

Write-Host ""
Write-Host "Banco de dados configurado com sucesso!" -ForegroundColor Green
Write-Host ""
Write-Host "Credenciais de acesso:" -ForegroundColor Yellow
Write-Host "   Usuário: operador1 a operador10" -ForegroundColor White
Write-Host "   Senha: admin123" -ForegroundColor White
Write-Host ""
Write-Host "Para conectar:" -ForegroundColor Yellow
Write-Host "   psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME" -ForegroundColor White
Write-Host ""
Write-Host "Para verificar tabelas:" -ForegroundColor Yellow
Write-Host "   \dt" -ForegroundColor White
Write-Host ""
Write-Host "Para verificar operadores:" -ForegroundColor Yellow
Write-Host "   SELECT username, name FROM operators;" -ForegroundColor White 