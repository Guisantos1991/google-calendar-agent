"""Unit tests for the NLU parse_intent function."""
import pytest
from app.nlu import parse_intent


def test_parse_intent_list_with_listar():
    intent = parse_intent("listar eventos")
    assert intent.action == "list"


def test_parse_intent_list_with_eventos():
    intent = parse_intent("mostre meus eventos")
    assert intent.action == "list"


def test_parse_intent_create():
    intent = parse_intent("criar Reunião de equipe")
    assert intent.action == "create"
    assert intent.title == "Reunião de equipe"
    assert intent.start is not None
    assert intent.end is not None


def test_parse_intent_create_empty_title():
    intent = parse_intent("criar ")
    assert intent.action == "unknown"


def test_parse_intent_cancel():
    intent = parse_intent("cancelar abc-123")
    assert intent.action == "cancel"
    assert intent.event_id == "abc-123"


def test_parse_intent_cancel_missing_id():
    intent = parse_intent("cancelar ")
    assert intent.action == "unknown"


def test_parse_intent_unknown():
    intent = parse_intent("olá tudo bem?")
    assert intent.action == "unknown"
