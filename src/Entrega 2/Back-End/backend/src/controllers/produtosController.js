const { pool } = require('../config/database');

// GET - Listar todos os produtos
const getAllProdutos = async (req, res) => {
    try {
        const [rows] = await pool.execute(`
            SELECT * FROM produtos 
            WHERE disponivel = true 
            ORDER BY created_at DESC
        `);
        res.json({
            success: true,
            data: rows,
            total: rows.length
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            error: error.message
        });
    }
};

// GET - Buscar produto por ID
const getProdutoById = async (req, res) => {
    try {
        const [rows] = await pool.execute(
            'SELECT * FROM produtos WHERE id = ? AND disponivel = true',
            [req.params.id]
        );
        
        if (rows.length === 0) {
            return res.status(404).json({
                success: false,
                error: 'Produto não encontrado'
            });
        }
        
        res.json({
            success: true,
            data: rows[0]
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            error: error.message
        });
    }
};

// POST - Criar novo produto
const createProduto = async (req, res) => {
    try {
        const { nome, descricao, preco, categoria, imagem_url } = req.body;
        
        // Validações básicas
        if (!nome || !preco || !categoria) {
            return res.status(400).json({
                success: false,
                error: 'Nome, preço e categoria são obrigatórios'
            });
        }
        
        const [result] = await pool.execute(
            'INSERT INTO produtos (nome, descricao, preco, categoria, imagem_url) VALUES (?, ?, ?, ?, ?)',
            [nome, descricao, preco, categoria, imagem_url]
        );
        
        res.status(201).json({
            success: true,
            message: 'Produto criado com sucesso',
            data: { id: result.insertId }
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            error: error.message
        });
    }
};

// PUT - Atualizar produto
const updateProduto = async (req, res) => {
    try {
        const { nome, descricao, preco, categoria, disponivel, imagem_url } = req.body;
        const produtoId = req.params.id;
        
        const [result] = await pool.execute(
            `UPDATE produtos 
             SET nome=?, descricao=?, preco=?, categoria=?, disponivel=?, imagem_url=?
             WHERE id=?`,
            [nome, descricao, preco, categoria, disponivel, imagem_url, produtoId]
        );
        
        if (result.affectedRows === 0) {
            return res.status(404).json({
                success: false,
                error: 'Produto não encontrado'
            });
        }
        
        res.json({
            success: true,
            message: 'Produto atualizado com sucesso'
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            error: error.message
        });
    }
};

// DELETE - Deletar produto (soft delete)
const deleteProduto = async (req, res) => {
    try {
        const [result] = await pool.execute(
            'UPDATE produtos SET disponivel = false WHERE id = ?',
            [req.params.id]
        );
        
        if (result.affectedRows === 0) {
            return res.status(404).json({
                success: false,
                error: 'Produto não encontrado'
            });
        }
        
        res.json({
            success: true,
            message: 'Produto deletado com sucesso'
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            error: error.message
        });
    }
};

module.exports = {
    getAllProdutos,
    getProdutoById,
    createProduto,
    updateProduto,
    deleteProduto
};