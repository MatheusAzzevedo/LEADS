# Script para corrigir problemas do Gradle Wrapper
Write-Host "🔧 Corrigindo problemas do Gradle Wrapper..." -ForegroundColor Green

# 1. Parar processos Java que possam estar travando
Write-Host "Parando processos Java..." -ForegroundColor Yellow
Get-Process | Where-Object {$_.ProcessName -like "*java*"} | Stop-Process -Force -ErrorAction SilentlyContinue

# 2. Limpar diretório .gradle do usuário
Write-Host "Limpando cache do Gradle..." -ForegroundColor Yellow
$gradleDir = "$env:USERPROFILE\.gradle"
if (Test-Path $gradleDir) {
    Remove-Item -Path $gradleDir -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "Cache do Gradle removido" -ForegroundColor Green
}

# 3. Criar diretório .gradle com permissões corretas
Write-Host "Criando diretório .gradle..." -ForegroundColor Yellow
New-Item -ItemType Directory -Path $gradleDir -Force | Out-Null
Write-Host "Diretório .gradle criado" -ForegroundColor Green

# 4. Limpar cache do projeto
Write-Host "Limpando cache do projeto..." -ForegroundColor Yellow
if (Test-Path ".gradle") {
    Remove-Item -Path ".gradle" -Recurse -Force -ErrorAction SilentlyContinue
}
if (Test-Path "build") {
    Remove-Item -Path "build" -Recurse -Force -ErrorAction SilentlyContinue
}

# 5. Verificar se gradlew.bat existe
if (Test-Path "gradlew.bat") {
    Write-Host "Gradle wrapper encontrado" -ForegroundColor Green
} else {
    Write-Host "❌ Gradle wrapper não encontrado!" -ForegroundColor Red
    exit 1
}

# 6. Testar gradle wrapper
Write-Host "Testando Gradle wrapper..." -ForegroundColor Yellow
try {
    $result = & ".\gradlew.bat" --version 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Gradle wrapper funcionando!" -ForegroundColor Green
        Write-Host $result[0] -ForegroundColor Cyan
    } else {
        throw "Falha no teste"
    }
} catch {
    Write-Host "❌ Erro ao testar Gradle wrapper" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🎉 Gradle Wrapper corrigido com sucesso!" -ForegroundColor Green
Write-Host "Agora você pode executar: .\gradlew.bat run" -ForegroundColor Cyan 