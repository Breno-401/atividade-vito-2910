package br.edu.si.pedidoitemapi.controller;

import br.edu.si.pedidoitemapi.dto.ItemRequest;
import br.edu.si.pedidoitemapi.entity.Item;
import br.edu.si.pedidoitemapi.service.ItemService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Cadastro e consulta dos produtos disponíveis.
@RestController
@RequestMapping("/api/v1/itens")
public class ItemController {
    private final ItemService service;

    public ItemController(ItemService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Item> criar(@Valid @RequestBody ItemRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dados));
    }

    @GetMapping
    public List<Item> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public Item buscar(@PathVariable Long id) { return service.buscar(id); }

    @PutMapping("/{id}")
    public Item atualizar(@PathVariable Long id, @Valid @RequestBody ItemRequest dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

