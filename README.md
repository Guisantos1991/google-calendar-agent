<p align="center">
  <img src="https://img.icons8.com/color/96/google-calendar--v1.png" alt="Google Calendar" width="80"/>
  <img src="https://img.icons8.com/color/96/telegram-app--v1.png" alt="Telegram" width="80"/>
  <img src="https://img.icons8.com/color/96/python--v1.png" alt="Python" width="80"/>
  <img src="https://img.icons8.com/color/96/java-coffee-cup-logo--v1.png" alt="Java" width="80"/>
</p>

<h1 align="center">📅 Google Calendar Agent</h1>

<p align="center">
  <b>Assistente inteligente de agenda via Telegram com processamento de linguagem natural</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Python-3.12-3776AB?style=for-the-badge&logo=python&logoColor=white"/>
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/FastAPI-0.100+-009688?style=for-the-badge&logo=fastapi&logoColor=white"/>
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/status-em%20desenvolvimento-yellow?style=flat-square"/>
  <img src="https://img.shields.io/badge/tipo-portfolio-blueviolet?style=flat-square"/>
  <img src="https://img.shields.io/badge/licença-pessoal-lightgrey?style=flat-square"/>
</p>

---

## 📌 Sobre o Projeto

> 🎯 **Projeto de portfólio e prática** — construído para demonstrar habilidades em **Python** e **Java**, aplicando conceitos de **arquitetura de microsserviços**, **processamento de linguagem natural (NLP)** e **integração com APIs externas**.

O **Google Calendar Agent** é um bot do Telegram que permite gerenciar sua agenda do Google Calendar usando **linguagem natural em português**. Basta enviar mensagens como *"marcar reunião amanhã às 14h"* ou *"o que tenho hoje?"* e o bot interpreta, executa a ação e responde.

### ✨ Destaques

| | Destaque | Descrição |
|---|---|---|
| 🧠 | **NLP sem IA generativa** | Detecção de intenções e extração de entidades com regex e patterns — leve e determinístico |
| 🏗️ | **Microsserviços** | Dois serviços independentes comunicando via REST |
| 🐍 | **Python + FastAPI** | Agente de NLP rápido e modular |
| ☕ | **Java 21 + Spring Boot 4** | API robusta com integração Google Calendar e Telegram |
| 🐳 | **Docker Compose** | Orquestração completa com um único comando |
| 🇧🇷 | **Português nativo** | Suporte completo a acentos, variações e linguagem coloquial |

---

## 🏛️ Arquitetura do Sistema

```
┌─────────────────┐         ┌──────────────────┐         ┌──────────────────┐
│                 │         │                  │         │                  │
│    Telegram     │◄───────►│  calendar-bot-api│◄───────►│   agno-agent     │
│    (Usuário)    │  HTTP   │  (Java/Spring)   │  REST   │  (Python/FastAPI)│
│                 │         │     :8080         │         │     :8000        │
└─────────────────┘         └────────┬─────────┘         └──────────────────┘
                                     │
                                     ▼
                            ┌──────────────────┐
                            │  Google Calendar  │
                            │      API          │
                            └──────────────────┘
```

### 🔄 Fluxo Completo da Requisição

```
 👤 Usuário                  ☕ Java/Spring Boot              🐍 Python/FastAPI
    │                              │                               │
    │  "Marcar dentista            │                               │
    │   amanhã às 10h"            │                               │
    │─────────────────────────────►│                               │
    │        Telegram Polling      │                               │
    │                              │  POST /chat                   │
    │                              │  {user_id, timezone,          │
    │                              │   now, message}               │
    │                              │──────────────────────────────►│
    │                              │                               │ 1. Detecta intenção
    │                              │                               │    → CREATE_EVENT
    │                              │                               │ 2. Extrai entidades
    │                              │                               │    → título: "dentista"
    │                              │                               │    → data: amanhã
    │                              │                               │    → hora: 10:00
    │                              │  {action: "CREATE_EVENT",     │
    │                              │   confidence: 0.85,           │
    │                              │   args: {summary, start_time}}│
    │                              │◄──────────────────────────────│
    │                              │                               │
    │                              │  Dispatcher roteia ação       │
    │                              │  → Google Calendar API        │
    │                              │  → Cria evento                │
    │  "✅ Agendado: Dentista      │                               │
    │   para 11/03/2026 às 10:00" │                               │
    │◄─────────────────────────────│                               │
```

---

## 🧩 Componentes

### 🐍 Agno Agent — Serviço Python

> **Responsabilidade:** Interpretar mensagens em linguagem natural e devolver ações estruturadas.

| Módulo | Descrição |
|---|---|
| 📡 `app.py` | Entry point FastAPI — expõe `/chat` e `/health` |
| 🔀 `router.py` | Orquestra detecção de intenção + extração + handler |
| 🧠 `intent/` | Detecção de intenções via pattern matching |
| 🔍 `extractors/` | Extração de entidades (data, hora, título) |
| ⚙️ `handlers/` | Handlers especializados por tipo de ação |
| 📦 `models/` | DTOs com Pydantic (request/response/extracted data) |

#### 🧠 Detecção de Intenções

O `IntentDetector` identifica **10 intenções** distintas através de padrões de linguagem natural:

```
┌─────────────────────────────────────────────────────┐
│                  INTENÇÕES SUPORTADAS               │
├──────────────────┬──────────────────────────────────┤
│ 📝 CREATE_EVENT  │ "marcar", "agendar", "criar"     │
│ ❌ CANCEL_EVENT  │ "cancelar", "desmarcar", "apagar"│
│ 📋 LIST_TODAY    │ "hoje", "agenda de hoje"          │
│ 📋 LIST_TOMORROW │ "amanhã", "agenda de amanhã"     │
│ 📋 LIST_NEXT     │ "próximos compromissos"           │
│ 📋 LIST_WEEK     │ "essa semana", "da semana"        │
│ 📋 LIST_NEXT_WEEK│ "semana que vem", "próxima semana"│
│ 📋 LIST_MONTH    │ "esse mês", "do mês"             │
│ 📋 LIST_NEXT_MONTH│ "mês que vem", "próximo mês"    │
│ ❓ HELP          │ "/start", "ajuda", "help"        │
└──────────────────┴──────────────────────────────────┘
```

> 💡 **Normalização inteligente:** Remove acentos, converte para lowercase e gera variantes automáticas de cada padrão (`próximo` → `proximo`), garantindo matching robusto.

#### 🔍 Extratores de Entidades

| Extrator | O que extrai | Exemplos |
|---|---|---|
| 📅 `DateExtractor` | Datas relativas e absolutas | `"amanhã"`, `"dia 15"`, `"12 de março"`, `"15/03"`, `"próxima segunda"` |
| ⏰ `TimeExtractor` | Horários e intervalos | `"14h"`, `"15:30"`, `"das 10h às 11h"` |
| 📝 `TitleExtractor` | Título/nome do evento | `"reunião com cliente"`, `"dentista"`, `"almoço"` |
| 🎯 `EntityExtractor` | Coordenador geral | Orquestra os 3 extratores acima |

#### ⚙️ Handlers (Strategy Pattern)

```
                    BaseHandler (ABC)
                         │
          ┌──────────────┼──────────────────┐
          │              │                  │
   CreateEventHandler  ListHandlers   CancelEventHandler
          │              │                  │
          │    ┌─────────┼─────────┐        │
          │    │         │         │        │
          │  Today    Tomorrow   Week      │
          │  Next    NextWeek   Month      │
          │         NextMonth              │
          │                                │
     HelpHandler              ClarifyHandler
```

| Handler | Ação |
|---|---|
| `CreateEventHandler` | Valida campos (título, data, hora) → monta ISO datetime ou pede clarificação |
| `ListTodayHandler` | Retorna ação `LIST_TODAY` |
| `ListTomorrowHandler` | Retorna ação `LIST_TOMORROW` |
| `ListNextHandler` | Retorna ação `LIST_NEXT` |
| `ListWeekHandler` | Retorna ação `LIST_WEEK` |
| `ListNextWeekHandler` | Retorna ação `LIST_NEXT_WEEK` |
| `ListMonthHandler` | Retorna ação `LIST_MONTH` |
| `ListNextMonthHandler` | Retorna ação `LIST_NEXT_MONTH` |
| `CancelEventHandler` | Retorna ação `CANCEL_EVENT` com query de busca |
| `HelpHandler` | Retorna menu de ajuda formatado |
| `ClarifyHandler` | Pede clarificação quando a intenção não é reconhecida |

---

### ☕ Calendar Bot API — Serviço Java

> **Responsabilidade:** Integrar com Telegram e Google Calendar, executando as ações interpretadas pelo agente.

```
com.guidev.googlecalendaragent
├── 📡 client/                      # Clientes HTTP
│   ├── AgnoClient.java             # Comunicação REST com o agno-agent
│   ├── GoogleCalendarClient.java   # Integração com Google Calendar API
│   └── TelegramClient.java         # Comunicação com Telegram Bot API
├── ⚙️ config/
│   └── RestClientConfig.java       # Configuração dos RestClients (Agno + Telegram)
├── 🌐 controller/
│   └── TelegramWebhookController.java  # Endpoint de webhook + teste
├── 📦 dto/
│   ├── agentDTO/                   # DTOs de comunicação com o agno-agent
│   │   ├── AgentRequest.java       # record: user_id, timezone, now, message
│   │   └── AgentResponse.java      # record: action, confidence, args
│   ├── messageDTO/                 # DTOs de mensagens do Telegram
│   │   ├── Chat.java               # record: id, type
│   │   ├── From.java               # record: id, username, firstName, lastName
│   │   └── Message.java            # record: messageId, chat, from, text
│   └── telegramDTO/                # DTOs do Telegram API
│       ├── TelegramGetResponse.java      # record: ok, result
│       ├── TelegramSendMessageRequest.java  # record: chatId, text, parseMode
│       └── TelegramUpdate.java           # record: updateId, message
├── 🔧 service/
│   ├── AgentActionDispatcher.java  # Roteia ações do agente → handlers apropriados
│   ├── CalendarEventFormatter.java # Formata eventos em mensagens legíveis (HTML)
│   └── TelegramMessageHandler.java # Processa updates do Telegram end-to-end
└── 📡 telegram/
    └── TelegramPollingService.java # Long polling do Telegram (Virtual Threads)
```

#### 🔄 Ciclo de Vida de uma Mensagem (Java)

```
TelegramPollingService          TelegramMessageHandler
     │ (long polling)                    │
     │ getUpdates()                      │
     │─────────────────────────────────►│
     │                                   │ agnoClient.interpret()
     │                                   │────────► Agno Agent (Python)
     │                                   │◄──────── AgentResponse
     │                                   │
     │                                   │ actionDispatcher.dispatch()
     │                                   │────────► CalendarEventFormatter
     │                                   │          │
     │                                   │          ├─► googleCalendarClient.listToday()
     │                                   │          ├─► googleCalendarClient.createEvent()
     │                                   │          └─► googleCalendarClient.deleteEvent()
     │                                   │◄────────
     │                                   │
     │                                   │ telegramClient.sendMessage()
     │                                   │────────► Telegram API
```

---

## 📡 API Contract

### `POST /chat` — Agno Agent

**Request:**
```json
{
  "user_id": "123456",
  "timezone": "America/Sao_Paulo",
  "now": "2026-03-10T14:30:00",
  "message": "marcar dentista amanhã às 10h"
}
```

**Response (Criar Evento):**
```json
{
  "action": "CREATE_EVENT",
  "confidence": 0.85,
  "args": {
    "summary": "Dentista",
    "start_time": "2026-03-11T10:00:00",
    "end_time": "2026-03-11T11:00:00"
  }
}
```

**Response (Listar):**
```json
{
  "action": "LIST_TODAY",
  "confidence": 0.9,
  "args": {}
}
```

**Response (Clarificação):**
```json
{
  "action": "ASK_CLARIFY",
  "confidence": 0.6,
  "args": {
    "clarify_question": "Qual horário? Ex: '14h', '15:30', 'das 10h às 11h'"
  }
}
```

### `GET /health` — Health Check

```json
{
  "status": "ok",
  "version": "2.0.0"
}
```

---

## 🚀 Funcionalidades

### 📋 Consultar Agenda

| Comando (exemplo) | Ação |
|---|---|
| 💬 `"O que tenho hoje?"` | Lista compromissos de hoje |
| 💬 `"Amanhã"` | Lista compromissos de amanhã |
| 💬 `"Próximos compromissos"` | Lista os próximos 10 eventos |
| 💬 `"Agenda da semana"` | Lista eventos da semana atual |
| 💬 `"Semana que vem"` | Lista eventos da próxima semana |
| 💬 `"Esse mês"` | Lista eventos do mês atual |
| 💬 `"Mês que vem"` | Lista eventos do próximo mês |

### 📝 Criar Eventos

| Comando (exemplo) | Resultado |
|---|---|
| 💬 `"Marcar reunião amanhã às 14h"` | ✅ Cria evento com título, data e hora |
| 💬 `"Agendar almoço dia 15 às 12h"` | ✅ Cria evento com data absoluta |
| 💬 `"Criar call segunda das 10h às 11h"` | ✅ Cria evento com intervalo de horário |
| 💬 `"Agendar dentista"` | 🤔 Pede data e horário faltantes |

### ❌ Cancelar Eventos

| Comando (exemplo) | Resultado |
|---|---|
| 💬 `"Cancelar reunião"` | Busca e cancela o evento pelo nome |
| 💬 `"Desmarcar dentista"` | Busca nos próximos 30 dias e remove |

### ❓ Ajuda

| Comando (exemplo) | Resultado |
|---|---|
| 💬 `"/start"` | Exibe menu de ajuda completo |
| 💬 `"Ajuda"` | Exibe menu de ajuda completo |
| 💬 `"O que você faz?"` | Exibe menu de ajuda completo |

---

## 🛠️ Tecnologias Utilizadas

### 🐍 Agno Agent (Python)

| Tecnologia | Uso |
|---|---|
| ![Python](https://img.shields.io/badge/Python-3.12-3776AB?style=flat-square&logo=python&logoColor=white) | Linguagem principal |
| ![FastAPI](https://img.shields.io/badge/FastAPI-0.100+-009688?style=flat-square&logo=fastapi&logoColor=white) | Framework web assíncrono |
| ![Pydantic](https://img.shields.io/badge/Pydantic-2.0+-E92063?style=flat-square&logo=pydantic&logoColor=white) | Validação de dados e DTOs |
| ![Uvicorn](https://img.shields.io/badge/Uvicorn-0.20+-499848?style=flat-square) | Servidor ASGI |

### ☕ Calendar Bot API (Java)

| Tecnologia | Uso |
|---|---|
| ![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white) | Linguagem principal |
| ![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.3-6DB33F?style=flat-square&logo=springboot&logoColor=white) | Framework principal |
| ![Google Calendar](https://img.shields.io/badge/Google_Calendar_API-v3-4285F4?style=flat-square&logo=googlecalendar&logoColor=white) | Integração com agenda |
| ![Telegram](https://img.shields.io/badge/Telegram_Bot_API-26A5E4?style=flat-square&logo=telegram&logoColor=white) | Interface com usuário |
| ![Lombok](https://img.shields.io/badge/Lombok-Produtividade-red?style=flat-square) | Redução de boilerplate |
| ![Jackson](https://img.shields.io/badge/Jackson-JSON-blue?style=flat-square) | Serialização/desserialização |
| ![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=flat-square&logo=apachemaven&logoColor=white) | Gerenciamento de dependências |

### 🐳 Infraestrutura

| Tecnologia | Uso |
|---|---|
| ![Docker](https://img.shields.io/badge/Docker-Containers-2496ED?style=flat-square&logo=docker&logoColor=white) | Containerização |
| ![Docker Compose](https://img.shields.io/badge/Docker_Compose-Orquestração-2496ED?style=flat-square&logo=docker&logoColor=white) | Orquestração multi-container |

---

## 📁 Estrutura do Projeto

```
google-calendar-agent/
│
├── 🐳 docker-compose.yml          # Orquestração dos serviços
├── 📋 requirements.txt             # Dependências Python (raiz)
│
├── 🐍 agno-agent/                  # Serviço de NLP (Python)
│   ├── 🐳 Dockerfile
│   ├── 📋 requirements.txt
│   ├── 🚀 app.py                   # Entry point FastAPI
│   ├── 🧪 test_agent.py            # Testes do agente
│   ├── 🧪 test_intents.py          # Testes de intenções
│   ├── 🔧 debug_test.py            # Testes de debug
│   └── 📦 src/
│       ├── 🔀 router.py            # Roteador de intenções
│       ├── 🧠 intent/
│       │   ├── intent_detector.py   # Detector de intenções
│       │   └── patterns.py          # Padrões de linguagem natural
│       ├── 🔍 extractors/
│       │   ├── entity_extractor.py  # Coordenador de extração
│       │   ├── date_extractor.py    # Extrator de datas
│       │   ├── time_extractor.py    # Extrator de horários
│       │   └── title_extractor.py   # Extrator de títulos
│       ├── ⚙️ handlers/
│       │   ├── base_handler.py      # Interface abstrata (ABC)
│       │   ├── create_event_handler.py
│       │   ├── list_events_handler.py
│       │   ├── cancel_event_handler.py
│       │   ├── help_handler.py
│       │   └── clarify_handler.py
│       └── 📦 models/
│           ├── request.py           # AgentRequest (Pydantic)
│           ├── response.py          # AgentResponse (Pydantic)
│           └── extracted_data.py    # ExtractedData + TimeRange
│
└── ☕ calendar-bot-api/             # Serviço de integração (Java)
    └── googleCalendarAgent/
        ├── 📋 pom.xml               # Dependências Maven
        └── src/main/java/com/guidev/googlecalendaragent/
            ├── 🚀 GoogleCalendarAgentApplication.java
            ├── 📡 client/
            │   ├── AgnoClient.java
            │   ├── GoogleCalendarClient.java
            │   └── TelegramClient.java
            ├── ⚙️ config/
            │   └── RestClientConfig.java
            ├── 🌐 controller/
            │   └── TelegramWebhookController.java
            ├── 📦 dto/
            │   ├── agentDTO/        # AgentRequest, AgentResponse
            │   ├── messageDTO/      # Chat, From, Message
            │   └── telegramDTO/     # TelegramUpdate, TelegramGetResponse, ...
            ├── 🔧 service/
            │   ├── AgentActionDispatcher.java
            │   ├── CalendarEventFormatter.java
            │   └── TelegramMessageHandler.java
            └── 📡 telegram/
                └── TelegramPollingService.java
```

---

## 🎨 Design Patterns Utilizados

| Padrão | Onde | Descrição |
|---|---|---|
| 🏗️ **Strategy** | `handlers/` | Cada intenção tem seu handler concreto com lógica especializada |
| 🔀 **Router / Mediator** | `IntentRouter` | Centraliza o fluxo: detecta → extrai → delega |
| 🏭 **Factory Method** | `AgentResponse` | Class methods (`create_event()`, `list_today()`, etc.) para construir respostas |
| 📦 **DTO / Record** | `models/`, `dto/` | Objetos imutáveis para transporte de dados (Pydantic + Java Records) |
| 🔌 **Dependency Injection** | Spring Boot | `@Component`, `@Service`, `@Configuration` com injeção via construtor |
| 🎯 **Single Responsibility** | Todo o projeto | Cada classe tem uma única responsabilidade bem definida |
| 🧱 **Template Method** | `BaseHandler` | Classe abstrata que define contrato para todos os handlers |

---

## ⚙️ Como Executar

### 📋 Pré-requisitos

- 🐳 Docker e Docker Compose
- 🔑 Credenciais do Google Calendar API (`credentials.json`)
- 🤖 Token de um Bot do Telegram

### 1️⃣ Clone o repositório

```bash
git clone https://github.com/seu-usuario/google-calendar-agent.git
cd google-calendar-agent
```

### 2️⃣ Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
# Agno Agent
AGNO_BASE_URL=http://agno-agent:8000

# Telegram
TELEGRAM_BOT_TOKEN=seu-bot-token
TELEGRAM_API_BASE=https://api.telegram.org
TELEGRAM_WEBHOOK_SECRET=seu-webhook-secret
TELEGRAM_TEST_CHAT_ID=seu-chat-id

# Server
SERVER_PORT=8080
```

### 3️⃣ Configure o Google Calendar

Coloque o arquivo `credentials.json` (obtido no Google Cloud Console) em:

```
calendar-bot-api/googleCalendarAgent/src/main/resources/credentials.json
```

### 4️⃣ Suba os serviços

```bash
docker-compose up --build
```

### ✅ Pronto!

| Serviço | URL | Descrição |
|---|---|---|
| 🐍 Agno Agent | `http://localhost:8000` | API de NLP |
| 🐍 Health Check | `http://localhost:8000/health` | Status do agente |
| ☕ Bot API | `http://localhost:8080` | API do bot |
| ☕ Actuator | `http://localhost:8080/actuator/health` | Health check Spring |

---

## 🐳 Docker Compose

```yaml
services:
  agno-agent:          # 🐍 Serviço Python (NLP)
    build: agno-agent
    ports: ["8000:8000"]
    env_file: [.env]

  bot-api:             # ☕ Serviço Java (Integração)
    build: ./calendar-bot-api
    ports: ["8080:8080"]
    env_file: [.env]
    depends_on: [agno-agent]   # ⚡ Garante que o agente inicia primeiro
```

---

## 💬 Exemplos de Uso

### 📋 Consultar agenda

```
👤 Você: "O que tenho hoje?"
🤖 Bot:  📅 Compromissos de hoje:
         ✅ 09:00 — Standup Diário
         ✅ 14:00 — Reunião com Cliente 📎 Meet
         ✅ 16:30 — Code Review
```

### 📝 Criar evento

```
👤 Você: "Marcar dentista amanhã às 10h"
🤖 Bot:  ✅ Agendado: Dentista para 11/03/2026 às 10:00.
```

### 📝 Criar evento com intervalo

```
👤 Você: "Agendar reunião de planejamento sexta das 14h às 16h"
🤖 Bot:  ✅ Agendado: Reunião De Planejamento para 14/03/2026 das 14:00 às 16:00.
```

### 🤔 Clarificação inteligente

```
👤 Você: "Agendar dentista"
🤖 Bot:  Preciso de mais detalhes: data, horário. Pode informar?
```

### ❌ Cancelar evento

```
👤 Você: "Cancelar reunião com cliente"
🤖 Bot:  ❌ Evento cancelado: Reunião com Cliente
```

### ❓ Ajuda

```
👤 Você: "/start"
🤖 Bot:  👋 Olá! Sou seu assistente de agenda.
         📋 O que posso fazer:
         • Ver sua agenda de hoje ou amanhã
         • Ver próximos compromissos
         • Criar novos eventos
         • Cancelar compromissos
         ...
```

---

## 🧪 Testes

O serviço Python inclui arquivos de teste para validar o comportamento do agente:

```bash
# Dentro do container do agno-agent
python test_agent.py      # Testes gerais do agente
python test_intents.py    # Testes de detecção de intenções
python debug_test.py      # Testes de debug
```

---

## 📊 Resumo das Ações do Agente

```
┌─────────────────┬──────────────────┬──────────────────────────────────────┐
│     Action      │   Confidence     │              Descrição               │
├─────────────────┼──────────────────┼──────────────────────────────────────┤
│ CREATE_EVENT    │     0.85         │ Criar evento com summary + datetime  │
│ CANCEL_EVENT    │     0.75         │ Cancelar evento por nome/query       │
│ LIST_TODAY      │     0.90         │ Listar compromissos de hoje          │
│ LIST_TOMORROW   │     0.90         │ Listar compromissos de amanhã        │
│ LIST_NEXT       │     0.85         │ Listar próximos compromissos         │
│ LIST_WEEK       │     0.85         │ Listar compromissos da semana        │
│ LIST_NEXT_WEEK  │     0.85         │ Listar compromissos da próxima semana│
│ LIST_MONTH      │     0.85         │ Listar compromissos do mês           │
│ LIST_NEXT_MONTH │     0.85         │ Listar compromissos do próximo mês   │
│ HELP            │     1.00         │ Exibir menu de ajuda                 │
│ ASK_CLARIFY     │   0.30-0.60      │ Pedir mais informações ao usuário    │
└─────────────────┴──────────────────┴──────────────────────────────────────┘
```

---

## 🧠 Como a NLP Funciona (Sem IA Generativa)

```
         Mensagem do Usuário
                │
                ▼
    ┌───────────────────────┐
    │     Normalização      │  lowercase + remove acentos + espaços
    └───────────┬───────────┘
                │
                ▼
    ┌───────────────────────┐
    │  Detecção de Intenção │  Pattern matching com prioridade:
    │                       │  1. CANCEL (verbos de ação)
    │                       │  2. CREATE (verbos de ação)
    │                       │  3. LIST_NEXT_WEEK / LIST_NEXT_MONTH
    │                       │  4. LIST_TOMORROW / LIST_TODAY
    │                       │  5. LIST_WEEK / LIST_MONTH
    │                       │  6. LIST_NEXT
    │                       │  7. CREATE (substantivos - fallback)
    │                       │  8. HELP
    └───────────┬───────────┘
                │
                ▼
    ┌───────────────────────┐
    │  Extração de Entidades│
    │  ├─ DateExtractor     │  Relativas, absolutas, dia da semana
    │  ├─ TimeExtractor     │  Horário único ou intervalo
    │  └─ TitleExtractor    │  Padrões + fallback após verbo
    └───────────┬───────────┘
                │
                ▼
    ┌───────────────────────┐
    │   Handler Apropriado  │  Strategy pattern por intenção
    └───────────┬───────────┘
                │
                ▼
          AgentResponse
     {action, confidence, args}
```

---

<p align="center">
  Feito com ❤️ para prática e portfólio
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Python-🐍-3776AB?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Java-☕-ED8B00?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Docker-🐳-2496ED?style=for-the-badge"/>
</p>

