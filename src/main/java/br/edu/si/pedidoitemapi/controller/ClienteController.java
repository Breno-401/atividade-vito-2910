package br.edu.si.pedidoitemapi.controller;

import br.edu.si.pedidoitemapi.dto.ClienteRequest;
import br.edu.si.pedidoitemapi.dto.PedidoResponse;
import br.edu.si.pedidoitemapi.entity.Cliente;
import br.edu.si.pedidoitemapi.service.ClienteService;
import br.edu.si.pedidoitemapi.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Recebe as requisições HTTP relacionadas aos clientes.
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {
    private final ClienteService service;
    private final PedidoService pedidos;

    public ClienteController(ClienteService service, PedidoService pedidos) {
        this.service = service;
        this.pedidos = pedidos;
    }

    @PostMapping
    public ResponseEntity<Cliente> criar(@Valid @RequestBody ClienteRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dados));
    }

    @GetMapping
    public List<Cliente> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public Cliente buscar(@PathVariable Long id) { return service.buscar(id); }

    // Desafio extra: lista os pedidos feitos por um cliente.
    @GetMapping("/{id}/pedidos")
    public List<PedidoResponse> pedidosDoCliente(@PathVariable Long id) {
        return pedidos.listarPorCliente(id);
    }

    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

