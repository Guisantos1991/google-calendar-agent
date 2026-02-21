from datetime import datetime, timedelta
from .models import ParsedIntent


def parse_intent(text: str) -> ParsedIntent:
    lowered = text.strip().lower()

    if lowered.startswith("cancelar "):
        event_id = lowered.replace("cancelar ", "", 1).strip()
        if event_id:
            return ParsedIntent(action="cancel", event_id=event_id)

    if "listar" in lowered or "eventos" in lowered:
        return ParsedIntent(action="list")

    if lowered.startswith("criar "):
        title = text.strip()[6:].strip()
        if not title:
            return ParsedIntent(action="unknown")

        start_dt = datetime.now() + timedelta(hours=1)
        end_dt = start_dt + timedelta(hours=1)

        return ParsedIntent(
            action="create",
            title=title,
            start=start_dt.isoformat(),
            end=end_dt.isoformat(),
        )

    return ParsedIntent(action="unknown")
