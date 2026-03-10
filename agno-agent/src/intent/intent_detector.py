"""Detector de intenções baseado em padrões de linguagem natural."""

import re
import unicodedata
from dataclasses import dataclass
from enum import Enum
from typing import Optional

from .patterns import (
    CREATE_ACTION_PATTERNS,
    CREATE_NOUN_PATTERNS,
    CANCEL_PATTERNS,
    LIST_TODAY_PATTERNS,
    LIST_TOMORROW_PATTERNS,
    LIST_NEXT_PATTERNS,
    LIST_WEEK_PATTERNS,
    LIST_NEXT_WEEK_PATTERNS,
    LIST_MONTH_PATTERNS,
    LIST_NEXT_MONTH_PATTERNS,
    HELP_PATTERNS,
)


class Intent(Enum):
    """Intenções suportadas pelo agente."""
    CREATE_EVENT = "CREATE_EVENT"
    CANCEL_EVENT = "CANCEL_EVENT"
    LIST_TODAY = "LIST_TODAY"
    LIST_TOMORROW = "LIST_TOMORROW"
    LIST_NEXT = "LIST_NEXT"
    LIST_WEEK = "LIST_WEEK"
    LIST_NEXT_WEEK = "LIST_NEXT_WEEK"
    LIST_MONTH = "LIST_MONTH"
    LIST_NEXT_MONTH = "LIST_NEXT_MONTH"
    HELP = "HELP"
    UNKNOWN = "UNKNOWN"


@dataclass
class DetectedIntent:
    """Resultado da detecção de intenção."""
    intent: Intent
    confidence: float
    matched_pattern: Optional[str] = None


def _strip_accents(text: str) -> str:
    """Remove acentos de uma string unicode (ex: próximo → proximo)."""
    nfkd = unicodedata.normalize("NFKD", text)
    return "".join(c for c in nfkd if not unicodedata.combining(c))


def _expand_patterns(patterns: list[str]) -> list[str]:
    """Gera versões com e sem acento para cada padrão, sem duplicar."""
    expanded = []
    seen = set()
    for p in patterns:
        for variant in (p, _strip_accents(p)):
            if variant not in seen:
                seen.add(variant)
                expanded.append(variant)
    return expanded


class IntentDetector:
    """Detecta a intenção do usuário baseado em padrões de linguagem natural."""

    def __init__(self):
        # Ordem importa: padrões mais específicos primeiro
        # 1) CANCEL e CREATE (verbos de ação) — prioridade máxima
        # 2) Listas específicas (next week/month) antes das genéricas
        # 3) CREATE (substantivos) como fallback no final
        self._pattern_map = [
            (_expand_patterns(CANCEL_PATTERNS), Intent.CANCEL_EVENT),
            (_expand_patterns(CREATE_ACTION_PATTERNS), Intent.CREATE_EVENT),
            (_expand_patterns(LIST_NEXT_WEEK_PATTERNS), Intent.LIST_NEXT_WEEK),
            (_expand_patterns(LIST_NEXT_MONTH_PATTERNS), Intent.LIST_NEXT_MONTH),
            (_expand_patterns(LIST_TOMORROW_PATTERNS), Intent.LIST_TOMORROW),
            (_expand_patterns(LIST_TODAY_PATTERNS), Intent.LIST_TODAY),
            (_expand_patterns(LIST_WEEK_PATTERNS), Intent.LIST_WEEK),
            (_expand_patterns(LIST_MONTH_PATTERNS), Intent.LIST_MONTH),
            (_expand_patterns(LIST_NEXT_PATTERNS), Intent.LIST_NEXT),
            (_expand_patterns(CREATE_NOUN_PATTERNS), Intent.CREATE_EVENT),
            (_expand_patterns(HELP_PATTERNS), Intent.HELP),
        ]

    def detect(self, message: str) -> DetectedIntent:
        """Detecta a intenção a partir da mensagem do usuário."""
        normalized = self._normalize(message)

        for patterns, intent in self._pattern_map:
            matched = self._match_patterns(normalized, patterns)
            if matched:
                return DetectedIntent(
                    intent=intent,
                    confidence=0.85,
                    matched_pattern=matched,
                )

        return DetectedIntent(intent=Intent.UNKNOWN, confidence=0.0)

    def _normalize(self, text: str) -> str:
        """Normaliza texto: lowercase, espaços únicos, remove acentos."""
        text = text.strip().lower()
        text = re.sub(r"\s+", " ", text)
        # Remove acentos para matching robusto
        text = _strip_accents(text)
        return text

    def _match_patterns(self, text: str, patterns: list[str]) -> Optional[str]:
        """Verifica se o texto contém algum dos padrões. Retorna o padrão encontrado ou None."""
        for pattern in patterns:
            if pattern in text:
                return pattern
        return None
