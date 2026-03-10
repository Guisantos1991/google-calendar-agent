from .base_handler import BaseHandler
from ..models.request import AgentRequest
from ..models.response import AgentResponse
from ..models.extracted_data import ExtractedData


class ClarifyHandler(BaseHandler):
    """Handler para quando não entendemos a intenção."""

    DEFAULT_QUESTION = """Não entendi bem. Você quer:
• Ver sua agenda? (ex: "hoje", "amanhã", "semana", "próximos compromissos")
• Agenda futura? (ex: "semana que vem", "próxima semana", "mês")
• Criar um evento? (ex: "marcar reunião amanhã 14h")
• Cancelar algo? (ex: "cancelar reunião X")"""

    def handle(self, request: AgentRequest, extracted: ExtractedData) -> AgentResponse:
        return AgentResponse.clarify(self.DEFAULT_QUESTION, confidence=0.3)

