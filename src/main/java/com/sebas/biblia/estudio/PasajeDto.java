package com.sebas.biblia.estudio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PasajeDto(
        @NotBlank String libro,
        @NotNull @Positive Integer capitulo,
        @NotNull @Positive Integer desde,
        @Positive Integer hasta) {
}