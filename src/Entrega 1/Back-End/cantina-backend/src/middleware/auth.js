const jwt = require('jsonwebtoken');
const { User } = require('../models');

const authMiddleware = {
  verifyToken: (req, res, next) => {
    const token = req.headers.authorization?.split(' ')[1];
    
    if (!token) {
      return res.status(401).json({ message: 'Token de acesso necessário' });
    }
    
    try {
      const decoded = jwt.verify(token, process.env.JWT_SECRET);
      req.userId = decoded.id;
      next();
    } catch (error) {
      return res.status(401).json({ message: 'Token inválido' });
    }
  },
  
  isAdmin: async (req, res, next) => {
    try {
      const user = await User.findByPk(req.userId);
      
      if (!user || !user.isAdmin) {
        return res.status(403).json({ message: 'Acesso restrito a administradores' });
      }
      
      next();
    } catch (error) {
      console.error(error);
      res.status(500).json({ message: 'Erro ao verificar permissões' });
    }
  }
};

module.exports = authMiddleware;