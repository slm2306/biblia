package com.sebas.biblia.nota;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotaRepository extends JpaRepository<Nota, Integer> {

    List<Nota> findByVersiculoIdOrderByCreadaEnDesc(Integer versiculoId);
}