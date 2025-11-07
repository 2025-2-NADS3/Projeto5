const jwt = require('jsonwebtoken');
const { User } = require('../models');

const authController = {
  async register(req, res) {
    try {
      const { name, email, password, studentId, phone } = req.body;
      
      // Verificar se usuário já existe
      const existingUser = await User.findOne({ where: { email } });
      if (existingUser) {
        return res.status(400).json({ message: 'Usuário já existe' });
      }
      
      // Criar novo usuário
      const user = await User.create({
        name,
        email,
        password,
        studentId,
        phone
      });
      
      // Gerar token JWT
      const token = jwt.sign(
        { id: user.id, email: user.email },
        process.env.JWT_SECRET,
        { expiresIn: '24h' }
      );
      
      res.status(201).json({
        message: 'Usuário criado com sucesso',
        token,
        user: {
          id: user.id,
          name: user.name,
          email: user.email,
          studentId: user.studentId
        }
      });
    } catch (error) {
      console.error(error);
      res.status(500).json({ message: 'Erro ao criar usuário' });
    }
  },
  
  async login(req, res) {
    try {
      const { email, password } = req.body;
      
      // Encontrar usuário
      const user = await User.findOne({ where: { email } });
      if (!user) {
        return res.status(401).json({ message: 'Credenciais inválidas' });
      }
      
      // Verificar senha
      const isValidPassword = await user.validPassword(password);
      if (!isValidPassword) {
        return res.status(401).json({ message: 'Credenciais inválidas' });
      }
      
      // Gerar token JWT
      const token = jwt.sign(
        { id: user.id, email: user.email },
        process.env.JWT_SECRET,
        { expiresIn: '24h' }
      );
      
      res.json({
        message: 'Login realizado com sucesso',
        token,
        user: {
          id: user.id,
          name: user.name,
          email: user.email,
          studentId: user.studentId
        }
      });
    } catch (error) {
      console.error(error);
      res.status(500).json({ message: 'Erro ao fazer login' });
    }
  }
};

module.exports = authController;