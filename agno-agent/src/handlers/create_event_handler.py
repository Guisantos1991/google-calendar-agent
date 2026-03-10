from datetime import datetime

from .base_handler import BaseHandler
from ..models.request import AgentRequest
from ..models.response import AgentResponse
from ..models.extracted_data import ExtractedData


class CreateEventHandler(BaseHandler):
    """Handler para criação de eventos."""

    def handle(self, request: AgentRequest, extracted: ExtractedData) -> AgentResponse:
        """Processa criação de evento, pedindo clarificação se necessário."""

        # Verifica dados faltantes
        if not extracted.is_complete_for_event():
            return self._ask_for_missing(extracted)

        # Monta datetime ISO para o Java
        start_time = self._build_iso_datetime(
            extracted.event_date,
            extracted.time_range.start,
            request.timezone
        )

        end_time = None
        if extracted.time_range.has_end():
            end_time = self._build_iso_datetime(
                extracted.event_date,
                extracted.time_range.end,
                request.timezone
            )

        return AgentResponse.create_event(
            summary=extracted.title,
            start_time=start_time,
            end_time=end_time
        )

    def _ask_for_missing(self, extracted: ExtractedData) -> AgentResponse:
        """Gera pergunta natural baseada nos campos faltantes."""
        missing = extracted.missing_fields()

        if len(missing) == 1:
            field = missing[0]
            questions = {
                "título": "Qual o nome do evento?",
                "data": "Para qual dia? Ex: 'amanhã', 'dia 15', 'segunda-feira'",
                "horário": "Qual horário? Ex: '14h', '15:30', 'das 10h às 11h'",
            }
            question = questions.get(field, f"Qual o {field}?")
        else:
            question = f"Preciso de mais detalhes: {', '.join(missing)}. Pode informar?"

        return AgentResponse.clarify(question, confidence=0.6)

    def _build_iso_datetime(self, event_date, event_time, timezone: str) -> str:
        """Constrói string ISO 8601 com timezone."""
        dt = datetime.combine(event_date, event_time)
        # Formato: 2026-03-12T18:00:00
        return dt.strftime("%Y-%m-%dT%H:%M:%S")

