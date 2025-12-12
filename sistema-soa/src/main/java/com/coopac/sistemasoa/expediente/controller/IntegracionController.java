package com.coopac.sistemasoa.expediente.controller;

import com.coopac.sistemasoa.expediente.model.dto.DocumentoExpedienteDTO;
import com.coopac.sistemasoa.expediente.service.integration.CentralRiesgosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/integraciones")
@CrossOrigin("*")
public class IntegracionController {

    @Autowired
    private CentralRiesgosService centralRiesgosService;

    @PostMapping("/experian")
    public ResponseEntity<DocumentoExpedienteDTO> consultarExperian(
            @RequestParam("dni") String dni,
            @RequestParam("codExpediente") Integer codExpediente) {

        // Dispara la orquestación
        DocumentoExpedienteDTO resultado = centralRiesgosService.consultarYGuardarHistorial(dni, codExpediente);

        return ResponseEntity.ok(resultado);
    }
}