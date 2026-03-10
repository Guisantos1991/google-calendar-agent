import re
from typing import Optional


class TitleExtractor:
    """Extrai título/nome do evento de texto em linguagem natural."""

    # Padrões que indicam início do título
    TITLE_INDICATORS = [
        r"(?:com\s+(?:o\s+)?nome|chamad[oa]|titulad[oa]|sobre)\s+[\"']?(.+?)[\"']?(?:\s+(?:para|no dia|dia|às|as|em|de|das|$))",
        r"(?:marcar|agendar|criar|adicionar)\s+(?:uma?\s+)?(?:reunião|evento|compromisso|call|consulta)?\s*[\"']?(.+?)[\"']?(?:\s+(?:para|no dia|dia|às|as|em|de|das|$))",
        r"(?:reunião|evento|compromisso|call|consulta)\s+(?:de\s+|sobre\s+|com\s+)?[\"']?(.+?)[\"']?(?:\s+(?:para|no dia|dia|às|as|em|de|das|$))",
    ]

    # Palavras que não devem ser título (stop words de contexto)
    STOP_PATTERNS = [
        r"^\s*(?:uma?|o|a|os|as|para|no|na|em|de|da|do|das|dos)\s*$",
        r"^\s*(?:hoje|amanhã|amanha|segunda|terça|terca|quarta|quinta|sexta|sábado|sabado|domingo)\s*$",
        r"^\s*(?:manhã|manha|tarde|noite)\s*$",
        r"^\s*\d+\s*$",
    ]

    def extract(self, text: str) -> Optional[str]:
        """Extrai o título do evento do texto."""
        normalized = text.lower()

        # Tenta cada padrão de indicador
        for pattern in self.TITLE_INDICATORS:
            match = re.search(pattern, normalized, re.IGNORECASE)
            if match:
                title = self._clean_title(match.group(1))
                if title and not self._is_stop_word(title):
                    return title.title()  # Capitaliza

        # Fallback: extrai substantivo após verbo de ação
        title = self._extract_after_action_verb(normalized)
        if title:
            return title.title()

        return None

    def _extract_after_action_verb(self, text: str) -> Optional[str]:
        """Extrai texto após verbos de ação como fallback."""
        pattern = r"(?:marcar|agendar|criar)\s+(?:uma?\s+)?(.+?)(?:\s+(?:para|no dia|dia|às|as|hoje|amanhã|amanha|\d))"
        match = re.search(pattern, text)
        if match:
            title = self._clean_title(match.group(1))
            if title and not self._is_stop_word(title):
                return title
        return None

    def _clean_title(self, title: str) -> Optional[str]:
        """Limpa o título extraído."""
        if not title:
            return None

        # Remove artigos e preposições do início/fim
        title = re.sub(r"^(?:uma?|o|a|de|da|do)\s+", "", title.strip())
        title = re.sub(r"\s+(?:para|no|na|em|de|da|do|às|as)$", "", title.strip())

        # Remove pontuação
        title = re.sub(r"[\"'.,!?]", "", title).strip()

        # Remove horários que sobraram
        title = re.sub(r"\b\d{1,2}(?:h|:)\d{0,2}\b", "", title).strip()

        return title if len(title) > 1 else None

    def _is_stop_word(self, text: str) -> bool:
        """Verifica se o texto é uma stop word."""
        for pattern in self.STOP_PATTERNS:
            if re.match(pattern, text, re.IGNORECASE):
                return True
        return False

