const express = require('express');
const { Product } = require('../models');
const router = express.Router();

// Listar todos os produtos disponíveis
router.get('/', async (req, res) => {
  try {
    const products = await Product.findAll({
      where: { isAvailable: true },
      order: [['category', 'ASC'], ['name', 'ASC']]
    });
    
    res.json(products);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Buscar produto por ID
router.get('/:id', async (req, res) => {
  try {
    const product = await Product.findByPk(req.params.id);
    
    if (!product) {
      return res.status(404).json({ error: 'Produto não encontrado' });
    }
    
    res.json(product);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;