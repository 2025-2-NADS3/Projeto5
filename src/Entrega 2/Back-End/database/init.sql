CREATE DATABASE IF NOT EXISTS comendoria_db;
USE comendoria_db;

-- Tabela de Produtos
CREATE TABLE produtos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10,2) NOT NULL,
    categoria VARCHAR(50),
    disponivel BOOLEAN DEFAULT true,
    imagem_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabela de Pedidos
CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    total DECIMAL(10,2) NOT NULL,
    status ENUM('pendente', 'confirmado', 'preparando', 'pronto', 'entregue') DEFAULT 'pendente',
    observacao TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabela de Itens do Pedido
CREATE TABLE pedido_itens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT,
    produto_id INT,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (produto_id) REFERENCES produtos(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Dados de exemplo
INSERT INTO produtos (nome, descricao, preco, categoria, imagem_url) VALUES
('Coxinha de Frango', 'Coxinha de frango com catupiry cremoso', 6.50, 'Salgado', 'https://example.com/coxinha.jpg'),
('Pastel de Carne', 'Pastel frito recheado com carne moída', 5.00, 'Salgado', 'https://example.com/pastel.jpg'),
('Refrigerante Lata', 'Lata 350ml - Coca-Cola, Guaraná, Fanta', 4.50, 'Bebida', 'https://example.com/refri.jpg'),
('Suco Natural', 'Copo 300ml - Laranja, Limão, Maracujá', 5.50, 'Bebida', 'https://example.com/suco.jpg'),
('Bolo de Chocolate', 'Fatia de bolo de chocolate com calda', 7.00, 'Doce', 'https://example.com/bolo.jpg'),
('Café Expresso', 'Café expresso tradicional 50ml', 3.00, 'Bebida', 'https://example.com/cafe.jpg');

INSERT INTO pedidos (usuario_id, total, status) VALUES
(1, 16.00, 'entregue'),
(2, 11.50, 'preparando');

INSERT INTO pedido_itens (pedido_id, produto_id, quantidade, preco_unitario) VALUES
(1, 1, 2, 6.50),  -- 2 coxinhas
(1, 3, 1, 4.50),  -- 1 refrigerante
(2, 2, 1, 5.00),  -- 1 pastel
(2, 4, 1, 5.50);  -- 1 suco