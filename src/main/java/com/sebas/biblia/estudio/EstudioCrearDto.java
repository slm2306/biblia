package com.sebas.biblia.estudio;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record EstudioCrearDto(
        @NotBlank String titulo,
        @NotBlank String explicacion,
        List<String> temas,
        @NotEmpty @Valid List<PasajeDto> pasajes) {
}