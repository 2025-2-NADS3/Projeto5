const express = require('express');
const router = express.Router();
const {
    getAllProdutos,
    getProdutoById,
    createProduto,
    updateProduto,
    deleteProduto
} = require('../controllers/produtosController');

router.get('/', getAllProdutos);
router.get('/:id', getProdutoById);
router.post('/', createProduto);
router.put('/:id', updateProduto);
router.delete('/:id', deleteProduto);

module.exports = router;