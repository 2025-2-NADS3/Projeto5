const sequelize = require('../config/database');

// Importar modelos
const User = require('./User');
const Product = require('./Product');
const Order = require('./Order');
const OrderItem = require('./Order_Item');

// Definir associações
User.hasMany(Order, { foreignKey: 'user_id' });
Order.belongsTo(User, { foreignKey: 'user_id' });

Order.hasMany(OrderItem, { foreignKey: 'order_id' });
OrderItem.belongsTo(Order, { foreignKey: 'order_id' });

Product.hasMany(OrderItem, { foreignKey: 'product_id' });
OrderItem.belongsTo(Product, { foreignKey: 'product_id' });

// Exportar modelos
module.exports = {
  sequelize,
  User,
  Product,
  Order,
  OrderItem
};