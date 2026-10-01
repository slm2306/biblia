package com.sebas.biblia.versiculo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersiculoRepository extends JpaRepository<Versiculo, Integer> {

    List<Versiculo> findByLibroNombreAndCapituloOrderByNumero(String nombre, Integer capitulo);
    List<Versiculo> findByLibroNombreIgnoreCaseAndCapituloAndNumeroBetweenOrderByNumero(
            String nombre, Integer capitulo, Integer desde, Integer hasta);
}

