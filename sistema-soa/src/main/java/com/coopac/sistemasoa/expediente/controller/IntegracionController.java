package com.coopac.sistemasoa.expediente.controller;

import com.coopac.sistemasoa.credito.model.dto.DocumentoExpedienteDTO;
import com.coopac.sistemasoa.expediente.service.ExperianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/integraciones")
@CrossOrigin("*")
public class IntegracionController {

    @Autowired
    private ExperianService experianService;

    @PostMapping("/experian")
    public ResponseEntity<DocumentoExpedienteDTO> consultarExperian(
            @RequestParam("dni") String dni,
            @RequestParam("codExpediente") Integer codExpediente) {

        // Dispara la orquestación
        DocumentoExpedienteDTO resultado = experianService.consultarYGuardarHistorial(dni, codExpediente);

        return ResponseEntity.ok(resultado);
    }
}