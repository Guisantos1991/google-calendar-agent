import re
from datetime import date
from typing import Optional


class DateExtractor:
    """Extrai datas de texto em linguagem natural."""

    MONTHS = {
        "janeiro": 1, "jan": 1,
        "fevereiro": 2, "fev": 2,
        "março": 3, "marco": 3, "mar": 3,
        "abril": 4, "abr": 4,
        "maio": 5, "mai": 5,
        "junho": 6, "jun": 6,
        "julho": 7, "jul": 7,
        "agosto": 8, "ago": 8,
        "setembro": 9, "set": 9,
        "outubro": 10, "out": 10,
        "novembro": 11, "nov": 11,
        "dezembro": 12, "dez": 12,
    }

    WEEKDAYS = {
        "segunda": 0, "segunda-feira": 0,
        "terça": 1, "terca": 1, "terça-feira": 1, "terca-feira": 1,
        "quarta": 2, "quarta-feira": 2,
        "quinta": 3, "quinta-feira": 3,
        "sexta": 4, "sexta-feira": 4,
        "sábado": 5, "sabado": 5,
        "domingo": 6,
    }

    def extract(self, text: str, reference_date: date) -> Optional[date]:
        """Extrai data do texto usando a data de referência para datas relativas."""
        normalized = text.lower()

        # Tenta extrair na ordem: relativas simples, absolutas, dia da semana
        result = self._extract_relative(normalized, reference_date)
        if result:
            return result

        result = self._extract_absolute(normalized, reference_date)
        if result:
            return result

        result = self._extract_weekday(normalized, reference_date)
        if result:
            return result

        return None

    def _extract_relative(self, text: str, ref: date) -> Optional[date]:
        """Extrai datas relativas: hoje, amanhã, depois de amanhã."""
        if "depois de amanhã" in text or "depois de amanha" in text:
            return ref + self._days(2)
        if "amanhã" in text or "amanha" in text:
            return ref + self._days(1)
        if "hoje" in text:
            return ref
        return None

    def _extract_absolute(self, text: str, ref: date) -> Optional[date]:
        """Extrai datas absolutas: '12 de março de 2026', 'dia 15', '15/03'."""

        # Formato: "dia 12 de março de 2026" ou "12 de março de 2026"
        pattern_full = r"(?:dia\s+)?(\d{1,2})\s+de\s+(\w+)(?:\s+de\s+(\d{4}))?"
        match = re.search(pattern_full, text)
        if match:
            day = int(match.group(1))
            month_name = match.group(2)
            year = int(match.group(3)) if match.group(3) else ref.year

            month = self.MONTHS.get(month_name)
            if month:
                return self._safe_date(year, month, day)

        # Formato: "15/03/2026" ou "15/03"
        pattern_numeric = r"(\d{1,2})/(\d{1,2})(?:/(\d{2,4}))?"
        match = re.search(pattern_numeric, text)
        if match:
            day = int(match.group(1))
            month = int(match.group(2))
            year = match.group(3)
            if year:
                year = int(year)
                if year < 100:
                    year += 2000
            else:
                year = ref.year
            return self._safe_date(year, month, day)

        # Formato: "dia 15" (assume mês atual ou próximo)
        pattern_day = r"dia\s+(\d{1,2})\b"
        match = re.search(pattern_day, text)
        if match:
            day = int(match.group(1))
            # Se o dia já passou neste mês, assume próximo mês
            if day < ref.day:
                month = ref.month + 1 if ref.month < 12 else 1
                year = ref.year if ref.month < 12 else ref.year + 1
            else:
                month = ref.month
                year = ref.year
            return self._safe_date(year, month, day)

        return None

    def _extract_weekday(self, text: str, ref: date) -> Optional[date]:
        """Extrai dia da semana: 'na segunda', 'próxima sexta'."""
        is_next_week = "próxima" in text or "proxima" in text or "que vem" in text

        for name, weekday in self.WEEKDAYS.items():
            if name in text:
                days_ahead = weekday - ref.weekday()
                if days_ahead <= 0:
                    days_ahead += 7
                if is_next_week and days_ahead < 7:
                    days_ahead += 7
                return ref + self._days(days_ahead)

        return None

    def _days(self, n: int):
        """Retorna timedelta de n dias."""
        from datetime import timedelta
        return timedelta(days=n)

    def _safe_date(self, year: int, month: int, day: int) -> Optional[date]:
        """Cria date com validação."""
        try:
            return date(year, month, day)
        except ValueError:
            return None

