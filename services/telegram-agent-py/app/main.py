from fastapi import FastAPI

from .agent import handle_agent_text
from .config import settings
from .models import AgentMessageRequest, AgentMessageResponse, ParsedIntent
from .nlu import parse_intent

app = FastAPI(title="telegram-agent-py", version="0.1.0")


@app.get("/health")
def health() -> dict:
    return {"status": "ok", "timezone": settings.default_timezone}


@app.post("/agent/intent", response_model=ParsedIntent)
def check_intent(payload: AgentMessageRequest) -> ParsedIntent:
    """Return the parsed intent for a given text without executing any calendar operation."""
    return parse_intent(payload.text)


@app.post("/agent/message", response_model=AgentMessageResponse)
async def handle_message(payload: AgentMessageRequest) -> AgentMessageResponse:
    return await handle_agent_text(payload.text)
