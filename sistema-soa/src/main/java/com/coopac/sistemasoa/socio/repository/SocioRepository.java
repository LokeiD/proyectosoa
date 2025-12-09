package com.coopac.sistemasoa.socio.repository;

import com.coopac.sistemasoa.socio.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Integer> {
    Optional<Socio> findByDni(String dni);
}