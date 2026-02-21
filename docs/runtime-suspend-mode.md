# Estratégia de suspensão (modo econômico)

## Objetivo
Reduzir custo quando não houver uso frequente do bot.

## Estratégia inicial (V1)
- Deixar serviço Python sempre ativo (baixo consumo)
- Permitir que serviço Java hiberne no PaaS por inatividade
- Na primeira requisição após hibernação, retornar mensagem de processamento

## Evolução (V2)
- Fila curta de comandos
- Wake explícito por endpoint interno
- Métrica de tempo de wake e taxa de timeout
