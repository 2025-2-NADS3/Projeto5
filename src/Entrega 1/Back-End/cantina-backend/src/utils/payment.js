const axios = require('axios');

// Exemplo de integração com Mercado Pago
const mercadoPagoService = {
  async createPreference(orderData) {
    try {
      const { items, userId, orderId } = orderData;
      
      const preference = {
        items: items.map(item => ({
          title: item.name,
          unit_price: item.price,
          quantity: item.quantity,
        })),
        back_urls: {
          success: `${process.env.APP_URL}/payment/success`,
          failure: `${process.env.APP_URL}/payment/failure`,
          pending: `${process.env.APP_URL}/payment/pending`
        },
        auto_return: 'approved',
        external_reference: orderId.toString(),
        notification_url: `${process.env.API_URL}/api/payments/notification`
      };
      
      const response = await axios.post(
        'https://api.mercadopago.com/checkout/preferences',
        preference,
        {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${process.env.MP_ACCESS_TOKEN}`
          }
        }
      );
      
      return response.data;
    } catch (error) {
      console.error('Erro ao criar preferência no Mercado Pago:', error.response?.data || error.message);
      throw new Error('Falha ao processar pagamento');
    }
  },
  
  async handleNotification(req) {
    try {
      const { type, data } = req.body;
      
      if (type === 'payment') {
        const paymentId = data.id;
        const paymentInfo = await axios.get(
          `https://api.mercadopago.com/v1/payments/${paymentId}`,
          {
            headers: {
              'Authorization': `Bearer ${process.env.MP_ACCESS_TOKEN}`
            }
          }
        );
        
        const { status, external_reference } = paymentInfo.data;
        const orderId = parseInt(external_reference);
        
        // Atualizar status do pedido no banco de dados
        return { orderId, status };
      }
      
      return null;
    } catch (error) {
      console.error('Erro ao processar notificação:', error);
      throw error;
    }
  }
};

module.exports = mercadoPagoService;