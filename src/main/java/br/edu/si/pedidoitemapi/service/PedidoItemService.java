package br.edu.si.pedidoitemapi.service;

import br.edu.si.pedidoitemapi.dto.PedidoItemRequest;
import br.edu.si.pedidoitemapi.dto.PedidoItemResponse;
import br.edu.si.pedidoitemapi.dto.QuantidadeRequest;
import br.edu.si.pedidoitemapi.entity.PedidoItem;
import br.edu.si.pedidoitemapi.repository.ItemRepository;
import br.edu.si.pedidoitemapi.repository.PedidoItemRepository;
import br.edu.si.pedidoitemapi.repository.PedidoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PedidoItemService {
    private final PedidoItemRepository itensPedido;
    private final PedidoRepository pedidos;
    private final ItemRepository itens;

    public PedidoItemService(PedidoItemRepository itensPedido,
                            PedidoRepository pedidos, ItemRepository itens) {
        this.itensPedido = itensPedido;
        this.pedidos = pedidos;
        this.itens = itens;
    }

    public List<PedidoItemResponse> listar(Long pedidoId) {
        buscarPedido(pedidoId);
        return itensPedido.findByPedidoIdOrderByIdAsc(pedidoId)
            .stream().map(PedidoItemResponse::from).toList();
    }

    @Transactional
    public PedidoItemResponse adicionar(Long pedidoId, PedidoItemRequest dados) {
        var pedido = buscarPedido(pedidoId);
        var item = itens.findById(dados.itemId()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));
        var pedidoItem = new PedidoItem();
        pedidoItem.setPedido(pedido);
        pedidoItem.setItem(item);
        pedidoItem.setQuantidade(dados.quantidade());
        // Guarda o preço de hoje: alterações futuras não mudam pedidos antigos.
        pedidoItem.setPrecoUnitario(item.getPreco());
        return PedidoItemResponse.from(itensPedido.saveAndFlush(pedidoItem));
    }

    @Transactional
    public PedidoItemResponse atualizarQuantidade(Long pedidoId, Long pedidoItemId,
                                                    QuantidadeRequest dados) {
        buscarPedido(pedidoId);
        var pedidoItem = buscarItemDoPedido(pedidoId, pedidoItemId);
        pedidoItem.setQuantidade(dados.quantidade());
        return PedidoItemResponse.from(itensPedido.saveAndFlush(pedidoItem));
    }

    @Transactional
    public void remover(Long pedidoId, Long pedidoItemId) {
        buscarPedido(pedidoId);
        var pedidoItem = buscarItemDoPedido(pedidoId, pedidoItemId);
        itensPedido.delete(pedidoItem);
        itensPedido.flush();
    }

    private br.edu.si.pedidoitemapi.entity.Pedido buscarPedido(Long id) {
        return pedidos.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    private PedidoItem buscarItemDoPedido(Long pedidoId, Long pedidoItemId) {
        // Confere se o vínculo realmente pertence ao pedido indicado na URL.
        return itensPedido.findByIdAndPedidoId(pedidoItemId, pedidoId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não pertence ao pedido"));
    }
}

