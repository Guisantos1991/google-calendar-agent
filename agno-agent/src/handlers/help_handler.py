from .base_handler import BaseHandler
from ..models.request import AgentRequest
from ..models.response import AgentResponse
from ..models.extracted_data import ExtractedData


class HelpHandler(BaseHandler):
    """Handler para ajuda/início."""

    HELP_TEXT = """👋 Olá! Sou seu assistente de agenda.

📋 *O que posso fazer:*
• Ver sua agenda de hoje
• Ver compromissos da semana
• Criar novos eventos
• Cancelar compromissos

💬 *Exemplos de comandos:*
• "O que tenho hoje?"
• "Agenda da semana"
• "Marcar reunião amanhã às 14h"
• "Agendar almoço dia 15 às 12h"
• "Cancelar reunião de segunda"

Pode falar naturalmente! 😊"""

    def handle(self, request: AgentRequest, extracted: ExtractedData) -> AgentResponse:
        return AgentResponse.help(self.HELP_TEXT)

