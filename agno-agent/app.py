from __future__ import annotations

import re
from datetime import datetime
from typing import Any, Dict, Optional

from fastapi import FastAPI
from pydantic import BaseModel, Field

app = FastAPI(title="Agno Agent (stub)")


class AgentRequest(BaseModel):
    user_id: str
    timezone: str
    now: str
    message: str


class AgentResponse(BaseModel):
    action: str
    confidence: float = Field(ge=0.0, le=1.0)
    args: Dict[str, Any] = Field(default_factory=dict)


@app.get("/health")
def health():
    return {"status": "ok"}


def _iso_now_fallback() -> str:
    return datetime.utcnow().isoformat() + "Z"


def _normalize(text: str) -> str:
    return re.sub(r"\s+", " ", text.strip().lower())


def _looks_like_create(msg: str) -> bool:
    # bem permissivo: você pode refinar depois
    return any(k in msg for k in ["marcar", "agendar", "criar", "reunião", "reuniao", "evento", "call", "consulta"])


def _looks_like_cancel(msg: str) -> bool:
    return any(k in msg for k in ["cancelar", "desmarcar", "remover", "apagar"])


def _looks_like_list_today(msg: str) -> bool:
    return any(k in msg for k in ["hoje", "agenda de hoje", "compromissos de hoje", "meus compromissos hoje"])


def _looks_like_list_week(msg: str) -> bool:
    return any(k in msg for k in ["semana", "essa semana", "desta semana", "próximos dias", "proximos dias"])


def _extract_time_hhmm(msg: str) -> Optional[str]:
    # aceita 14h, 14:30, 14h30
    m = re.search(r"\b([01]?\d|2[0-3])(?:h|:)?([0-5]\d)?\b", msg)
    if not m:
        return None
    hh = int(m.group(1))
    mm = int(m.group(2) or 0)
    return f"{hh:02d}:{mm:02d}"


def _extract_day_hint(msg: str) -> Optional[str]:
    # super simples (MVP): hoje/amanhã
    if "amanhã" in msg or "amanha" in msg:
        return "tomorrow"
    if "hoje" in msg:
        return "today"
    return None


def _extract_title(msg: str) -> Optional[str]:
    # tenta pegar o que vem depois de "marcar/agendar/criar"
    m = re.search(r"\b(?:marcar|agendar|criar)\b\s+(.*)", msg)
    if not m:
        return None
    title = m.group(1).strip()
    # limpa pedaços comuns no fim
    title = re.sub(r"\b(hoje|amanhã|amanha|às|as)\b.*$", "", title).strip()
    return title or None


@app.post("/chat", response_model=AgentResponse)
def chat(req: AgentRequest):
    msg = _normalize(req.message)

    # comandos básicos
    if msg in {"/start", "start", "ajuda", "help", "?"}:
        return AgentResponse(
            action="HELP",
            confidence=1.0,
            args={
                "text": "Comandos: 'agenda de hoje', 'agenda da semana', 'marcar reunião amanhã 14h', 'cancelar reunião X'."
            },
        )

    # listar agenda
    if _looks_like_list_today(msg):
        return AgentResponse(
            action="LIST_TODAY",
            confidence=0.85,
            args={
                "range": {"type": "today"},
                "debug": {"received_at": _iso_now_fallback(), "now": req.now, "tz": req.timezone},
            },
        )

    if _looks_like_list_week(msg):
        return AgentResponse(
            action="LIST_WEEK",
            confidence=0.8,
            args={
                "range": {"type": "week"},
                "debug": {"received_at": _iso_now_fallback(), "now": req.now, "tz": req.timezone},
            },
        )

    # cancelar evento
    if _looks_like_cancel(msg):
        # MVP: o "query" é o texto inteiro; depois você melhora com entidades
        return AgentResponse(
            action="CANCEL_EVENT",
            confidence=0.7,
            args={
                "query": req.message.strip(),
                "debug": {"received_at": _iso_now_fallback()},
            },
        )

    # criar evento
    if _looks_like_create(msg):
        time_hhmm = _extract_time_hhmm(msg)
        day_hint = _extract_day_hint(msg)
        title = _extract_title(msg) or "Evento"

        # se não tiver horário, pede clarificação (melhor do que inventar)
        if time_hhmm is None:
            return AgentResponse(
                action="ASK_CLARIFY",
                confidence=0.55,
                args={
                    "clarify_question": "Qual horário? Ex: 'amanhã 14h' ou 'hoje 09:30'.",
                    "intent": "CREATE_EVENT",
                    "partial": {"title": title, "day_hint": day_hint},
                },
            )

        return AgentResponse(
            action="CREATE_EVENT",
            confidence=0.75,
            args={
                "title": title,
                "day_hint": day_hint,  # today/tomorrow/None
                "time": time_hhmm,
                "timezone": req.timezone,
                "debug": {"received_at": _iso_now_fallback(), "now": req.now},
            },
        )

    # fallback
    return AgentResponse(
        action="ASK_CLARIFY",
        confidence=0.35,
        args={
            "clarify_question": "Você quer listar agenda (hoje/semana) ou criar/cancelar um evento?",
            "debug": {"received_at": _iso_now_fallback()},
        },
    )
