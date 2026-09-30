package com.sebas.biblia.nota;

import jakarta.validation.constraints.NotBlank;

public record NotaActualizarDto(@NotBlank String contenido) {
}