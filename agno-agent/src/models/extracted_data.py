from dataclasses import dataclass, field
from datetime import date, time
from typing import Optional, List


@dataclass
class TimeRange:
    """Representa um intervalo de horário."""
    start: time
    end: Optional[time] = None

    def has_end(self) -> bool:
        return self.end is not None


@dataclass
class ExtractedData:
    """Dados extraídos da mensagem do usuário."""
    title: Optional[str] = None
    event_date: Optional[date] = None
    time_range: Optional[TimeRange] = None
    raw_message: str = ""

    def has_title(self) -> bool:
        return self.title is not None and len(self.title) > 0

    def has_date(self) -> bool:
        return self.event_date is not None

    def has_time(self) -> bool:
        return self.time_range is not None

    def is_complete_for_event(self) -> bool:
        """Verifica se temos dados suficientes para criar um evento."""
        return self.has_title() and self.has_date() and self.has_time()

    def missing_fields(self) -> List[str]:
        """Retorna lista de campos faltantes."""
        missing = []
        if not self.has_title():
            missing.append("título")
        if not self.has_date():
            missing.append("data")
        if not self.has_time():
            missing.append("horário")
        return missing

