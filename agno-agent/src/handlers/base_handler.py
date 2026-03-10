from abc import ABC, abstractmethod

from ..models.request import AgentRequest
from ..models.response import AgentResponse
from ..models.extracted_data import ExtractedData


class BaseHandler(ABC):
    """Interface base para handlers de ações."""

    @abstractmethod
    def handle(self, request: AgentRequest, extracted: ExtractedData) -> AgentResponse:
        """Processa a requisição e retorna resposta."""
        pass

