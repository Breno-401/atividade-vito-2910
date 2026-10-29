package br.edu.si.pedidoitemapi.service;

import br.edu.si.pedidoitemapi.dto.ItemRequest;
import br.edu.si.pedidoitemapi.entity.Item;
import br.edu.si.pedidoitemapi.repository.ItemRepository;
import br.edu.si.pedidoitemapi.repository.PedidoItemRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itens;
    private final PedidoItemRepository itensPedido;

    public ItemService(ItemRepository itens, PedidoItemRepository itensPedido) {
        this.itens = itens;
        this.itensPedido = itensPedido;
    }

    public List<Item> listar() { return itens.findAll(); }

    public Item buscar(Long id) {
        return itens.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));
    }

    @Transactional
    public Item criar(ItemRequest dados) {
        Item item = new Item();
        preencher(item, dados);
        return itens.saveAndFlush(item);
    }

    @Transactional
    public Item atualizar(Long id, ItemRequest dados) {
        Item item = buscar(id);
        preencher(item, dados);
        return itens.saveAndFlush(item);
    }

    @Transactional
    public void excluir(Long id) {
        Item item = buscar(id);
        // Um item que participa de um pedido não pode ser excluído.
        if (itensPedido.existsByItemId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Item associado a pedidos");
        }
        itens.delete(item);
        itens.flush();
    }

    private void preencher(Item item, ItemRequest dados) {
        item.setDescricao(dados.descricao());
        item.setPreco(dados.preco());
        item.setEstoque(dados.estoque());
    }
}

