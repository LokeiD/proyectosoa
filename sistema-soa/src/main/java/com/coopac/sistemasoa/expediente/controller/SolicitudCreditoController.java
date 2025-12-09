package com.coopac.sistemasoa.expediente.controller;

import com.coopac.sistemasoa.dto.CargaComboBoxFormularioDTO;
import com.coopac.sistemasoa.dto.SolicitudCreditoDTO;
import com.coopac.sistemasoa.expediente.model.ExpedienteCredito;
import com.coopac.sistemasoa.expediente.service.GestionCreditosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/creditos")

public class SolicitudCreditoController {

    @Autowired
    private GestionCreditosService creditosService;

    @GetMapping("/datos-formulario")
    public ResponseEntity<CargaComboBoxFormularioDTO> obtenerDatosFormulario() {
        return ResponseEntity.ok(creditosService.obtenerDatosCombos());
    }

    @PostMapping("/solicitud")
    public ResponseEntity<?> crearSolicitud(@RequestBody SolicitudCreditoDTO dto) {
        ExpedienteCredito nuevoExpediente = creditosService.registrarSolicitud(dto);
        return ResponseEntity.ok(nuevoExpediente);
    }

    @GetMapping("/listado")
    public ResponseEntity<List<ExpedienteCredito>> listarSolicitudes() {
        return ResponseEntity.ok(creditosService.listarTodas());
    }
}