from pydantic import BaseModel
from typing import Literal, Optional


class AgentMessageRequest(BaseModel):
    text: str


class ParsedIntent(BaseModel):
    action: Literal["create", "list", "cancel", "unknown"]
    title: Optional[str] = None
    start: Optional[str] = None
    end: Optional[str] = None
    event_id: Optional[str] = None


class AgentMessageResponse(BaseModel):
    status: Literal["ok", "need_confirmation", "error"]
    message: str
    data: Optional[dict] = None
