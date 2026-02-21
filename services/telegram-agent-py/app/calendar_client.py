import httpx
from .config import settings


class CalendarCoreClient:
    def __init__(self) -> None:
        self.base_url = settings.calendar_core_base_url.rstrip("/")

    async def create_event(self, payload: dict) -> dict:
        async with httpx.AsyncClient(timeout=10) as client:
            response = await client.post(f"{self.base_url}/calendar/events", json=payload)
            response.raise_for_status()
            return response.json()

    async def list_events(self, from_iso: str, to_iso: str) -> list[dict]:
        async with httpx.AsyncClient(timeout=10) as client:
            response = await client.get(
                f"{self.base_url}/calendar/events",
                params={"from": from_iso, "to": to_iso},
            )
            response.raise_for_status()
            return response.json()

    async def cancel_event(self, event_id: str) -> None:
        async with httpx.AsyncClient(timeout=10) as client:
            response = await client.delete(f"{self.base_url}/calendar/events/{event_id}")
            response.raise_for_status()
