package br.edu.si.pedidoitemapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.datasource.url=jdbc:sqlite:pedido-item-test.db")
class PedidoItemApiIntegrationTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper json;
    @Autowired
    private JdbcTemplate banco;

    @BeforeEach
    void limparBancoDeTeste() {
        // Limpa apenas o banco de testes, respeitando a ordem dos relacionamentos.
        banco.update("DELETE FROM pedido_item");
        banco.update("DELETE FROM pedido");
        banco.update("DELETE FROM item");
        banco.update("DELETE FROM cliente");
    }

    @Test
    void testaFluxoCompletoDaAtividade() throws Exception {
        // 1. Cadastra dois clientes e altera o telefone de um deles.
        long maria = id(enviar("POST", "/api/v1/clientes", """
            {"nome":"Maria Silva","email":"maria.teste@example.com","telefone":"27999999999"}
            """, 201));
        long joao = id(enviar("POST", "/api/v1/clientes", """
            {"nome":"João Souza","email":"joao.teste@example.com","telefone":"27988888888"}
            """, 201));
        assertTrue(enviar("GET", "/api/v1/clientes", null, 200).isArray());
        assertEquals("27977777777", enviar("PUT", "/api/v1/clientes/" + maria, """
            {"nome":"Maria Silva","email":"maria.teste@example.com","telefone":"27977777777"}
            """, 200).get("telefone").asText());

        // 2. Cadastra três produtos, com preços diferentes.
        long teclado = id(enviar("POST", "/api/v1/itens", """
            {"descricao":"Teclado USB","preco":85.90,"estoque":30}
            """, 201));
        long mouse = id(enviar("POST", "/api/v1/itens", """
            {"descricao":"Mouse USB","preco":45.00,"estoque":50}
            """, 201));
        id(enviar("POST", "/api/v1/itens", """
            {"descricao":"Cabo USB","preco":19.90,"estoque":100}
            """, 201));
        assertTrue(enviar("GET", "/api/v1/itens", null, 200).size() >= 3);
        enviar("POST", "/api/v1/itens", """
            {"descricao":"Inválido","preco":0,"estoque":-1}
            """, 400);

        // 3. Cria pedido válido e verifica as respostas de erro.
        enviar("POST", "/api/v1/pedidos", """
            {"clienteId":99999999,"status":"ABERTO"}
            """, 404);
        enviar("POST", "/api/v1/pedidos", """
            {"clienteId":1,"status":"PENDENTE"}
            """, 400);
        long pedido = id(enviar("POST", "/api/v1/pedidos", String.format(
            "{\"clienteId\":%d,\"status\":\"ABERTO\"}", maria), 201));
        long pedido2 = id(enviar("POST", "/api/v1/pedidos", String.format(
            "{\"clienteId\":%d,\"status\":\"FINALIZADO\"}", joao), 201));

        // 4. Inclui dois itens e confere o total calculado.
        String caminhoItens = "/api/v1/pedidos/" + pedido + "/itens";
        long linhaTeclado = id(enviar("POST", caminhoItens, String.format(
            "{\"itemId\":%d,\"quantidade\":2}", teclado), 201));
        long linhaMouse = id(enviar("POST", caminhoItens, String.format(
            "{\"itemId\":%d,\"quantidade\":1}", mouse), 201));
        enviar("POST", caminhoItens, """
            {"itemId":99999999,"quantidade":2}
            """, 404);
        enviar("POST", caminhoItens, String.format(
            "{\"itemId\":%d,\"quantidade\":0}", mouse), 400);
        assertEquals(2, enviar("GET", caminhoItens, null, 200).size());
        JsonNode detalhes = enviar("GET", "/api/v1/pedidos/" + pedido, null, 200);
        assertEquals(2, detalhes.get("itens").size());
        assertEquals(216.80, detalhes.get("valorTotal").asDouble(), 0.001);

        // 5. Muda o preço do produto: pedido antigo mantém o preço original.
        enviar("PUT", "/api/v1/itens/" + teclado, """
            {"descricao":"Teclado USB","preco":100.00,"estoque":30}
            """, 200);
        detalhes = enviar("GET", "/api/v1/pedidos/" + pedido, null, 200);
        assertEquals(85.90, detalhes.get("itens").get(0).get("precoUnitario").asDouble(), 0.001);

        // 6. Altera quantidade e impede mexer em item de outro pedido.
        enviar("PUT", caminhoItens + "/" + linhaTeclado, """
            {"quantidade":3}
            """, 200);
        enviar("PUT", "/api/v1/pedidos/" + pedido2 + "/itens/" + linhaTeclado, """
            {"quantidade":5}
            """, 404);
        assertEquals(302.70, enviar("GET", "/api/v1/pedidos/" + pedido + "/total",
            null, 200).get("valorTotal").asDouble(), 0.001);
        assertEquals(1, enviar("GET", "/api/v1/clientes/" + maria + "/pedidos",
            null, 200).size());

        // 7. Remove um item, testa conflitos e depois exclui o pedido.
        enviar("DELETE", caminhoItens + "/" + linhaMouse, null, 204);
        assertEquals(1, enviar("GET", caminhoItens, null, 200).size());
        enviar("DELETE", "/api/v1/clientes/" + maria, null, 409);
        enviar("DELETE", "/api/v1/itens/" + teclado, null, 409);
        enviar("DELETE", "/api/v1/pedidos/" + pedido, null, 204);
        enviar("GET", "/api/v1/pedidos/" + pedido, null, 404);
        Integer vinculados = banco.queryForObject(
            "SELECT COUNT(*) FROM pedido_item WHERE pedido_id = ?", Integer.class, pedido);
        assertEquals(0, vinculados);

        // 8. Excluir pedido não exclui o produto cadastrado.
        enviar("GET", "/api/v1/itens/" + teclado, null, 200);
        enviar("DELETE", "/api/v1/itens/" + teclado, null, 204);
        enviar("DELETE", "/api/v1/pedidos/" + pedido2, null, 204);
        enviar("DELETE", "/api/v1/clientes/" + joao, null, 204);
        enviar("GET", "/v3/api-docs", null, 200);
    }

    private long id(JsonNode objeto) {
        return objeto.get("id").asLong();
    }

    // Executa uma requisição simulada e compara o status HTTP esperado.
    private JsonNode enviar(String metodo, String caminho, String corpo, int status)
        throws Exception {
        MockHttpServletRequestBuilder requisicao = switch (metodo) {
            case "POST" -> post(caminho);
            case "PUT" -> put(caminho);
            case "DELETE" -> delete(caminho);
            default -> get(caminho);
        };
        if (corpo != null) {
            requisicao.contentType(MediaType.APPLICATION_JSON).content(corpo);
        }
        MvcResult resposta = mvc.perform(requisicao).andReturn();
        assertEquals(status, resposta.getResponse().getStatus(),
            metodo + " " + caminho + ": " + resposta.getResponse().getContentAsString());
        String texto = resposta.getResponse().getContentAsString();
        return texto.isBlank() ? null : json.readTree(texto);
    }
}

