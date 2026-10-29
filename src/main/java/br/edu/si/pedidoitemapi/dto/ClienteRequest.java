package br.edu.si.pedidoitemapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
    @NotBlank @Size(max = 120) String nome,
    @NotBlank @Email @Size(max = 150) String email,
    @Size(max = 20) String telefone
) {}

