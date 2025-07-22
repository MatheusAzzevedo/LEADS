#!/bin/bash

# Script para configurar o banco de dados MenuErh
# Uso: ./setup-database.sh [database_name] [username] [password]

set -e

# Configurações padrão
DB_NAME=${1:-menuerh_db}
DB_USER=${2:-postgres}
DB_PASSWORD=${3:-admin123}
DB_HOST=${4:-localhost}
DB_PORT=${5:-5432}

echo "🚀 Configurando banco de dados MenuErh..."
echo "📊 Database: $DB_NAME"
echo "👤 User: $DB_USER"
echo "🌐 Host: $DB_HOST:$DB_PORT"

# Verificar se PostgreSQL está rodando
echo "🔍 Verificando conexão com PostgreSQL..."
if ! pg_isready -h $DB_HOST -p $DB_PORT -U $DB_USER; then
    echo "❌ PostgreSQL não está rodando ou não está acessível"
    echo "💡 Certifique-se de que o PostgreSQL está instalado e rodando"
    exit 1
fi

# Criar banco de dados se não existir
echo "📝 Criando banco de dados..."
createdb -h $DB_HOST -p $DB_PORT -U $DB_USER $DB_NAME 2>/dev/null || echo "✅ Banco de dados já existe"

# Executar migrações
echo "🔄 Executando migrações..."

# Migration 001 - Criar tabelas
echo "📋 Executando migration 001 - Criar tabelas..."
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f database/migrations/001_create_tables.sql

# Migration 002 - Inserir dados iniciais
echo "📋 Executando migration 002 - Inserir dados iniciais..."
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f database/migrations/002_insert_initial_data.sql

# Verificar dados inseridos
echo "✅ Verificando dados inseridos..."
echo "👥 Operadores cadastrados:"
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "SELECT username, name FROM operators;"

echo "📋 Planos cadastrados:"
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "SELECT plan_id, name FROM plans;"

echo ""
echo "🎉 Banco de dados configurado com sucesso!"
echo ""
echo "📝 Credenciais de acesso:"
echo "   Usuário: operador1 a operador10"
echo "   Senha: admin123"
echo ""
echo "🔗 Para conectar:"
echo "   psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME"
echo ""
echo "📊 Para verificar tabelas:"
echo "   \dt"
echo ""
echo "👥 Para verificar operadores:"
echo "   SELECT username, name FROM operators;" 