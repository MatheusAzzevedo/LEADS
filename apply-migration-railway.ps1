# Script PowerShell para aplicar migração no Railway
# Execute este script para aplicar a migração 004 no banco de dados do Railway

Write-Host "=== Aplicando Migração 004 no Railway ===" -ForegroundColor Green

# Verificar se o arquivo SQL existe
if (-not (Test-Path "apply-migration-railway.sql")) {
    Write-Host "❌ Arquivo apply-migration-railway.sql não encontrado!" -ForegroundColor Red
    exit 1
}

# Instruções para o usuário
Write-Host ""
Write-Host "INSTRUÇÕES PARA APLICAR A MIGRAÇÃO:" -ForegroundColor Yellow
Write-Host "1. Acesse o painel do Railway: https://railway.app" -ForegroundColor White
Write-Host "2. Vá para seu projeto e clique na aba 'Database'" -ForegroundColor White
Write-Host "3. Clique em 'Query' ou 'Connect' para acessar o console SQL" -ForegroundColor White
Write-Host "4. Copie e cole o conteúdo do arquivo apply-migration-railway.sql" -ForegroundColor White
Write-Host "5. Execute o script SQL" -ForegroundColor White
Write-Host ""

# Mostrar o conteúdo do arquivo SQL
Write-Host "=== CONTEÚDO DO SCRIPT SQL ===" -ForegroundColor Cyan
Get-Content "apply-migration-railway.sql" | Write-Host
Write-Host ""

# Opção alternativa usando psql se disponível
Write-Host "=== ALTERNATIVA: Usando psql (se disponível) ===" -ForegroundColor Yellow
Write-Host "Se você tem psql instalado e a string de conexão do Railway:" -ForegroundColor White
Write-Host "psql 'sua_connection_string_railway' -f apply-migration-railway.sql" -ForegroundColor Gray
Write-Host ""

Write-Host "✅ Após aplicar a migração, redeploy sua aplicação no Railway." -ForegroundColor Green
Write-Host ""

# Aguardar confirmação do usuário
Read-Host "Pressione Enter após aplicar a migração no Railway..."

Write-Host "✅ Migração aplicada! Verifique os logs da aplicação." -ForegroundColor Green