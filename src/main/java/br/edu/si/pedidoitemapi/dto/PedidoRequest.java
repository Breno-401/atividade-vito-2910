package br.edu.si.pedidoitemapi.dto;

import br.edu.si.pedidoitemapi.entity.StatusPedido;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PedidoRequest(
    @NotNull @Positive Long clienteId,
    @NotNull StatusPedido status
) {}

