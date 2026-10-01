package com.sebas.biblia.nota;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotaRepository extends JpaRepository<Nota, Integer> {

    List<Nota> findByVersiculoIdOrderByCreadaEnDesc(Integer versiculoId);
    @Query("""
        select n from Nota n
        where n.versiculo.libro.nombre = :libro and n.versiculo.capitulo = :capitulo
        order by n.versiculo.numero, n.creadaEn desc
        """)
    List<Nota> porCapitulo(@Param("libro") String libro, @Param("capitulo") Integer capitulo);
}