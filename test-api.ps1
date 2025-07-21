# Script de teste para APIs do MenuErh
# Execute após iniciar o servidor Ktor

Write-Host "🧪 Testando APIs do MenuErh" -ForegroundColor Green
Write-Host ""

# Variáveis
$BASE_URL = "http://localhost:8080"
$TOKEN = ""

# Função para fazer requisições
function Invoke-ApiRequest {
    param(
        [string]$Method,
        [string]$Url,
        [string]$Body = "",
        [string]$Token = ""
    )
    
    $headers = @{
        "Content-Type" = "application/json"
    }
    
    if ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }
    
    try {
        if ($Method -eq "GET") {
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers
        } else {
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers -Body $Body
        }
        
        Write-Host "✅ $Method $Url" -ForegroundColor Green
        $response | ConvertTo-Json -Depth 3
        return $response
    }
    catch {
        Write-Host "❌ $Method $Url" -ForegroundColor Red
        Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

# 1. Teste de login
Write-Host "1. Testando login..." -ForegroundColor Yellow
$loginBody = @{
    username = "operador1"
    password = "admin123"
} | ConvertTo-Json

$loginResponse = Invoke-ApiRequest -Method "POST" -Url "$BASE_URL/auth/login" -Body $loginBody

if ($loginResponse -and $loginResponse.token) {
    $TOKEN = $loginResponse.token
    Write-Host "Token obtido: $($TOKEN.Substring(0, 20))..." -ForegroundColor Green
} else {
    Write-Host "❌ Falha no login" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 2. Teste de validação de token
Write-Host "2. Testando validação de token..." -ForegroundColor Yellow
Invoke-ApiRequest -Method "GET" -Url "$BASE_URL/auth/validate" -Token $TOKEN

Write-Host ""

# 3. Teste de busca de planos
Write-Host "3. Testando busca de planos..." -ForegroundColor Yellow
Invoke-ApiRequest -Method "GET" -Url "$BASE_URL/plans"

Write-Host ""

# 4. Teste de criação de lead
Write-Host "4. Testando criação de lead..." -ForegroundColor Yellow
$leadBody = @{
    firstName = "João"
    lastName = "Silva"
    email = "joao.silva@email.com"
    phone = "11999999999"
    position = "Gerente"
    company = "Empresa ABC"
    question1Responses = @("Opção 1", "Opção 2")
    question2Responses = @("Sim")
    question3Responses = @("Não")
    question4Responses = @("Talvez")
    question5Text = "Comentário adicional sobre o projeto"
    selectedPlan = "pro"
} | ConvertTo-Json

Invoke-ApiRequest -Method "POST" -Url "$BASE_URL/leads" -Body $leadBody -Token $TOKEN

Write-Host ""

# 5. Teste de busca de leads
Write-Host "5. Testando busca de leads..." -ForegroundColor Yellow
Invoke-ApiRequest -Method "GET" -Url "$BASE_URL/leads" -Token $TOKEN

Write-Host ""

# 6. Teste de leads do operador
Write-Host "6. Testando leads do operador..." -ForegroundColor Yellow
Invoke-ApiRequest -Method "GET" -Url "$BASE_URL/leads/my" -Token $TOKEN

Write-Host ""

# 7. Teste de estatísticas do dashboard
Write-Host "7. Testando estatísticas do dashboard..." -ForegroundColor Yellow
Invoke-ApiRequest -Method "GET" -Url "$BASE_URL/dashboard/stats" -Token $TOKEN

Write-Host ""
Write-Host "🎉 Testes concluídos!" -ForegroundColor Green 