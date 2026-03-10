import re
from datetime import time
from typing import Optional
from dataclasses import dataclass


@dataclass
class TimeRange:
    """Intervalo de horário."""
    start: time
    end: Optional[time] = None

    def has_end(self) -> bool:
        return self.end is not None


class TimeExtractor:
    """Extrai horários de texto em linguagem natural."""

    def extract(self, text: str) -> Optional[TimeRange]:
        """Extrai intervalo de horário do texto."""
        normalized = text.lower()

        # Tenta extrair range primeiro (mais específico)
        result = self._extract_range(normalized)
        if result:
            return result

        # Depois tenta horário único
        result = self._extract_single(normalized)
        if result:
            return result

        return None

    def _extract_range(self, text: str) -> Optional[TimeRange]:
        """Extrai faixa de horário: 'das 18h às 19h', '14:00 - 15:30'."""

        # Padrão: "das Xh às Yh" ou "de Xh a Yh"
        pattern_verbal = r"(?:das?|de)\s*(\d{1,2})(?:h|:)?(\d{2})?\s*(?:às?|a|até|-)\s*(\d{1,2})(?:h|:)?(\d{2})?"
        match = re.search(pattern_verbal, text)
        if match:
            start = self._build_time(match.group(1), match.group(2))
            end = self._build_time(match.group(3), match.group(4))
            if start and end:
                return TimeRange(start, end)

        # Padrão: "14:00 - 15:30" ou "14h-15h"
        pattern_dash = r"(\d{1,2})(?:h|:)(\d{2})?\s*[-–]\s*(\d{1,2})(?:h|:)(\d{2})?"
        match = re.search(pattern_dash, text)
        if match:
            start = self._build_time(match.group(1), match.group(2))
            end = self._build_time(match.group(3), match.group(4))
            if start and end:
                return TimeRange(start, end)

        return None

    def _extract_single(self, text: str) -> Optional[TimeRange]:
        """Extrai horário único: '14h', '14:30', 'às 15h'."""

        # Padrão: "às 14h30", "14:30", "14h"
        pattern = r"(?:às?\s*)?(\d{1,2})(?:h|:)(\d{2})?(?:\s*(?:horas?|hrs?))?"
        match = re.search(pattern, text)
        if match:
            start = self._build_time(match.group(1), match.group(2))
            if start:
                # Assume duração padrão de 1 hora
                end = self._add_hour(start)
                return TimeRange(start, end)

        return None

    def _build_time(self, hour_str: str, minute_str: Optional[str]) -> Optional[time]:
        """Constrói objeto time a partir de strings."""
        try:
            hour = int(hour_str)
            minute = int(minute_str) if minute_str else 0
            if 0 <= hour <= 23 and 0 <= minute <= 59:
                return time(hour, minute)
        except ValueError:
            pass
        return None

    def _add_hour(self, t: time) -> time:
        """Adiciona 1 hora ao horário."""
        new_hour = t.hour + 1 if t.hour < 23 else 23
        return time(new_hour, t.minute)

