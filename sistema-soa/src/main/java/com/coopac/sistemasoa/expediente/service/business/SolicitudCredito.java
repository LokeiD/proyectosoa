package com.coopac.sistemasoa.expediente.service.business;

import com.coopac.sistemasoa.expediente.model.dto.SolicitudCreditoRequest;
import com.coopac.sistemasoa.expediente.service.entity.AporteValidationService;
import com.coopac.sistemasoa.expediente.service.entity.SocioValidationService;
import com.coopac.sistemasoa.expediente.service.support.ExpedienteSupportService;
import com.coopac.sistemasoa.expediente.service.entity.DatosSocioService;
import com.coopac.sistemasoa.socio.model.dto.SocioDTO;
import com.coopac.sistemasoa.socio.model.Socio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolicitudCredito {

    @Autowired private SocioValidationService socioValidationService;
    @Autowired private AporteValidationService aporteValidationService;
    @Autowired private ExpedienteSupportService expedienteSupportService;
    @Autowired private DatosSocioService datosSocioService;


    public SocioDTO buscarYValidarSocio(String dni) {
        Socio socioValidado = socioValidationService.validarSocioApto(dni);
        aporteValidationService.validarAportesAlDia(socioValidado);
        return datosSocioService.mapearSocioADTO(socioValidado);
    }

    public void registrarSolicitud(SolicitudCreditoRequest request) {
        Socio socioValidado = socioValidationService.validarSocioApto(request.getDniSocio());
        aporteValidationService.validarAportesAlDia(socioValidado);
        expedienteSupportService.generarExpediente(request, socioValidado);
    }
}