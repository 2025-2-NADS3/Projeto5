const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const connection = require('./config/db');

const app = express();
const PORT = 3001;

app.use(cors());
app.use(bodyParser.json());

// Teste de rota
app.get('/', (req, res) => {
  res.send('Backend rodando!');
});

// ================== CRUD DE USUÁRIOS ==================

// Criar usuário
app.post('/usuarios', (req, res) => {
  const { nome, email, senha, telefone } = req.body;

  const sql = 'INSERT INTO usuarios (nome, email, senha, telefone) VALUES (?, ?, ?, ?)';
  connection.query(sql, [nome, email, senha, telefone], (err, result) => {
    if (err) {
      console.error(err);
      return res.status(500).json({ error: 'Erro ao cadastrar usuário' });
    }
    res.status(201).json({ message: 'Usuário cadastrado com sucesso!', id: result.insertId });
  });
});

// Listar todos os usuários
app.get('/usuarios', (req, res) => {
  connection.query('SELECT * FROM usuarios', (err, results) => {
    if (err) {
      console.error(err);
      return res.status(500).json({ error: 'Erro ao buscar usuários' });
    }
    res.json(results);
  });
});

// ======================================================

app.listen(PORT, () => {
  console.log(`Servidor rodando na porta ${PORT}`);
});
