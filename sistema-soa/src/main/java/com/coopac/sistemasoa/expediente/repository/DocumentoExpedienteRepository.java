package com.coopac.sistemasoa.expediente.repository;

import com.coopac.sistemasoa.expediente.model.DocumentoExpediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoExpedienteRepository extends JpaRepository<DocumentoExpediente, Integer> {

}