package br.edu.si.pedidoitemapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ItemRequest(
    @NotBlank @Size(max = 150) String descricao,
    @NotNull @DecimalMin("0.01") BigDecimal preco,
    @NotNull @Min(0) Integer estoque
) {}

