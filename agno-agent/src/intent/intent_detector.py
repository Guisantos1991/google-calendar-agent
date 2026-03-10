from enum import Enum, auto
from dataclasses import dataclass
from typing import Optional, List

from .patterns import (
    CREATE_PATTERNS,
    CANCEL_PATTERNS,
    LIST_TODAY_PATTERNS,
    LIST_TOMORROW_PATTERNS,
    LIST_NEXT_PATTERNS,
    LIST_WEEK_PATTERNS,
    LIST_MONTH_PATTERNS,
    HELP_PATTERNS,
)


class Intent(Enum):
    """Intenções possíveis do usuário."""
    CREATE_EVENT = auto()
    CANCEL_EVENT = auto()
    LIST_TODAY = auto()
    LIST_TOMORROW = auto()
    LIST_NEXT = auto()
    LIST_WEEK = auto()
    LIST_MONTH = auto()
    HELP = auto()
    UNKNOWN = auto()


@dataclass
class DetectedIntent:
    """Resultado da detecção de intenção."""
    intent: Intent
    confidence: float
    matched_pattern: Optional[str] = None


class IntentDetector:
    """Detecta a intenção do usuário baseado em padrões de linguagem natural."""

    def detect(self, message: str) -> DetectedIntent:
        """Detecta a intenção principal da mensagem."""
        normalized = message.lower().strip()

        # Help tem prioridade máxima (comando explícito)
        if self._matches_any(normalized, HELP_PATTERNS):
            return DetectedIntent(Intent.HELP, 1.0, self._find_match(normalized, HELP_PATTERNS))

        # Cancelar tem prioridade sobre criar (ação destrutiva, precisa ser explícita)
        if self._matches_any(normalized, CANCEL_PATTERNS):
            return DetectedIntent(Intent.CANCEL_EVENT, 0.85, self._find_match(normalized, CANCEL_PATTERNS))

        # Listar hoje (mais específico)
        if self._matches_any(normalized, LIST_TODAY_PATTERNS):
            return DetectedIntent(Intent.LIST_TODAY, 0.9, self._find_match(normalized, LIST_TODAY_PATTERNS))

        # Listar amanhã
        if self._matches_any(normalized, LIST_TOMORROW_PATTERNS):
            return DetectedIntent(Intent.LIST_TOMORROW, 0.9, self._find_match(normalized, LIST_TOMORROW_PATTERNS))

        # Listar semana (antes de LIST_NEXT para evitar conflito com "próximos dias")
        if self._matches_any(normalized, LIST_WEEK_PATTERNS):
            return DetectedIntent(Intent.LIST_WEEK, 0.85, self._find_match(normalized, LIST_WEEK_PATTERNS))

        # Listar mês
        if self._matches_any(normalized, LIST_MONTH_PATTERNS):
            return DetectedIntent(Intent.LIST_MONTH, 0.85, self._find_match(normalized, LIST_MONTH_PATTERNS))

        # Listar próximos compromissos
        if self._matches_any(normalized, LIST_NEXT_PATTERNS):
            return DetectedIntent(Intent.LIST_NEXT, 0.85, self._find_match(normalized, LIST_NEXT_PATTERNS))

        # Criar evento (mais permissivo, vem por último)
        if self._matches_any(normalized, CREATE_PATTERNS):
            return DetectedIntent(Intent.CREATE_EVENT, 0.8, self._find_match(normalized, CREATE_PATTERNS))

        return DetectedIntent(Intent.UNKNOWN, 0.3)

    def _matches_any(self, text: str, patterns: List[str]) -> bool:
        """Verifica se o texto contém algum dos padrões."""
        return any(pattern in text for pattern in patterns)

    def _find_match(self, text: str, patterns: List[str]) -> Optional[str]:
        """Encontra qual padrão deu match."""
        for pattern in patterns:
            if pattern in text:
                return pattern
        return None

