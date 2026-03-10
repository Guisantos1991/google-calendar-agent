"""Testes do Agno Agent."""
from datetime import date
from src.models import AgentRequest
from src.router import IntentRouter
from src.extractors import EntityExtractor


def test_extraction():
    ex = EntityExtractor()
    msg = 'Dia 12 de marco de 2026 das 18h as 19h com o nome Teste de Evento'
    result = ex.extract(msg, date(2026, 3, 7))
    print("TESTE 0: Extractor debug")
    print(f"  Date: {result.event_date}")
    print(f"  Time: {result.time_range}")
    print(f"  Title: {result.title}")
    print(f"  Complete: {result.is_complete_for_event()}")
    print()


def test_create_event_complete():
    router = IntentRouter()
    req = AgentRequest(
        user_id='123',
        timezone='America/Sao_Paulo',
        now='2026-03-07T10:00:00',
        message='Dia 12 de marco de 2026 das 18h as 19h com o nome Teste de Evento'
    )
    resp = router.route(req)
    print("TESTE 1: Criar evento completo")
    print(f"  Action: {resp.action}")
    print(f"  Args: {resp.args}")
    print()


def test_list_today():
    router = IntentRouter()
    req = AgentRequest(
        user_id='123',
        timezone='America/Sao_Paulo',
        now='2026-03-07T10:00:00',
        message='O que tenho hoje?'
    )
    resp = router.route(req)
    print("TESTE 2: Listar hoje")
    print(f"  Action: {resp.action}")
    print()


def test_help():
    router = IntentRouter()
    req = AgentRequest(
        user_id='123',
        timezone='America/Sao_Paulo',
        now='2026-03-07T10:00:00',
        message='/start'
    )
    resp = router.route(req)
    print("TESTE 3: Help")
    print(f"  Action: {resp.action}")
    print()


def test_incomplete_event():
    router = IntentRouter()
    req = AgentRequest(
        user_id='123',
        timezone='America/Sao_Paulo',
        now='2026-03-07T10:00:00',
        message='Marcar reuniao as 14h'
    )
    resp = router.route(req)
    print("TESTE 4: Evento incompleto (sem data)")
    print(f"  Action: {resp.action}")
    print(f"  Args: {resp.args}")
    print()


def test_event_tomorrow():
    router = IntentRouter()
    req = AgentRequest(
        user_id='123',
        timezone='America/Sao_Paulo',
        now='2026-03-07T10:00:00',
        message='Agendar almoco amanha as 12h'
    )
    resp = router.route(req)
    print("TESTE 5: Evento amanha")
    print(f"  Action: {resp.action}")
    print(f"  Args: {resp.args}")
    print()


if __name__ == "__main__":
    test_extraction()
    test_create_event_complete()
    test_list_today()
    test_help()
    test_incomplete_event()
    test_event_tomorrow()

