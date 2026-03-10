from .base_handler import BaseHandler
from ..models.request import AgentRequest
from ..models.response import AgentResponse
from ..models.extracted_data import ExtractedData


class ListTodayHandler(BaseHandler):
    """Handler para listar eventos de hoje."""

    def handle(self, request: AgentRequest, extracted: ExtractedData) -> AgentResponse:
        return AgentResponse.list_today()


class ListWeekHandler(BaseHandler):
    """Handler para listar eventos da semana."""

    def handle(self, request: AgentRequest, extracted: ExtractedData) -> AgentResponse:
        return AgentResponse.list_week()

