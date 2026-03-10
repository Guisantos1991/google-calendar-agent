from .base_handler import BaseHandler
from ..models.request import AgentRequest
from ..models.response import AgentResponse
from ..models.extracted_data import ExtractedData


class CancelEventHandler(BaseHandler):
    """Handler para cancelamento de eventos."""

    def handle(self, request: AgentRequest, extracted: ExtractedData) -> AgentResponse:
        # Usa título extraído ou a mensagem original como query
        query = extracted.title if extracted.has_title() else request.message.strip()
        return AgentResponse.cancel_event(query)

