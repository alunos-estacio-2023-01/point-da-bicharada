# Point da Bicharada

## Pré Requisitos

- Java
- Maven

## Como executar

```bash
mvn exec:java -D"exec.args"="{acao} {entidade}"
```

O sistema irá solicitar as informações necessários para executar a ação solicitada.

### Ações disponíveis

- criar (ou cadastrar)
- selecionar
- atualizar
- apagar

### Entidades disponíveis

- cliente
- pet (ou animal)
- agendamento

No caso da ação `selecionar`, também é possível passar a entidade `pets` (ou `animais`) para obter todos os animais de um determinado `cliente`.

## Exemplos de uso

```bash
mvn exec:java -D"exec.args"="criar cliente"
```

Será primeiro solicitado o CPF do cliente, depois o nome do Cliente, seguido pelo endereço, e por último o telefone. Ao passar esses dados o cliente será criado.
