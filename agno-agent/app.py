"""
Agno Agent - Assistente de Agenda em Linguagem Natural

Entry point da aplicação FastAPI.
Toda a lógica está organizada em módulos separados dentro de src/.
"""
from fastapi import FastAPI

from src.models import AgentRequest, AgentResponse
from src.router import IntentRouter

app = FastAPI(
    title="Agno Agent",
    description="Assistente de agenda com processamento de linguagem natural",
    version="2.0.0",
)

# Router singleton (stateless, pode ser compartilhado)
router = IntentRouter()


@app.get("/health")
def health():
    """Health check endpoint."""
    return {"status": "ok", "version": "2.0.0"}


@app.post("/chat", response_model=AgentResponse)
def chat(request: AgentRequest) -> AgentResponse:
    """
    Processa mensagem do usuário e retorna ação interpretada.

    O router detecta a intenção, extrai entidades (data, hora, título)
    e delega ao handler apropriado.
    """
    return router.route(request)

