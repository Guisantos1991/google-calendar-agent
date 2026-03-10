from .base_handler import BaseHandler
from ..models.request import AgentRequest
from ..models.response import AgentResponse
from ..models.extracted_data import ExtractedData


class ListTodayHandler(BaseHandler):
    """Handler para listar eventos de hoje."""

    def handle(self, request: AgentRequest, extracted: ExtractedData) -> AgentResponse:
        return AgentResponse.list_today()


class ListTomorrowHandler(BaseHandler):
    """Handler para listar eventos de amanhã."""

    def handle(self, request: AgentRequest, extracted: ExtractedData) -> AgentResponse:
        return AgentResponse.list_tomorrow()


class ListNextHandler(BaseHandler):
    """Handler para listar próximos eventos."""

    def handle(self, request: AgentRequest, extracted: ExtractedData) -> AgentResponse:
        return AgentResponse.list_next()


class ListWeekHandler(BaseHandler):
    """Handler para listar eventos da semana atual."""

    def handle(self, request: AgentRequest, extracted: ExtractedData) -> AgentResponse:
        return AgentResponse.list_week()


class ListNextWeekHandler(BaseHandler):
    """Handler para listar eventos da próxima semana."""

    def handle(self, request: AgentRequest, extracted: ExtractedData) -> AgentResponse:
        return AgentResponse.list_next_week()


class ListMonthHandler(BaseHandler):
    """Handler para listar eventos do mês atual."""

    def handle(self, request: AgentRequest, extracted: ExtractedData) -> AgentResponse:
        return AgentResponse.list_month()


class ListNextMonthHandler(BaseHandler):
    """Handler para listar eventos do próximo mês."""

    def handle(self, request: AgentRequest, extracted: ExtractedData) -> AgentResponse:
        return AgentResponse.list_next_month()