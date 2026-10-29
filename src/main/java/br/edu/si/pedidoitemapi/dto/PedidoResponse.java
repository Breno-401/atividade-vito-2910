package br.edu.si.pedidoitemapi.dto;

import br.edu.si.pedidoitemapi.entity.Pedido;
import br.edu.si.pedidoitemapi.entity.StatusPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
    Long id, ClienteResumo cliente, LocalDateTime dataPedido, StatusPedido status,
    List<PedidoItemResponse> itens, BigDecimal valorTotal
) {
    public record ClienteResumo(Long id, String nome) {}

    public static PedidoResponse from(Pedido pedido) {
        var itens = pedido.getItens().stream()
            .map(PedidoItemResponse::from)
            .sorted((a, b) -> a.id().compareTo(b.id()))
            .toList();
        var total = itens.stream()
            .map(PedidoItemResponse::subtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PedidoResponse(
            pedido.getId(),
            new ClienteResumo(pedido.getCliente().getId(), pedido.getCliente().getNome()),
            pedido.getDataPedido(), pedido.getStatus(), itens, total
        );
    }
}

