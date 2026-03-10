from pydantic import BaseModel


class AgentRequest(BaseModel):
    """Requisição recebida do bot."""
    user_id: str
    timezone: str
    now: str
    message: str

    def normalized_message(self) -> str:
        """Retorna mensagem normalizada (lowercase, espaços únicos)."""
        import re
        return re.sub(r"\s+", " ", self.message.strip().lower())

