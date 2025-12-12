package com.coopac.sistemasoa.expediente.service.entity;

import com.coopac.sistemasoa.socio.model.Socio;
import com.coopac.sistemasoa.socio.repository.AportesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AporteValidationService {

    @Autowired
    private AportesRepository aportesRepository;
    public void validarAportesAlDia(Socio socio) {
        boolean aportesActivos = aportesRepository.existsBySocioAndEstadoTrue(socio);
        if (!aportesActivos) {
            throw new RuntimeException("Error SE-2: El socio no cuenta con aportes ACTIVOS o no está al día.");
        }
    }
}