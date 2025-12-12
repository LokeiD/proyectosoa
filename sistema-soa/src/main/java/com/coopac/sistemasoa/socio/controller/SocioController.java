package com.coopac.sistemasoa.socio.controller;

// Asegúrate de que esta sea la importación correcta de tu nueva clase de consulta:
import com.coopac.sistemasoa.expediente.service.business.SolicitudCredito;
import com.coopac.sistemasoa.expediente.service.entity.DatosSocioService;

import com.coopac.sistemasoa.socio.api.SocioApi;
import com.coopac.sistemasoa.socio.model.dto.SocioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SocioController implements SocioApi {

    @Autowired
    private DatosSocioService datosSocioService;

    @Autowired
    private SolicitudCredito solicitudCredito;

    @Override
    public ResponseEntity<SocioDTO> buscarSocioPorDni(String dni) {
        return ResponseEntity.ok(solicitudCredito.buscarYValidarSocio(dni));
    }
}