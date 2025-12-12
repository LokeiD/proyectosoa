package com.coopac.sistemasoa.expediente.service.entity;

import com.coopac.sistemasoa.socio.model.Socio;
import com.coopac.sistemasoa.socio.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class SocioValidationService {

    @Autowired
    private SocioRepository socioRepository;

    public Socio validarSocioApto(String dni) {
        Socio socio = socioRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Error SE-1: El socio con DNI " + dni + " no existe."));

        LocalDate fechaNacimiento = socio.getFechaNacimiento();

        // CÁLCUO DE LA EDAD A PARTIR DE FECHA_NACIMIENTO
        LocalDate fechaActual = LocalDate.now();
        int edad = Period.between(fechaNacimiento, fechaActual).getYears();
        if (edad < 18) {
            throw new RuntimeException("Error SE-1: Socio no cumple con la mayoría de edad (" + edad + " años) para solicitar crédito.");
        }
        return socio;
    }
}