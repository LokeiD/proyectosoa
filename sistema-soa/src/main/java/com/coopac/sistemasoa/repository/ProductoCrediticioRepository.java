package com.coopac.sistemasoa.repository;

import com.coopac.sistemasoa.model.ProductoCrediticio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoCrediticioRepository extends JpaRepository<ProductoCrediticio, Integer> {
}