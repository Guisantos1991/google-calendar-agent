"""Padrões de linguagem natural para detecção de intenções."""

# Palavras-chave para criação de eventos — verbos de ação explícitos (alta prioridade)
CREATE_ACTION_PATTERNS = [
    "marcar",
    "agendar",
    "criar",
    "adicionar",
    "novo evento",
    "nova reuniao",
    "nova reunião",
    "novo compromisso",
    "evento",
    "marca um compromisso",
    "marca"

]

# Palavras-chave para criação — substantivos que indicam evento (baixa prioridade,
# usados como fallback quando nenhuma intenção de listagem foi detectada)
CREATE_NOUN_PATTERNS = [
    "reuniao",
    "reunião",
    "call",
    "consulta",
    "encontro",
    "almoco",
    "almoço",
    "jantar",
    "cafe",
    "café",
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
    "como esta minha agenda hoje",
    "como está minha agenda hoje",
]

# Palavras-chave para listar próximos compromissos (genérico)
LIST_NEXT_PATTERNS = [
    "proximo compromisso",
    "próximo compromisso",
    "proximos compromissos",
    "próximos compromissos",
    "qual o proximo",
    "qual o próximo",
    "quais os proximos",
    "quais os próximos",
    "proximo evento",
    "próximo evento",
    "proximos eventos",
    "próximos eventos",
    "o que vem ai",
    "o que vem aí",
    "o que tem pela frente",
    "meus proximos",
    "meus próximos",
    "proximos",
    "próximos",
    "agenda proxima",
    "agenda próxima",
    "quais compromissos",
    "meus compromissos",
    "meus eventos",
    "compromissos futuros",
    "eventos futuros",
    "agenda",
]

# Palavras-chave para listar amanhã
LIST_TOMORROW_PATTERNS = [
    "amanha",
    "amanhã",
    "agenda de amanha",
    "agenda de amanhã",
    "compromissos de amanha",
    "compromissos de amanhã",
    "o que tenho amanha",
    "o que tenho amanhã",
    "eventos de amanha",
    "eventos de amanhã",
]

# Palavras-chave para listar semana atual
LIST_WEEK_PATTERNS = [
    "essa semana",
    "esta semana",
    "desta semana",
    "da semana",
    "agenda da semana",
    "compromissos da semana",
    "eventos da semana",
    "agenda semana",
    "compromissos semana",
    "proximos dias",
    "próximos dias",
    "semana",
]

# Palavras-chave para listar próxima semana
LIST_NEXT_WEEK_PATTERNS = [
    "proxima semana",
    "próxima semana",
    "semana que vem",
    "na proxima semana",
    "na próxima semana",
    "agenda da proxima semana",
    "agenda da próxima semana",
    "compromissos da proxima semana",
    "compromissos da próxima semana",
    "eventos da proxima semana",
    "eventos da próxima semana",
    "agenda semana que vem",
    "compromissos semana que vem",
]

# Palavras-chave para listar mês atual
LIST_MONTH_PATTERNS = [
    "esse mes",
    "esse mês",
    "este mes",
    "este mês",
    "agenda do mes",
    "agenda do mês",
    "compromissos do mes",
    "compromissos do mês",
    "eventos do mes",
    "eventos do mês",
    "mes",
    "mês",
]

# Palavras-chave para listar próximo mês
LIST_NEXT_MONTH_PATTERNS = [
    "proximo mes",
    "próximo mês",
    "proximo mês",
    "mes que vem",
    "mês que vem",
    "no proximo mes",
    "no próximo mês",
    "no proximo mês",
    "agenda do proximo mes",
    "agenda do próximo mês",
    "agenda do proximo mês",
    "compromissos do proximo mes",
    "compromissos do próximo mês",
    "compromissos do proximo mês",
]

# Comandos de ajuda
HELP_PATTERNS = [
    "/start",
    "start",
    "ajuda",
    "help",
    "?",
    "comandos",
    "o que voce faz",
    "o que você faz",
]
