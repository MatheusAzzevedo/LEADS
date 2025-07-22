# Script para compilar e executar backend simples
Write-Host "Compilando backend simples..." -ForegroundColor Green

# Criar diretório para dependências
if (!(Test-Path "lib")) {
    New-Item -ItemType Directory -Name "lib"
}

# Download das dependências Ktor (se necessário)
$ktorVersion = "2.3.7"
$dependencies = @(
    "io.ktor:ktor-server-core:$ktorVersion",
    "io.ktor:ktor-server-netty:$ktorVersion",
    "io.ktor:ktor-server-content-negotiation:$ktorVersion",
    "io.ktor:ktor-serialization-kotlinx-json:$ktorVersion",
    "io.ktor:ktor-server-cors:$ktorVersion",
    "org.jetbrains.kotlin:kotlin-stdlib:1.9.20"
)

Write-Host "Baixando dependências..." -ForegroundColor Yellow
foreach ($dep in $dependencies) {
    $parts = $dep.Split(":")
    $groupId = $parts[0]
    $artifactId = $parts[1]
    $version = $parts[2]
    
    $url = "https://repo1.maven.org/maven2/$($groupId.Replace('.', '/'))/$artifactId/$version/$artifactId-$version.jar"
    $output = "lib/$artifactId-$version.jar"
    
    if (!(Test-Path $output)) {
        Write-Host "Baixando $artifactId..." -ForegroundColor Cyan
        Invoke-WebRequest -Uri $url -OutFile $output
    }
}

# Compilar o backend simples
Write-Host "Compilando SimpleBackend.kt..." -ForegroundColor Yellow
$kotlinc = "kotlinc"
$classpath = "lib/*"

try {
    & $kotlinc -cp $classpath -include-runtime SimpleBackend.kt -d SimpleBackend.jar
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Backend compilado com sucesso!" -ForegroundColor Green
        
        # Executar o backend
        Write-Host "Iniciando backend na porta 8080..." -ForegroundColor Yellow
        Start-Process -FilePath "java" -ArgumentList "-jar", "SimpleBackend.jar" -NoNewWindow
        
        # Aguardar um pouco e testar
        Start-Sleep -Seconds 3
        
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:8080/health" -Method GET -TimeoutSec 5
            if ($response.StatusCode -eq 200) {
                Write-Host "Backend está rodando!" -ForegroundColor Green
                Write-Host "URL: http://localhost:8080" -ForegroundColor Cyan
                Write-Host "Teste: http://localhost:8080/api/dashboard/stats" -ForegroundColor Cyan
            }
        } catch {
            Write-Host "Backend não respondeu. Verifique se a porta 8080 está livre." -ForegroundColor Red
        }
    } else {
        Write-Host "Erro na compilação!" -ForegroundColor Red
    }
} catch {
    Write-Host "Erro ao compilar: $_" -ForegroundColor Red
    Write-Host "Tentando alternativa com Kotlin compiler..." -ForegroundColor Yellow
    
    # Tentar com kotlinc se disponível
    try {
        & kotlinc -cp $classpath -include-runtime SimpleBackend.kt -d SimpleBackend.jar
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Compilação alternativa bem-sucedida!" -ForegroundColor Green
        }
    } catch {
        Write-Host "Kotlin compiler não encontrado. Instale o Kotlin ou use o IntelliJ IDEA." -ForegroundColor Red
    }
} 