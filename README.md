# BossDrop 🎮

> O seu agregador definitivo de promoções de jogos para PC.

O **BossDrop** é um aplicativo Android nativo que monitora e agrega as melhores ofertas de jogos digitais de diversas lojas (Steam, Epic Games, GOG, Nuuvem, etc.), permitindo que o usuário encontre preços baixos, favorite seus jogos desejados e receba notificações automáticas de desconto.

---

## 📋 Índice
- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Arquitetura e Tecnologias](#-arquitetura-e-tecnologias)
- [Relatórios de Testes](#-relatórios-de-testes-qa)
- [Configuração e Segurança](#-configuração-e-segurança)
- [Autores](#-autores)

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
## ☁️ Cloud Functions

O "cérebro" do BossDrop é um robô (`index.js`) agendado que executa o ciclo ETL (Extract, Transform, Load):

1.  **Coleta:** Identifica jogos populares e varre as listas de desejos de todos os usuários (`wishlist_games`).
2.  **Consulta:** Busca preços atualizados na API externa para milhares de IDs.
3.  **Processamento:** Filtra lojas confiáveis (Steam, Nuuvem, Epic, etc.) e unifica os dados.
4.  **Persistência:** Atualiza a coleção `promocoes_br_v3` no Firestore.
5.  **Notificação:** Detecta quedas de preço em jogos monitorados e dispara mensagens FCM para os usuários interessados.

---

## 🧪 Relatórios de Testes (QA)

A qualidade do código é garantida através de baterias de testes automatizados. Você pode consultar os relatórios detalhados de execução hospedados no Firebase Hosting:

| Tipo de Teste | Descrição | Resultado (HTML) |
| :--- | :--- | :--- |
| **Testes Unitários** | Validação de lógica de ViewModels, Repositórios e utilitários locais. | [📊 Ver Relatório Unitário](https://appbossdrop.web.app/relatorios/unitarios/) |
| **Testes Instrumentados** | Validação de UI e integração com componentes Android (Contexto real). | [📱 Ver Relatório Instrumentado](https://appbossdrop.web.app/relatorios/instrumentados/) |

---

## 🔒 Configuração e Segurança

### Autenticação e Proteção de Dados
* **Reautenticação:** Para alterar e-mail ou senha, o app exige que o usuário confirme sua senha atual, prevenindo acesso não autorizado em sessões antigas.
* **Firebase Identity Platform:** O projeto utiliza a infraestrutura moderna do Google Cloud Identity para gestão de usuários.
    

---

## 👥 Autores

Este projeto foi desenvolvido por:

* **Murilo Pacheco**
* **Pierre de Sá**
* **Guilherme Augusto**
* **Italo Lira**
* **Giovanni Jesus**
* **Vinicyus Rodrigues**

---
