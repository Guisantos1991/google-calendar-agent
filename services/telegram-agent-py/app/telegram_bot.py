from typing import Iterable

from telegram import Update
from telegram.ext import Application, ContextTypes, MessageHandler, filters

from .agent import handle_agent_text
from .config import settings


def _format_response(status: str, message: str, data: dict | None) -> str:
    if status != "ok":
        return message

    if data and "events" in data:
        events: Iterable[dict] = data.get("events", [])
        if not events:
            return "Nenhum evento encontrado."

        lines = []
        for event in events:
            title = event.get("title", "(sem título)")
            start = event.get("start", "")
            end = event.get("end", "")
            event_id = event.get("id", "")
            parts = [title]
            if start or end:
                parts.append(f"{start} - {end}".strip())
            if event_id:
                parts.append(f"id: {event_id}")
            lines.append(" | ".join([part for part in parts if part]))
        return "\n".join(lines)

    return message


async def _handle_text(update: Update, context: ContextTypes.DEFAULT_TYPE) -> None:
    if not update.message or not update.message.text:
        return

    response = await handle_agent_text(update.message.text)
    formatted = _format_response(response.status, response.message, response.data)
    await update.message.reply_text(formatted)


def build_application() -> Application:
    if not settings.telegram_bot_token:
        raise RuntimeError("TELEGRAM_BOT_TOKEN não configurado")

    application = Application.builder().token(settings.telegram_bot_token).build()
    application.add_handler(MessageHandler(filters.TEXT & ~filters.COMMAND, _handle_text))
    return application

