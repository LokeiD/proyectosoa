package com.coopac.sistemasoa.controller;

import com.coopac.sistemasoa.dto.CargaComboBoxFormularioDTO;
import com.coopac.sistemasoa.dto.SolicitudCreditoDTO;
import com.coopac.sistemasoa.model.ExpedienteCredito;
import com.coopac.sistemasoa.service.GestionCreditosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/creditos")
@CrossOrigin(origins = "*")
public class SolicitudCreditoController {

    @Autowired
    private GestionCreditosService creditosService;

    @GetMapping("/datos-formulario")
    public ResponseEntity<CargaComboBoxFormularioDTO> obtenerDatosFormulario() {
        return ResponseEntity.ok(creditosService.obtenerDatosCombos());
    }

    @PostMapping("/solicitud")
    public ResponseEntity<?> crearSolicitud(@RequestBody SolicitudCreditoDTO dto) {
        try {
            ExpedienteCredito nuevoExpediente = creditosService.registrarSolicitud(dto);
            return ResponseEntity.ok(nuevoExpediente);
        } catch (RuntimeException e) {
            // Errores de validación (DNI no existe, monto 0, etc.)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error interno del servidor: " + e.getMessage());
        }
    }

    @GetMapping("/listado")
    public ResponseEntity<List<ExpedienteCredito>> listarSolicitudes() {
        return ResponseEntity.ok(creditosService.listarTodas());
    }
}