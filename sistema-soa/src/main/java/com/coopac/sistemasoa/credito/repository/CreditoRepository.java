package com.coopac.sistemasoa.credito.repository;

import com.coopac.sistemasoa.credito.model.Credito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditoRepository extends JpaRepository<Credito, Integer> {
}