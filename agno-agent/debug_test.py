from datetime import date
from src.extractors import EntityExtractor

ex = EntityExtractor()
msg = 'Dia 12 de marco de 2026 das 18h as 19h com o nome Teste de Evento'
result = ex.extract(msg, date(2026, 3, 7))
print("Date:", result.event_date)
print("Time:", result.time_range)
print("Title:", result.title)
print("Complete:", result.is_complete_for_event())

