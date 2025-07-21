const http = require('http');
const url = require('url');

// Dados de teste
const testData = {
    dashboardStats: {
        totalLeads: 0,
        leadsByPlan: {
            basic: 0,
            pro: 0,
            enterprise: 0
        },
        plans: []
    },
    leads: [],
    plans: [
        {
            planId: "basic",
            name: "Básico",
            description: "Plano básico",
            price: 99.0,
            isActive: true
        },
        {
            planId: "pro",
            name: "Pro",
            description: "Plano profissional",
            price: 199.0,
            isActive: true
        },
        {
            planId: "enterprise",
            name: "Enterprise",
            description: "Plano empresarial",
            price: 399.0,
            isActive: true
        }
    ]
};

// Função para responder com JSON
function respondWithJson(res, data, statusCode = 200) {
    res.writeHead(statusCode, {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type, Authorization'
    });
    res.end(JSON.stringify(data));
}

// Função para ler o body da requisição
function getRequestBody(req) {
    return new Promise((resolve) => {
        let body = '';
        req.on('data', chunk => {
            body += chunk.toString();
        });
        req.on('end', () => {
            try {
                resolve(JSON.parse(body));
            } catch {
                resolve({});
            }
        });
    });
}

// Criar servidor
const server = http.createServer(async (req, res) => {
    const parsedUrl = url.parse(req.url, true);
    const path = parsedUrl.pathname;
    const method = req.method;

    console.log(`${method} ${path}`);

    // CORS preflight
    if (method === 'OPTIONS') {
        res.writeHead(200, {
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
            'Access-Control-Allow-Headers': 'Content-Type, Authorization'
        });
        res.end();
        return;
    }

    try {
        // Health check
        if (path === '/health' && method === 'GET') {
            res.writeHead(200, { 'Content-Type': 'text/plain' });
            res.end('OK');
            return;
        }

        // Dashboard stats
        if (path === '/api/dashboard/stats' && method === 'GET') {
            respondWithJson(res, testData.dashboardStats);
            return;
        }

        // Leads
        if (path === '/api/leads/my' && method === 'GET') {
            respondWithJson(res, testData.leads);
            return;
        }

        // Plans
        if (path === '/api/plans' && method === 'GET') {
            respondWithJson(res, testData.plans);
            return;
        }

        // Login
        if (path === '/api/auth/login' && method === 'POST') {
            const body = await getRequestBody(req);
            if (body.username && body.password) {
                respondWithJson(res, {
                    token: 'test-token-123',
                    user: {
                        id: 1,
                        username: body.username,
                        name: 'Operador 1'
                    }
                });
            } else {
                respondWithJson(res, { error: 'Credenciais inválidas' }, 401);
            }
            return;
        }

        // Validate token
        if (path === '/api/auth/validate' && method === 'GET') {
            respondWithJson(res, { valid: true });
            return;
        }

        // Create lead
        if (path === '/api/leads' && method === 'POST') {
            const body = await getRequestBody(req);
            const newLead = {
                leadId: `LEAD-${Date.now()}`,
                operatorId: 1,
                firstName: body.firstName || 'Teste',
                lastName: body.lastName || 'Lead',
                email: body.email || 'teste@teste.com',
                phone: body.phone || '(11) 99999-9999',
                position: body.position || 'Teste',
                company: body.company || 'Empresa Teste',
                selectedPlan: body.selectedPlan || 'basic',
                createdAt: new Date().toISOString()
            };
            
            testData.leads.push(newLead);
            testData.dashboardStats.totalLeads = testData.leads.length;
            
            respondWithJson(res, newLead);
            return;
        }

        // 404 para rotas não encontradas
        respondWithJson(res, { error: 'Not Found' }, 404);

    } catch (error) {
        console.error('Erro:', error);
        respondWithJson(res, { error: 'Internal Server Error' }, 500);
    }
});

const PORT = 8080;

server.listen(PORT, () => {
    console.log(`🚀 Servidor de teste rodando na porta ${PORT}`);
    console.log(`📊 Health check: http://localhost:${PORT}/health`);
    console.log(`📈 Dashboard stats: http://localhost:${PORT}/api/dashboard/stats`);
    console.log(`👥 Leads: http://localhost:${PORT}/api/leads/my`);
    console.log(`📋 Planos: http://localhost:${PORT}/api/plans`);
    console.log('');
    console.log('✅ Frontend pode conectar em: http://localhost:3000');
    console.log('🔑 Login: operador1 / admin123');
}); 