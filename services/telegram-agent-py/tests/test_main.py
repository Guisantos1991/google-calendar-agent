"""Integration tests for the FastAPI endpoints in main.py."""
import pytest
from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


def test_health():
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "ok"
    assert "timezone" in data


def test_check_intent_list():
    response = client.post("/agent/intent", json={"text": "listar eventos"})
    assert response.status_code == 200
    data = response.json()
    assert data["action"] == "list"


def test_check_intent_create():
    response = client.post("/agent/intent", json={"text": "criar Reunião de onboarding"})
    assert response.status_code == 200
    data = response.json()
    assert data["action"] == "create"
    assert data["title"] == "Reunião de onboarding"


def test_check_intent_cancel():
    response = client.post("/agent/intent", json={"text": "cancelar evt-999"})
    assert response.status_code == 200
    data = response.json()
    assert data["action"] == "cancel"
    assert data["event_id"] == "evt-999"


def test_check_intent_unknown():
    response = client.post("/agent/intent", json={"text": "olá"})
    assert response.status_code == 200
    data = response.json()
    assert data["action"] == "unknown"
