# 🍽️ Comedoria da Tia — Documentação da Entrega

Este documento descreve de forma resumida todas as telas e funcionalidades desenvolvidas para o aplicativo **Comedoria da Tia**, incluindo as áreas de **Cliente** e **Admin**, além de informar que o app agora está **integrado ao Firebase** para gerenciamento de dados.

---

## 📱 Área do Cliente

### **🏠 Home**
- Exibe os produtos em destaque.
- Exibe banners informativos.
- Acesso rápido às categorias.

### **🔍 Busca**
- Campo de busca para encontrar produtos rapidamente.
- Lista de resultados filtrados.

### **💳 Carteira (Protótipo)**
- Tela ilustrativa do futuro recurso de pagamentos internos.
- Mostra saldo fictício e possíveis formas de recarga.

### **🛒 Carrinho**
- Lista de produtos adicionados.
- Opção de alterar quantidade ou remover itens.
- Exibe valor total e botão para confirmar pedido.

### **📄 Detalhes do Produto**
- Mostra imagem, nome, descrição e preço.
- Botão para adicionar ao carrinho.

### **👤 Perfil**

#### **Dados da Conta**
- Usuário pode alterar nome, e-mail, telefone, endereço e demais dados.

#### **🔐 Segurança**
- Alterar senha.
- Excluir conta.
- Opção de sair da conta.

---

## 🛠️ Área do Administrador

### **⚙️ Gerenciamento (Hub de Ações)**
- Tela inicial com opções de navegação para todas as ferramentas do admin:
  - Relatórios
  - Pedidos
  - Edição de Produtos
  - Edição de Banners
  - Edição de Categorias
  - Permissões de Usuários
  - Perfil

### **📊 Relatórios**
- Exibição de métricas gerais (protótipo).
- Informações sobre vendas, produtos e fluxo de pedidos.

### **📦 Pedidos**
- Lista de pedidos enviados pelos clientes.
- Cada pedido possui layout detalhado:
  - Itens
  - Quantidades
  - Preço total
  - Dados do cliente
  - Status

### **🧁 Edição de Produtos**
- **Adicionar** novo produto.
- **Editar** produtos existentes.
- **Excluir** produtos.
- Campos como nome, descrição, imagem e preço.

### **🏷️ Banners e Categorias**
- **Adicionar**, **editar** e **excluir** banners.
- **Adicionar**, **editar** e **excluir** categorias.

### **👤 Perfil (Admin)**
- Funciona igual ao perfil do cliente.
- Editar dados pessoais e configurações da conta.

### **🔑 Permitir Acesso a Usuários**
- Controle de quem pode acessar a área administrativa.
- Opções para ativar/desativar permissões.

---

## 🚧 Observação Importante
 O projeto ainda **não possui integração com API/banco**, então os pedidos feitos pelo cliente são enviados diretamente para o painel do administrador.

---

## 📌 Links dos documentos utilizados no app abaixo:
- [Documento principal do app](https://github.com/2025-2-NADS3/Projeto5/tree/main/src/Entrega%202/Front-End)
- [Documento do Banco de Dados-Firebase](https://exemplo.com)


