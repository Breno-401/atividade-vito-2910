package br.edu.si.pedidoitemapi.service;

import br.edu.si.pedidoitemapi.dto.ClienteRequest;
import br.edu.si.pedidoitemapi.entity.Cliente;
import br.edu.si.pedidoitemapi.repository.ClienteRepository;
import br.edu.si.pedidoitemapi.repository.PedidoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ClienteService {
    private final ClienteRepository clientes;
    private final PedidoRepository pedidos;

    public ClienteService(ClienteRepository clientes, PedidoRepository pedidos) {
        this.clientes = clientes;
        this.pedidos = pedidos;
    }

    public List<Cliente> listar() { return clientes.findAll(); }

    public Cliente buscar(Long id) {
        return clientes.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @Transactional
    public Cliente criar(ClienteRequest dados) {
        Cliente cliente = new Cliente();
        preencher(cliente, dados);
        return clientes.saveAndFlush(cliente);
    }

    @Transactional
    public Cliente atualizar(Long id, ClienteRequest dados) {
        Cliente cliente = buscar(id);
        preencher(cliente, dados);
        return clientes.saveAndFlush(cliente);
    }

    @Transactional
    public void excluir(Long id) {
        Cliente cliente = buscar(id);
        // Não deixa apagar o cliente se ele tiver pedidos cadastrados.
        if (pedidos.existsByClienteId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cliente possui pedidos");
        }
        clientes.delete(cliente);
        clientes.flush();
    }

    private void preencher(Cliente cliente, ClienteRequest dados) {
        cliente.setNome(dados.nome());
        cliente.setEmail(dados.email());
        cliente.setTelefone(dados.telefone());
    }
}

