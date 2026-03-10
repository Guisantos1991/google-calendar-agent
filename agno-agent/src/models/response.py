from typing import Any, Dict, Optional
from pydantic import BaseModel, Field


class AgentResponse(BaseModel):
    """Resposta enviada de volta ao bot."""
    action: str
    confidence: float = Field(ge=0.0, le=1.0)
    args: Dict[str, Any] = Field(default_factory=dict)

    @classmethod
    def help(cls, text: str) -> "AgentResponse":
        return cls(action="HELP", confidence=1.0, args={"text": text})

    @classmethod
    def clarify(cls, question: str, confidence: float = 0.5) -> "AgentResponse":
        return cls(action="ASK_CLARIFY", confidence=confidence, args={"clarify_question": question})

    @classmethod
    def list_today(cls) -> "AgentResponse":
        return cls(action="LIST_TODAY", confidence=0.9, args={})

    @classmethod
    def list_tomorrow(cls) -> "AgentResponse":
        return cls(action="LIST_TOMORROW", confidence=0.9, args={})

    @classmethod
    def list_next(cls) -> "AgentResponse":
        return cls(action="LIST_NEXT", confidence=0.85, args={})

    @classmethod
    def list_week(cls) -> "AgentResponse":
        return cls(action="LIST_WEEK", confidence=0.85, args={})

    @classmethod
    def list_month(cls) -> "AgentResponse":
        return cls(action="LIST_MONTH", confidence=0.85, args={})

    @classmethod
    def create_event(cls, summary: str, start_time: str, end_time: Optional[str] = None) -> "AgentResponse":
        args = {"summary": summary, "start_time": start_time}
        if end_time:
            args["end_time"] = end_time
        return cls(action="CREATE_EVENT", confidence=0.85, args=args)

    @classmethod
    def cancel_event(cls, query: str) -> "AgentResponse":
        return cls(action="CANCEL_EVENT", confidence=0.75, args={"query": query})


