# telegram-agent-py

Small FastAPI service that interprets natural-language commands and talks to a calendar core API.

## Setup

1. Create your env file based on `.env.example`.
2. Fill in `CALENDAR_CORE_BASE_URL` and optional Telegram vars.
3. Use Python 3.13.x (3.14 is not supported by `pydantic-core` yet).

### Virtualenv

```bash
python3.13 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

## Run locally

```bash
python -m app
```

Or with Uvicorn directly:

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

## Telegram quick test (polling)

Set `TELEGRAM_BOT_TOKEN` in your `.env`, then run:

```bash
python -m app.telegram_runner
```

## Endpoints

- `GET /health`
- `POST /agent/message`

### Example payload

```json
{ "text": "criar reunião com time" }
```
