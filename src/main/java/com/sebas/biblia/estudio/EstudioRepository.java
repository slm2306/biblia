package com.sebas.biblia.estudio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EstudioRepository extends JpaRepository<Estudio, Integer> {

    List<Estudio> findAllByOrderByCreadoEnDesc();

    boolean existsByTituloIgnoreCase(String titulo);

    boolean existsByTituloIgnoreCaseAndIdNot(String titulo, Integer id);

    @Query(value = """
            SELECT d.id
            FROM (
                SELECT e.id, e.creado_en,
                       setweight(to_tsvector('spanish', unaccent(e.titulo)), 'A')
                    || setweight(to_tsvector('spanish', unaccent(coalesce((
                           SELECT string_agg(t.nombre, ' ')
                           FROM estudio_tema et JOIN tema t ON t.id = et.tema_id
                           WHERE et.estudio_id = e.id), ''))), 'B')
                    || setweight(to_tsvector('spanish', unaccent(e.explicacion)), 'C') AS doc
                FROM estudio e
            ) d,
            websearch_to_tsquery('spanish', unaccent(:q)) consulta
            WHERE d.doc @@ consulta
            ORDER BY ts_rank(d.doc, consulta) DESC, d.creado_en DESC
            """, nativeQuery = true)
    List<Integer> buscarIds(@Param("q") String q);
}