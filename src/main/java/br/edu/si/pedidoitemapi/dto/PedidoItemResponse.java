package br.edu.si.pedidoitemapi.dto;

import br.edu.si.pedidoitemapi.entity.PedidoItem;
import java.math.BigDecimal;

public record PedidoItemResponse(
    Long id, Long itemId, String descricao, Integer quantidade,
    BigDecimal precoUnitario, BigDecimal subtotal
) {
    public static PedidoItemResponse from(PedidoItem pi) {
        return new PedidoItemResponse(
            pi.getId(), pi.getItem().getId(), pi.getItem().getDescricao(),
            pi.getQuantidade(), pi.getPrecoUnitario(),
            pi.getPrecoUnitario().multiply(BigDecimal.valueOf(pi.getQuantidade()))
        );
    }
}

