# App - Comedoria da Tia
<p align="center">
  <a href="https://www.fecap.br/" target="_blank">
    <img src="https://github.com/user-attachments/assets/27727ad3-c71d-4bc5-99c5-c8b667173a81" alt="Logo FECAP" width="250"/>
  </a>
</p>

<h3 align="center">Desenvolvido por <strong>MenuUx</strong> | Fundação Escola de Comércio Álvares Penteado (FECAP)</h3>

---

# 👥 Integrantes

<h3>
  <a href="https://www.linkedin.com/in/oficialvitormelo/">Vitor Melo</a> •
  <a href="https://www.linkedin.com/in/kau%C3%A3-aguiar-9979742b5/">Kauã Daniel</a> •
  <a href="https://www.linkedin.com/in/nathanlucena/">Nathan Lucena</a> •
  <a href="https://www.linkedin.com/in/eriane-dos-santos-oliveira-cfp-pqo-paap-53116292/">Eriane Dias</a>
</h3>



# 👨‍🏫 Orientadores


<h3><a href="https://www.linkedin.com/in/victorbarq/?originalSubdomain=br">Dr. Victor Rosetti de Quiroz</a></h3>


# 🧠 Sobre o Projeto

### 🎯 Objetivos
<h3>
O Comedoria da Tia é um aplicativo mobile desenvolvido para revolucionar a experiência de compra na cantina da FECAP. 
  Atualmente, os estudantes enfrentam longas filas durante os intervalos, gerando atrasos e transtornos.</h3>

Com nossa solução, os usuários podem:

<div style="text-align: left;">

📱 **✅ Realizar pedidos antecipados**  
🛒 **✅ Reservar e garantir seus lanches**  
⏱️ **✅ Evitar filas e aglomerações**  
😊 **✅ Desfrutar de maior comodidade**

</div>

### 🌍 Impacto Esperado
<h3>Além de beneficiar os usuários, o sistema contribuirá para uma melhor gestão da
comedoria, facilitando o controle de pedidos e reduzindo aglomerações. Para assegurar a
qualidade desse processo, será fundamental aplicar boas práticas de desenvolvimento de
software, baseadas em modelos e normas de qualidade, além de realizar testes criteriosos
que garantam confiabilidade, segurança e usabilidade.
Dessa forma, o aplicativo cumprirá sua função principal: melhorar significativamente
a experiência do cliente, tornando a compra de lanches mais simples, eficiente e sem
contratempos.</h3>

## 💻 Tecnologias & Ferramentas Utilizadas

📱 Frontend Mobile

<img src="https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white"/> <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/> 
🔥 Frontend & Mobile

<img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black"/> <img src="https://img.shields.io/badge/Google_Cloud-4285F4?style=for-the-badge&logo=google-cloud&logoColor=white"/>
🔥 Backend & Banco de Dados

<img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white"/> <img src="https://img.shields.io/badge/Adobe_XD-FF61F6?style=for-the-badge&logo=adobexd&logoColor=white"/>
🎨 Design & UX

<img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"/> 
📦 Versionamento & Gestão

<div align="center">

| 🚀 Funcionalidade | 💡 Benefício |
|-------------------|--------------|
| ✅ **Aplicação Nativa Android** | Melhor performance e acesso a APIs do dispositivo |
| ✅ **Firebase Integration** | Sincronização em tempo real e autenticação simplificada |
| ✅ **Design System** | Interface consistente seguindo Material Design |
| ✅ **Arquitetura MVC** | Separação clara de responsabilidades |
| ✅ **Desenvolvimento Ágil** | Iterações rápidas baseadas em feedback dos usuários |

</div>
## 📊 Arquitetura do Sistema

```mermaid

graph TB
    A[📱 Aplicativo Android] --> B[🔥 Firebase]
    B --> C[☁️ Google Cloud]
    B --> D[🗃️ Firestore]
    B --> E[🔐 Firebase Auth]
    F[🎨 Figma] --> A
    G[⚙️ Android Studio] --> A
    
    style A fill:#3DDC84
    style B fill:#FFCA28
    style C fill:#4285F4
    style D fill:#FFCA28
    style E fill:#FFCA28
    style F fill:#F24E1E
    style G fill:#3DDC84

   ```


### 🛠 Estrutura de Pasta

📁 Estrutura de Pastas — Projeto Comedoria da Tia (MenuUX / FECAP)
🗂 Documentos/

Pasta principal para entregas teóricas e relatórios acadêmicos.
```
Comedoria-da-Tia/
│
├── 📁 Documentos/(Entregas teóricas e relatórios acadêmicos organizados por disciplina)
│   ├── 📁 Entrega_1/
│   │   ├── 📄 Programação_Mobile/
│   │   ├── ⚙️ Sistemas_Operacionais_Cloud/
│   │   ├── 🧪 Testes_Qualidade_Software/
│   │   └── 🎨 User_Experience_Digital/
│   │
│   ├── 📁 Entrega_2/
│   │   ├── 📄 Programação_Mobile/
│   │   ├── ⚙️ Sistemas_Operacionais_Cloud/
│   │   ├── 🧪 Testes_Qualidade_Software/
│   │   └── 🎨 User_Experience_Digital/
│   │
│   └── 📘 FECAP_ADS3_MenuUX.pptx.pdf
│
└── 💻 src/(Contém o código-fonte prático do projeto — dividido também por entrega)
    ├── Entrega_1/
    │   ├── Back-End/cantina-backend/
    │   └── Front-End/ComedoriaTia/
    │
    └── Entrega_2/
        ├── Back-End/
        └── Front-End/
```

📝 Descrição:

Cada Entrega representa uma fase de desenvolvimento do projeto (ex: Entrega 1 = protótipo inicial; Entrega 2 = evolução com novas funcionalidades).

Dentro de cada entrega, há pastas específicas de cada disciplina envolvida na PI:

Programação Mobile: código ou relatórios focados no app.

Sistemas Operacionais e Cloud Native: configurações de servidor, cloud, ou arquitetura.

Testes e Qualidade de Software (DevOps): scripts e relatórios de testes automatizados ou pipelines.

User Experience Digital: design, protótipos, personas, fluxos e justificativas de UX/UI.

O arquivo FECAP_ADS3_MenuUX.pptx.pdf é a apresentação oficial do grupo (provavelmente usada na banca ou PI).


🧠 Descrição:

Entrega 1: contém o primeiro protótipo funcional — com o backend em uma pasta chamada cantina-backend e o frontend em ComedoriaTia.

Entrega 2: representa a nova versão (melhorada e atualizada) do sistema, mantendo a separação entre back-end e front-end.

## 📋 Licença/License
<div align="center">
<a href="https://github.com/2025-2-NADS3/Projeto5">Comedoria da Tia</a> © 2025 by <a href="https://github.com/2025-2-NADS3/Projeto5"> [Eriane Dias](#), [Kauã Daniel](#), [Nathan Lucena](#), [Vitor Melo](#)</a> is licensed under <a href="https://creativecommons.org/licenses/by/4.0/">CC BY 4.0</a><img src="https://mirrors.creativecommons.org/presskit/icons/cc.svg" alt="" style="max-width: 1em;max-height:1em;margin-left: .2em;"><img src="https://mirrors.creativecommons.org/presskit/icons/by.svg" alt="" style="max-width: 1em;max-height:1em;margin-left: .2em;">
</div>

## 🎓 Referências


Norma ISO/IEC 25010 - Qualidade de Produto de Software
Framework HEART para métricas de UX
Metodologia INVEST para User Stories
Princípios de Cloud Native Architecture
