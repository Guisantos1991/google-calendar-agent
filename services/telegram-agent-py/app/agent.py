from datetime import datetime, timedelta
from fastapi import HTTPException

from .calendar_client import CalendarCoreClient
from .config import settings
from .models import AgentMessageResponse
from .nlu import parse_intent

_calendar_client = CalendarCoreClient()


async def handle_agent_text(text: str) -> AgentMessageResponse:
    intent = parse_intent(text)

    if intent.action == "create":
        result = await _calendar_client.create_event(
            {
                "title": intent.title,
                "start": intent.start,
                "end": intent.end,
                "timezone": settings.default_timezone,
            }
        )
        return AgentMessageResponse(
            status="ok",
            message="Evento criado com sucesso.",
            data=result,
        )

    if intent.action == "list":
        now = datetime.now()
        to_dt = now + timedelta(days=1)
        events = await _calendar_client.list_events(now.isoformat(), to_dt.isoformat())
        return AgentMessageResponse(
            status="ok",
            message="Eventos recuperados.",
            data={"events": events},
        )

    if intent.action == "cancel":
        try:
            await _calendar_client.cancel_event(intent.event_id or "")
        except Exception as exc:  # noqa: BLE001
            raise HTTPException(status_code=404, detail="Evento não encontrado") from exc

        return AgentMessageResponse(status="ok", message="Evento cancelado.")

    return AgentMessageResponse(
        status="need_confirmation",
        message="Não entendi. Use: 'criar <título>', 'listar', ou 'cancelar <id>'.",
    )

