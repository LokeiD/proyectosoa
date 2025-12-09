package com.coopac.sistemasoa.service;

import com.coopac.sistemasoa.dto.SocioDTO;
import com.coopac.sistemasoa.exception.SoaException;
import com.coopac.sistemasoa.model.Socio;
import com.coopac.sistemasoa.repository.AportesRepository;
import com.coopac.sistemasoa.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class SocioService {

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private AportesRepository aportesRepository;

    public SocioDTO obtenerInformacionParaSolicitud(String dni) {
        Socio socio = buscarSocioPorDni(dni);

        validarSocioActivo(socio);
        validarMayoriaEdad(socio);
        validarAportesActivos(socio);

        return mapearSocioADTO(socio);
    }

    private Socio buscarSocioPorDni(String dni) {
        return socioRepository.findByDni(dni)
                .orElseThrow(() -> new SoaException("404","El DNI ingresado no existe en el padrón de socios."));
    }

    private void validarSocioActivo(Socio socio) {
        if (Boolean.FALSE.equals(socio.getEstado())) {
            throw new RuntimeException("El socio se encuentra INACTIVO.");
        }
    }

    private void validarMayoriaEdad(Socio socio) {
        int edad = calcularEdad(socio.getFechaNacimiento());
        if (edad < 18) {
            throw new RuntimeException("El socio es menor de edad (" + edad + " años). No puede solicitar créditos.");
        }
    }

    private void validarAportesActivos(Socio socio) {
        boolean aportesActivos = aportesRepository.existsBySocioAndEstadoTrue(socio);
        if (!aportesActivos) {
            throw new RuntimeException("El socio no cuenta con aportes ACTIVOS o no está al día.");
        }
    }

    private int calcularEdad(LocalDate fechaNacimiento) {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    private SocioDTO mapearSocioADTO(Socio socio) {
        SocioDTO dto = new SocioDTO();
        dto.setCodSocio(socio.getCodSocio());
        dto.setDni(socio.getDni());
        dto.setNombresCompletos(socio.getNombres() + " " + socio.getApellidoPaterno() + " " + socio.getApellidoMaterno());
        dto.setEdad(calcularEdad(socio.getFechaNacimiento()));
        dto.setFechaNacimiento(socio.getFechaNacimiento());
        dto.setEstadoCivil(socio.getEstadoCivil());
        dto.setDomicilio(socio.getDireccion());
        dto.setDepartamento(socio.getDepartamento());
        dto.setProvincia(socio.getProvincia());
        dto.setDistrito(socio.getDistrito());
        dto.setSector(socio.getSector());
        return dto;
    }
}