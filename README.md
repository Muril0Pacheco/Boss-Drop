# BossDrop 🎮

> O seu agregador definitivo de promoções de jogos para PC.

O **BossDrop** é um aplicativo Android nativo que monitora e agrega as melhores ofertas de jogos digitais de diversas lojas (Steam, Epic Games, GOG, Nuuvem, etc.), permitindo que o usuário encontre preços baixos, favorite seus jogos desejados e receba notificações automáticas de desconto.

---

## 📋 Índice
- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Arquitetura e Tecnologias](#-arquitetura-e-tecnologias)
- [Estrutura do Backend](#-estrutura-do-backend-cloud-functions)
- [Relatórios de Testes](#-relatórios-de-testes-qa)
- [Configuração e Segurança](#-configuração-e-segurança)

---

## 🚀 Sobre o Projeto

Este projeto foi desenvolvido para resolver o problema da fragmentação de preços em jogos de PC. Utilizando a API da *IsThereAnyDeal* e um backend serverless no Firebase, o BossDrop mantém uma base de dados atualizada e notifica os usuários proativamente sobre quedas de preço em sua lista de desejos.

**Status:**  Em Alpha / Teste Fechado

---

## ✨ Funcionalidades

* **Feed de Promoções:** Lista curada e atualizada automaticamente com as melhores ofertas do momento, com destaque visual para jogos "Grátis".
* **Busca Híbrida:** Pesquisa instantânea de jogos na base de dados local (cache) ou externa.
* **Detalhes do Jogo:** Visualização rica com capa (poster), banner ambiental, comparação de preços (atual vs. histórico) e link direto para a loja.
* **Lista de Desejos (Favoritos):** Sistema de "coração" ❤️ que salva jogos de interesse do usuário e inscreve o dispositivo para receber alertas.
* **Notificações Push:** Alertas em tempo real via FCM (Firebase Cloud Messaging) quando um jogo favoritado entra em promoção.
* **Gestão de Conta Segura:** Sistema completo de autenticação (Login, Cadastro, Google Sign-In) com segurança reforçada para alteração de dados sensíveis (reautenticação obrigatória).

---

## 🛠 Arquitetura e Tecnologias

### Android (Cliente)
* **Linguagem:** Kotlin
* **Arquitetura:** MVVM (Model-View-ViewModel) + Repository Pattern
* **UI:** XML com ViewBinding
* **Bibliotecas Principais:**
    * `Firebase BOM` (Auth, Firestore, Messaging, Analytics)
    * `Glide` (Gestão e cache de imagens)
    * `Coroutines` & `LiveData` (Assincronismo e Estado)
    * `Play Services Auth` (Integração Google Sign-In)

### Backend (Serverless)
* **Plataforma:** Firebase Cloud Functions (2nd Gen)
* **Linguagem:** Node.js
* **Banco de Dados:** Cloud Firestore (NoSQL)
* **Integração:** Axios para consumo da API *IsThereAnyDeal*

---
