package com.coopac.sistemasoa.expediente.repository;

import com.coopac.sistemasoa.expediente.model.ProductoCrediticio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoCrediticioRepository extends JpaRepository<ProductoCrediticio, Integer> {
}