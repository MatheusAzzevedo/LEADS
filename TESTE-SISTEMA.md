# 🧪 Guia de Teste - Sistema MenuErh

## 📋 Objetivo
Testar o sistema MenuErh com dados zerados para validar o fluxo completo de cadastro de leads.

## ✅ Preparação Concluída

### 1. **Dashboard Configurado**
- ✅ Dados mockados removidos
- ✅ Sistema busca apenas dados reais do banco
- ✅ Estatísticas zeradas para teste

### 2. **Banco de Dados Configurado**
- ✅ PostgreSQL rodando na porta 5432
- ✅ Banco `menuerh_db` criado
- ✅ Tabelas criadas (operators, leads, plans)
- ✅ 10 operadores pré-cadastrados
- ✅ 3 planos pré-cadastrados

### 3. **Credenciais de Acesso**
```
Usuário: operador1
Senha: admin123
```

## 🚀 Como Testar

### **Passo 1: Iniciar os Serviços**

```bash
# Terminal 1 - Backend (Ktor)
./gradlew run

# Terminal 2 - Frontend (React)
npm run dev
```

### **Passo 2: Acessar o Sistema**

1. **Abra o navegador**: http://localhost:3000
2. **Faça login**:
   - Usuário: `operador1`
   - Senha: `admin123`

### **Passo 3: Verificar Dashboard Zerado**

Ao fazer login, você deve ver:
- **Total de Leads**: 0
- **Leads este Mês**: 0  
- **Leads Hoje**: 0
- **Taxa de Conversão**: 0%
- **Tabela de Leads**: Vazia (mensagem "Nenhum lead encontrado")

### **Passo 4: Testar Cadastro de Leads**

1. **Clique em "Novo Lead"** no dashboard
2. **Preencha o formulário** com dados de teste
3. **Complete o fluxo**: Cadastro → Perguntas → Planos
4. **Verifique o dashboard** - deve mostrar o lead cadastrado

## 📊 Dados de Teste Sugeridos

### **Lead 1 - Teste Básico**
```
Nome: João Silva
Email: joao@teste.com
Telefone: (11) 99999-9999
Empresa: Empresa Teste Ltda
Cargo: Gerente
```

### **Lead 2 - Teste Completo**
```
Nome: Maria Santos
Email: maria@teste.com
Telefone: (11) 88888-8888
Empresa: Startup Inovação
Cargo: CEO
```

## 🔍 Verificações Importantes

### **No Dashboard**
- [ ] Estatísticas começam zeradas
- [ ] Leads aparecem após cadastro
- [ ] Contadores atualizam corretamente
- [ ] Filtros funcionam

### **No Banco de Dados (pgAdmin)**
- [ ] Tabela `leads` vazia inicialmente
- [ ] Novos registros aparecem após cadastro
- [ ] Dados são salvos corretamente

### **No Fluxo de Cadastro**
- [ ] Formulário valida dados
- [ ] Perguntas são exibidas
- [ ] Planos são apresentados
- [ ] Lead é salvo no banco

## 🐛 Solução de Problemas

### **Se o Backend não iniciar**
```bash
# Limpar cache do Gradle
Remove-Item -Path "C:\Users\liane\.gradle\wrapper\dists" -Recurse -Force
./gradlew clean build
```

### **Se o Frontend não conectar**
```bash
# Verificar se o backend está rodando
netstat -an | findstr ":8080"
```

### **Se o banco não conectar**
```bash
# Verificar PostgreSQL
Get-Service -Name "*postgres*"
```

## 📝 Logs de Teste

Mantenha um registro dos testes:

| Data | Lead Testado | Plano Escolhido | Status | Observações |
|------|-------------|----------------|--------|-------------|
|      |             |                |        |             |

## ✅ Critérios de Sucesso

- [ ] Dashboard inicia com dados zerados
- [ ] Cadastro de lead funciona completamente
- [ ] Dados aparecem no dashboard após cadastro
- [ ] Estatísticas são atualizadas corretamente
- [ ] Sistema mantém consistência dos dados

---

**🎯 Objetivo Alcançado**: Sistema pronto para teste de cadastro com dados reais do banco! 