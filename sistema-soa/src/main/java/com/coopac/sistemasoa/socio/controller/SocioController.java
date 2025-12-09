package com.coopac.sistemasoa.socio.controller;

import com.coopac.sistemasoa.socio.service.SocioService;
import com.coopac.sistemasoa.socio.api.SocioApi;
import com.coopac.sistemasoa.socio.model.dto.SocioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")

public class SocioController implements SocioApi {

    @Autowired
    private SocioService socioService;

    @Override
    public ResponseEntity<SocioDTO> buscarSocioPorDni(String dni) {
        return ResponseEntity.ok(socioService.obtenerInformacionParaSolicitud(dni));
    }

}