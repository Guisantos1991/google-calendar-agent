from datetime import date

from ..models.extracted_data import ExtractedData, TimeRange
from .date_extractor import DateExtractor
from .time_extractor import TimeExtractor
from .title_extractor import TitleExtractor


class EntityExtractor:
    """Coordena a extração de todas as entidades de uma mensagem."""

    def __init__(self):
        self._date_extractor = DateExtractor()
        self._time_extractor = TimeExtractor()
        self._title_extractor = TitleExtractor()

    def extract(self, message: str, reference_date: date) -> ExtractedData:
        """Extrai todas as entidades relevantes da mensagem."""

        extracted_date = self._date_extractor.extract(message, reference_date)
        time_result = self._time_extractor.extract(message)
        title = self._title_extractor.extract(message)

        time_range = None
        if time_result:
            time_range = TimeRange(
                start=time_result.start,
                end=time_result.end
            )

        return ExtractedData(
            title=title,
            event_date=extracted_date,
            time_range=time_range,
            raw_message=message
        )

