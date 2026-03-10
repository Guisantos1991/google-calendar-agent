"""Padrões de linguagem natural para detecção de intenções."""

# Palavras-chave para criação de eventos
CREATE_PATTERNS = [
    "marcar",
    "agendar",
    "criar",
    "adicionar",
    "novo evento",
    "nova reunião",
    "novo compromisso",
    "reunião",
    "reuniao",
    "evento",
    "call",
    "consulta",
    "encontro",
    "almoço",
    "almoco",
    "jantar",
    "café",
    "cafe",
    "meeting",
]

# Palavras-chave para cancelamento
CANCEL_PATTERNS = [
    "cancelar",
    "desmarcar",
    "remover",
    "apagar",
    "deletar",
    "excluir",
    "tirar",
]

# Palavras-chave para listar hoje
LIST_TODAY_PATTERNS = [
    "hoje",
    "agenda de hoje",
    "compromissos de hoje",
    "meus compromissos hoje",
    "o que tenho hoje",
    "o que tem hoje",
    "eventos de hoje",
    "minha agenda hoje",
]

# Palavras-chave para listar próximos compromissos
LIST_NEXT_PATTERNS = [
    "próximo compromisso",
    "proximo compromisso",
    "próximos compromissos",
    "proximos compromissos",
    "qual o próximo",
    "qual o proximo",
    "próximo evento",
    "proximo evento",
    "próximos eventos",
    "proximos eventos",
    "o que vem aí",
    "o que vem ai",
    "agenda",
]

# Palavras-chave para listar amanhã
LIST_TOMORROW_PATTERNS = [
    "amanhã",
    "amanha",
    "agenda de amanhã",
    "agenda de amanha",
    "compromissos de amanhã",
    "compromissos de amanha",
    "o que tenho amanhã",
    "o que tenho amanha",
    "eventos de amanhã",
    "eventos de amanha",
]

# Palavras-chave para listar semana
LIST_WEEK_PATTERNS = [
    "semana",
    "essa semana",
    "esta semana",
    "desta semana",
    "próximos dias",
    "proximos dias",
    "próxima semana",
    "proxima semana",
    "agenda da semana",
    "compromissos da semana",
]

# Palavras-chave para listar mês
LIST_MONTH_PATTERNS = [
    "mês",
    "mes",
    "esse mês",
    "esse mes",
    "este mês",
    "este mes",
    "próximo mês",
    "proximo mes",
    "agenda do mês",
    "agenda do mes",
    "compromissos do mês",
    "compromissos do mes",
]

# Comandos de ajuda
HELP_PATTERNS = [
    "/start",
    "start",
    "ajuda",
    "help",
    "?",
    "comandos",
    "o que você faz",
    "o que voce faz",
]

