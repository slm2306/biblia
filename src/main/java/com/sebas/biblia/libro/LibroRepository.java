package com.sebas.biblia.libro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LibroRepository extends JpaRepository<Libro, Integer>{
    List<Libro> findAllByOrderByOrdenAsc();
}
