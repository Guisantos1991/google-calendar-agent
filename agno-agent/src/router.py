from datetime import date, datetime

from .models.request import AgentRequest
from .models.response import AgentResponse
from .intent import IntentDetector, Intent
from .extractors import EntityExtractor
from .handlers import (
    BaseHandler,
    CreateEventHandler,
    ListTodayHandler,
    ListTomorrowHandler,
    ListNextHandler,
    ListWeekHandler,
    ListNextWeekHandler,
    ListMonthHandler,
    ListNextMonthHandler,
    CancelEventHandler,
    HelpHandler,
    ClarifyHandler,
)


class IntentRouter:
    """Roteia requisições para o handler apropriado baseado na intenção detectada."""

    def __init__(self):
        self._intent_detector = IntentDetector()
        self._entity_extractor = EntityExtractor()
        self._handlers = self._build_handlers()

    def _build_handlers(self) -> dict[Intent, BaseHandler]:
        """Constrói mapa de intenção → handler."""
        return {
            Intent.CREATE_EVENT: CreateEventHandler(),
            Intent.LIST_TODAY: ListTodayHandler(),
            Intent.LIST_TOMORROW: ListTomorrowHandler(),
            Intent.LIST_NEXT: ListNextHandler(),
            Intent.LIST_WEEK: ListWeekHandler(),
            Intent.LIST_NEXT_WEEK: ListNextWeekHandler(),
            Intent.LIST_MONTH: ListMonthHandler(),
            Intent.LIST_NEXT_MONTH: ListNextMonthHandler(),
            Intent.CANCEL_EVENT: CancelEventHandler(),
            Intent.HELP: HelpHandler(),
            Intent.UNKNOWN: ClarifyHandler(),
        }

    def route(self, request: AgentRequest) -> AgentResponse:
        """Processa a requisição: detecta intenção, extrai entidades e delega ao handler."""

        # Detecta intenção
        detected = self._intent_detector.detect(request.message)

        # Extrai entidades (data, horário, título)
        reference_date = self._parse_reference_date(request.now)
        extracted = self._entity_extractor.extract(request.message, reference_date)

        # Delega ao handler apropriado
        handler = self._handlers.get(detected.intent, self._handlers[Intent.UNKNOWN])
        return handler.handle(request, extracted)

    def _parse_reference_date(self, now_str: str) -> date:
        """Converte string ISO para date."""
        try:
            # Formato esperado: 2026-03-07T10:30:00
            return datetime.fromisoformat(now_str.replace("Z", "")).date()
        except (ValueError, AttributeError):
            return date.today()


