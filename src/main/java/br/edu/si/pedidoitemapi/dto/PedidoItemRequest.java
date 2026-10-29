package br.edu.si.pedidoitemapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PedidoItemRequest(
    @NotNull @Positive Long itemId,
    @NotNull @Positive Integer quantidade
) {}

