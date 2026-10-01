package com.sebas.biblia.estudio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemaRepository extends JpaRepository<Tema, Integer> {

    Optional<Tema> findByNombre(String nombre);
}