package com.coopac.sistemasoa.expediente.service.entity;

import com.coopac.sistemasoa.socio.model.Socio;
import com.coopac.sistemasoa.socio.model.dto.SocioDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class DatosSocioService {

    public SocioDTO mapearSocioADTO(Socio socio) {
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

    private int calcularEdad(LocalDate fechaNacimiento) {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}