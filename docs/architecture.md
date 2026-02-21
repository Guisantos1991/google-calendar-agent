# Arquitetura MVP

## Fluxo principal

1. Usuário envia mensagem no Telegram
2. `telegram-agent-py` interpreta intenção (NLU simples por regras)
3. `telegram-agent-py` chama `calendar-core-java`
4. `calendar-core-java` executa operação de calendário
5. Resposta retorna ao usuário

## Serviços

### telegram-agent-py (FastAPI)
- Endpoint de entrada: `POST /agent/message`
- Endpoint de saúde: `GET /health`
- Responsável por:
  - interpretação simples de texto
  - roteamento para API Java
  - mensagens de confirmação quando necessário

### calendar-core-java (Spring Boot)
- Endpoints:
  - `POST /calendar/events`
  - `GET /calendar/events`
  - `DELETE /calendar/events/{id}`
  - `GET /oauth/status`
- Endpoint de saúde: `GET /actuator/health`
- Responsável por:
  - regras de calendário
  - (próxima etapa) integração OAuth + Google Calendar

## Limites do MVP atual
- Armazenamento em memória
- Single-user
- NLU de baixa complexidade
