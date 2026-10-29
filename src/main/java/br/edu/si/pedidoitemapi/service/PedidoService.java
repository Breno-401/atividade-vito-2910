package br.edu.si.pedidoitemapi.service;

import br.edu.si.pedidoitemapi.dto.PedidoRequest;
import br.edu.si.pedidoitemapi.dto.PedidoResponse;
import br.edu.si.pedidoitemapi.entity.Pedido;
import br.edu.si.pedidoitemapi.repository.ClienteRepository;
import br.edu.si.pedidoitemapi.repository.PedidoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PedidoService {
    private final PedidoRepository pedidos;
    private final ClienteRepository clientes;

    public PedidoService(PedidoRepository pedidos, ClienteRepository clientes) {
        this.pedidos = pedidos;
        this.clientes = clientes;
    }

    public Pedido buscarEntidade(Long id) {
        return pedidos.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    public PedidoResponse buscar(Long id) { return PedidoResponse.from(buscarEntidade(id)); }

    public List<PedidoResponse> listar() {
        return pedidos.findAll().stream().map(PedidoResponse::from).toList();
    }

    public List<PedidoResponse> listarPorCliente(Long clienteId) {
        if (!clientes.existsById(clienteId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }
        return pedidos.findByClienteId(clienteId).stream().map(PedidoResponse::from).toList();
    }

    public BigDecimal total(Long id) { return buscar(id).valorTotal(); }

    @Transactional
    public PedidoResponse criar(PedidoRequest dados) {
        Pedido pedido = new Pedido();
        preencher(pedido, dados);
        // A data é gerada pelo servidor, sem depender do usuário.
        pedido.setDataPedido(LocalDateTime.now());
        return PedidoResponse.from(pedidos.saveAndFlush(pedido));
    }

    @Transactional
    public PedidoResponse atualizar(Long id, PedidoRequest dados) {
        Pedido pedido = buscarEntidade(id);
        preencher(pedido, dados);
        return PedidoResponse.from(pedidos.saveAndFlush(pedido));
    }

    @Transactional
    public void excluir(Long id) {
        Pedido pedido = buscarEntidade(id);
        // Exclui os vínculos pedido_item, mas mantém os itens cadastrados.
        pedidos.delete(pedido);
        pedidos.flush();
    }

    private void preencher(Pedido pedido, PedidoRequest dados) {
        var cliente = clientes.findById(dados.clienteId()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
        pedido.setCliente(cliente);
        pedido.setStatus(dados.status());
    }
}

