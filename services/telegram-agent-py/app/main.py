from fastapi import FastAPI

from .agent import handle_agent_text
from .config import settings
from .models import AgentMessageRequest, AgentMessageResponse

app = FastAPI(title="telegram-agent-py", version="0.1.0")


@app.get("/health")
def health() -> dict:
    return {"status": "ok", "timezone": settings.default_timezone}


@app.post("/agent/message", response_model=AgentMessageResponse)
async def handle_message(payload: AgentMessageRequest) -> AgentMessageResponse:
    return await handle_agent_text(payload.text)
