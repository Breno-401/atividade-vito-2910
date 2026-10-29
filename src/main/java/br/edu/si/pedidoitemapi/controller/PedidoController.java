package br.edu.si.pedidoitemapi.controller;

import br.edu.si.pedidoitemapi.dto.PedidoRequest;
import br.edu.si.pedidoitemapi.dto.PedidoResponse;
import br.edu.si.pedidoitemapi.service.PedidoService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// CRUD dos pedidos. O cálculo de valores é feito no service/DTO.
@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {
    private final PedidoService service;

    public PedidoController(PedidoService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dados));
    }

    @GetMapping
    public List<PedidoResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id) { return service.buscar(id); }

    // Desafio extra: mostra somente o valor total do pedido.
    @GetMapping("/{id}/total")
    public Map<String, BigDecimal> total(@PathVariable Long id) {
        return Map.of("valorTotal", service.total(id));
    }

    @PutMapping("/{id}")
    public PedidoResponse atualizar(@PathVariable Long id,
                                    @Valid @RequestBody PedidoRequest dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

