package com.sebas.biblia.nota;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotaCrearDto(
        @NotNull Integer versiculoId,
        @NotBlank String contenido) {
}