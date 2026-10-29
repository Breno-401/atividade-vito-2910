package br.edu.si.pedidoitemapi.controller;

import br.edu.si.pedidoitemapi.dto.PedidoItemRequest;
import br.edu.si.pedidoitemapi.dto.PedidoItemResponse;
import br.edu.si.pedidoitemapi.dto.QuantidadeRequest;
import br.edu.si.pedidoitemapi.service.PedidoItemService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Gerencia os itens de um pedido específico.
@RestController
@RequestMapping("/api/v1/pedidos/{id}/itens")
public class PedidoItemController {
    private final PedidoItemService service;

    public PedidoItemController(PedidoItemService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<PedidoItemResponse> adicionar(
        @PathVariable Long id, @Valid @RequestBody PedidoItemRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionar(id, dados));
    }

    @GetMapping
    public List<PedidoItemResponse> listar(@PathVariable Long id) {
        return service.listar(id);
    }

    @PutMapping("/{pedidoItemId}")
    public PedidoItemResponse atualizar(
        @PathVariable Long id, @PathVariable Long pedidoItemId,
        @Valid @RequestBody QuantidadeRequest dados) {
        return service.atualizarQuantidade(id, pedidoItemId, dados);
    }

    @DeleteMapping("/{pedidoItemId}")
    public ResponseEntity<Void> remover(
        @PathVariable Long id, @PathVariable Long pedidoItemId) {
        service.remover(id, pedidoItemId);
        return ResponseEntity.noContent().build();
    }
}

