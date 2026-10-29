# Atividade prática: pedido-item-api

## Integrantes

- Breno Clemente Pereira
- Moacyr Felipe Del Caro

Projeto desenvolvido para a atividade de laboratório de Sistemas de Informação (4º período).

A aplicação é uma API REST simples para cadastrar clientes, itens e pedidos. Um cliente pode ter vários pedidos e cada pedido pode conter vários itens.

## Tecnologias

- Java 25 e Spring Boot 3.5.16
- Maven
- Spring Web, Spring Data JPA e Bean Validation
- SQLite (banco local)
- Swagger/OpenAPI para testar os endpoints

## Como executar

1. Instale **Java 25** e **Maven 3.6.3 ou superior**.
2. Abra um terminal na pasta do projeto.
3. Execute:

   ```bash
   mvn spring-boot:run
   ```

4. Acesse o Swagger: <http://localhost:8080/swagger-ui.html>
5. A API utiliza a porta 8080 e cria o arquivo `pedido-item.db` na pasta do projeto.

Para executar o teste automatizado:

```bash
mvn test
```

O teste usa outro banco (`pedido-item-test.db`), para não misturar os dados da aplicação com os testes.

## Organização das pastas

- `entity/`: classes que representam as tabelas do SQLite.
- `repository/`: acesso ao banco de dados.
- `service/`: regras de negócio, validações e exclusões.
- `controller/`: rotas da API, recebendo GET, POST, PUT e DELETE.
- `dto/`: dados de entrada e saída em JSON.
- `exception/`: tratamento dos erros HTTP.

## Endpoints

| Método | Rota | Finalidade |
| --- | --- | --- |
| POST | /api/v1/clientes | Criar cliente |
| GET | /api/v1/clientes | Listar clientes |
| GET | /api/v1/clientes/{id} | Buscar cliente |
| PUT | /api/v1/clientes/{id} | Atualizar cliente |
| DELETE | /api/v1/clientes/{id} | Remover cliente |
| POST | /api/v1/itens | Criar item |
| GET | /api/v1/itens | Listar itens |
| GET | /api/v1/itens/{id} | Buscar item |
| PUT | /api/v1/itens/{id} | Atualizar item |
| DELETE | /api/v1/itens/{id} | Remover item |
| POST | /api/v1/pedidos | Criar pedido |
| GET | /api/v1/pedidos | Listar pedidos |
| GET | /api/v1/pedidos/{id} | Consultar pedido e seus itens |
| PUT | /api/v1/pedidos/{id} | Atualizar pedido |
| DELETE | /api/v1/pedidos/{id} | Excluir pedido |
| POST | /api/v1/pedidos/{id}/itens | Adicionar item ao pedido |
| GET | /api/v1/pedidos/{id}/itens | Listar os itens do pedido |
| PUT | /api/v1/pedidos/{id}/itens/{pedidoItemId} | Alterar quantidade |
| DELETE | /api/v1/pedidos/{id}/itens/{pedidoItemId} | Remover item do pedido |
| GET | /api/v1/clientes/{id}/pedidos | **Extra:** pedidos de um cliente |
| GET | /api/v1/pedidos/{id}/total | **Extra:** valor total do pedido |

## Exemplo de uso no Swagger

Primeiro, cadastre um cliente em **POST /api/v1/clientes**:

```json
{"nome":"Maria Silva","email":"maria@email.com","telefone":"27999999999"}
```

Depois, cadastre um item em **POST /api/v1/itens**:

```json
{"descricao":"Teclado USB","preco":85.90,"estoque":30}
```

Crie o pedido com o ID retornado para o cliente:

```json
{"clienteId":1,"status":"ABERTO"}
```

Por último, adicione o item em **POST /api/v1/pedidos/1/itens**:

```json
{"itemId":1,"quantidade":2}
```

Os IDs acima são apenas exemplos. Utilize os IDs retornados pela sua própria API.

## Regras de negócio

- O cliente precisa existir antes de criar o pedido.
- O item precisa existir antes de entrar em um pedido.
- Preços e quantidades devem ser maiores que zero; estoque não pode ser negativo.
- O preço unitário é copiado ao adicionar o item. Mudanças posteriores no cadastro não alteram a compra.
- Clientes com pedidos e itens associados a pedidos não podem ser excluídos.
- Excluir um pedido remove os itens daquele pedido, mas não apaga os produtos.
- Status possíveis: `ABERTO`, `FINALIZADO` e `CANCELADO`.
- Não há baixa automática de estoque, conforme a atividade.
- Respostas mais importantes: `201` (criado), `204` (excluído), `400` (dados inválidos), `404` (não encontrado) e `409` (conflito).

## Evidências para apresentação

O teste de integração em `src/test/` cobre o fluxo principal da atividade: cadastro de dois clientes, três itens, pedidos, consulta, alteração de quantidade, exclusão e consulta direta ao banco. Também verifica validações, integridade, preço histórico e os dois desafios extras.

Para entregar conforme o PDF, execute o projeto localmente e **tire capturas de tela do Swagger UI** com as respostas das operações. As capturas precisam ser feitas com a API realmente em execução; não são geradas por este repositório.

Use dados fictícios nos testes e não envie o arquivo do banco com informações pessoais.

