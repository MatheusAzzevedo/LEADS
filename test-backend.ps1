# Script para testar e iniciar o backend MenuErh
Write-Host "Testando Backend MenuErh..." -ForegroundColor Green

# Verificar se Java está instalado
Write-Host "Verificando Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java encontrado:" -ForegroundColor Green
    Write-Host $javaVersion[0] -ForegroundColor Cyan
} catch {
    Write-Host "Java não encontrado!" -ForegroundColor Red
    exit 1
}

# Verificar se PostgreSQL está rodando
Write-Host "Verificando PostgreSQL..." -ForegroundColor Yellow
try {
    $pgService = Get-Service -Name "*postgres*" -ErrorAction SilentlyContinue
    if ($pgService -and $pgService.Status -eq "Running") {
        Write-Host "PostgreSQL está rodando" -ForegroundColor Green
    } else {
        Write-Host "PostgreSQL não está rodando!" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Erro ao verificar PostgreSQL!" -ForegroundColor Red
    exit 1
}

# Verificar se o banco existe
Write-Host "Verificando banco de dados..." -ForegroundColor Yellow
try {
    $testDb = & "C:\Program Files\PostgreSQL\17\bin\psql.exe" -h localhost -p 5432 -U postgres -d menuerh_db -c "SELECT 1;" 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Banco menuerh_db está acessível" -ForegroundColor Green
    } else {
        Write-Host "Banco menuerh_db não está acessível!" -ForegroundColor Red
        Write-Host "Execute: .\database\setup-database.ps1" -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "Erro ao conectar no banco!" -ForegroundColor Red
    exit 1
}

# Tentar resolver problema do Gradle
Write-Host "Tentando resolver problema do Gradle..." -ForegroundColor Yellow
try {
    # Limpar cache do Gradle
    Remove-Item -Path "C:\Users\liane\.gradle\wrapper\dists" -Recurse -Force -ErrorAction SilentlyContinue
    Remove-Item -Path "C:\Users\liane\.gradle\caches" -Recurse -Force -ErrorAction SilentlyContinue
    
    Write-Host "Cache do Gradle limpo" -ForegroundColor Green
} catch {
    Write-Host "Erro ao limpar cache do Gradle" -ForegroundColor Yellow
}

# Tentar compilar com Gradle
Write-Host "Compilando projeto..." -ForegroundColor Yellow
try {
    $gradleResult = .\gradlew.bat build 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Projeto compilado com sucesso!" -ForegroundColor Green
    } else {
        Write-Host "Erro na compilação:" -ForegroundColor Red
        Write-Host $gradleResult -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Erro ao executar Gradle!" -ForegroundColor Red
    Write-Host "Tentando alternativa..." -ForegroundColor Yellow
}

# Testar se o backend está rodando
Write-Host "Testando se o backend está respondendo..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/health" -Method GET -TimeoutSec 10
    if ($response.StatusCode -eq 200) {
        Write-Host "Backend está respondendo!" -ForegroundColor Green
        Write-Host "URL: http://localhost:8080" -ForegroundColor Cyan
    } else {
        Write-Host "Backend respondeu com status: $($response.StatusCode)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "Backend não está respondendo na porta 8080" -ForegroundColor Red
    Write-Host "Verifique se o processo Java está rodando" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Próximos passos:" -ForegroundColor Green
Write-Host "1. Se o backend estiver rodando, acesse: http://localhost:3001" -ForegroundColor White
Write-Host "2. Faça login com: operador1 / admin123" -ForegroundColor White
Write-Host "3. Teste o cadastro de leads" -ForegroundColor White 