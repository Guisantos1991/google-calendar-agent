# -*- coding: utf-8 -*-
"""Testes completos de detecção de intenção."""
from src.intent.intent_detector import IntentDetector


def main():
    detector = IntentDetector()

    test_cases = [
        # Mensagens que falhavam no bot (screenshot)
        ("Quais os pr\u00f3ximos compromissos?", "LIST_NEXT"),
        ("agenda semana", "LIST_WEEK"),
        ("semana que vem", "LIST_NEXT_WEEK"),
        ("proxima semana", "LIST_NEXT_WEEK"),
        ("Pr\u00f3ximos Compromissos", "LIST_NEXT"),
        ("agenda da semana que vem", "LIST_NEXT_WEEK"),
        # Basicos
        ("hoje", "LIST_TODAY"),
        ("amanh\u00e3", "LIST_TOMORROW"),
        ("meus compromissos", "LIST_NEXT"),
        ("m\u00eas", "LIST_MONTH"),
        ("pr\u00f3ximo m\u00eas", "LIST_NEXT_MONTH"),
        ("m\u00eas que vem", "LIST_NEXT_MONTH"),
        ("o que tenho hoje?", "LIST_TODAY"),
        ("eventos de hoje", "LIST_TODAY"),
        # Acao
        ("cancelar reuni\u00e3o", "CANCEL_EVENT"),
        ("marcar reuni\u00e3o amanh\u00e3 14h", "CREATE_EVENT"),
        ("agendar almo\u00e7o dia 15 \u00e0s 12h", "CREATE_EVENT"),
        ("/start", "HELP"),
        # Extras
        ("agenda", "LIST_NEXT"),
        ("quais compromissos", "LIST_NEXT"),
        ("compromissos da semana", "LIST_WEEK"),
        ("compromissos semana que vem", "LIST_NEXT_WEEK"),
        ("o que vem a\u00ed", "LIST_NEXT"),
        ("agenda do m\u00eas", "LIST_MONTH"),
        ("compromissos do pr\u00f3ximo m\u00eas", "LIST_NEXT_MONTH"),
        ("eventos de amanh\u00e3", "LIST_TOMORROW"),
        ("reuni\u00e3o com Jo\u00e3o amanh\u00e3", "CREATE_EVENT"),
    ]

    print("Intent Detection Tests:")
    print("=" * 70)
    all_passed = True
    for msg, expected in test_cases:
        result = detector.detect(msg)
        actual = result.intent.value
        status = "PASS" if actual == expected else "FAIL"
        if actual != expected:
            all_passed = False
        print(f'{status} | "{msg}"')
        print(f"       Expected: {expected} | Got: {actual} | Pattern: {result.matched_pattern}")

    print("=" * 70)
    if all_passed:
        print("ALL PASSED")
    else:
        print("SOME FAILED")


if __name__ == "__main__":
    main()

