# ProjetoX — Agente Telegram + Google Calendar

MVP pessoal para portfólio com dois serviços:

- `services/telegram-agent-py`: API rápida em Python (FastAPI) para receber texto e transformar em intenção
- `services/calendar-core-java`: API em Java (Spring Boot) para operações de calendário

## Escopo V1

- Criar evento
- Listar eventos (dia/semana)
- Cancelar evento
- Single-user

## Arquitetura (MVP)

`Telegram -> Python API -> Java API -> (Google Calendar na próxima etapa)`

Nesta primeira implementação, o Java roda com armazenamento em memória para acelerar validação de fluxo.

## Como rodar local

1. Copie `.env.example` para `.env`
2. Suba os serviços:

```bash
docker compose up --build
```

3. Healthchecks:

- Python: `GET http://localhost:8000/health`
- Java: `GET http://localhost:8080/actuator/health`

## Endpoints principais

### Python

- `POST /agent/message`

### Java

- `POST /calendar/events`
- `GET /calendar/events?from=...&to=...`
- `DELETE /calendar/events/{id}`

## Próximos passos

- Integrar OAuth Google Calendar no serviço Java
- Integrar API Telegram (webhook/polling) no serviço Python
- Implementar modo de suspensão/wake em deploy PaaS
