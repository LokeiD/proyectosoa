package com.coopac.sistemasoa.expediente.controller;

import com.coopac.sistemasoa.credito.model.dto.*;
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

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoDTO>> listarProductos() {
        return ResponseEntity.ok(creditosService.listarProductos());
    }

    @GetMapping("/recurrencias")
    public ResponseEntity<List<RecurrenciaDTO>> listarRecurrencias() {
        return ResponseEntity.ok(creditosService.listarRecurrencias());
    }

    @GetMapping("/riesgos")
    public ResponseEntity<List<RiesgoDTO>> listarRiesgos() {
        return ResponseEntity.ok(creditosService.listarRiesgos());
    }

    @GetMapping("/periodos")
    public ResponseEntity<List<PeriodoDTO>> listarPeriodos() {
        return ResponseEntity.ok(creditosService.listarPeriodos());
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