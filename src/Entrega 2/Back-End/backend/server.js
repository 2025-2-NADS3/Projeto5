const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
require('dotenv').config();

const { testConnection } = require('./src/config/database');

const produtosRoutes = require('./src/routes/produtos');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(helmet());
app.use(cors());
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));


app.use('/api/produtos', produtosRoutes);

app.get('/health', (req, res) => {
    res.json({ 
        status: 'OK', 
        timestamp: new Date().toISOString(),
        service: 'Comendoria da Tia API'
    });
});

app.get('/', (req, res) => {
    res.json({
        message: '🚀 API Comendoria da Tia - FECAP',
        version: '1.0.0',
        endpoints: {
            produtos: '/api/produtos',
            health: '/health'
        },
        documentation: '/docs'
    });
});

app.use('*', (req, res) => {
    res.status(404).json({
        success: false,
        error: 'Endpoint não encontrado'
    });
});

app.use((error, req, res, next) => {
    console.error('Erro:', error);
    res.status(500).json({
        success: false,
        error: 'Erro interno do servidor'
    });
});

async function startServer() {
    await testConnection();
    
    app.listen(PORT, () => {
        console.log(`🎉 Servidor rodando na porta ${PORT}`);
        console.log(`📍 URL: http://localhost:${PORT}`);
        console.log(`🏥 Health Check: http://localhost:${PORT}/health`);
        console.log(`🛍️  API Produtos: http://localhost:${PORT}/api/produtos`);
    });
}

startServer().catch(console.error);