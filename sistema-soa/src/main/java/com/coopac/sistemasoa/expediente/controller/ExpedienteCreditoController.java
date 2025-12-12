
package com.coopac.sistemasoa.expediente.controller;


import com.coopac.sistemasoa.expediente.service.business.ExpedienteService;
import com.coopac.sistemasoa.expediente.api.SolicitudApi;
import com.coopac.sistemasoa.expediente.model.dto.SolicitudCreditoRequest;
import com.coopac.sistemasoa.expediente.service.business.SolicitudCredito; // SN-1

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class ExpedienteCreditoController implements SolicitudApi {

    // SN-1: Orquestador de Registro
    @Autowired
    private SolicitudCredito solicitudCredito;

    // SN-2: Orquestador de Envío a Evaluación <--- NUEVA INYECCIÓN
    @Autowired
    private ExpedienteService expedienteBusinessService;

    // ------------------------------------------------------------------
    // SN-1: REGISTRAR SOLICITUD (POST /credito/solicitar-expediente)
    // ------------------------------------------------------------------
    @Override
    public ResponseEntity<Void> registrarSolicitudExpediente(SolicitudCreditoRequest request) {
        solicitudCredito.registrarSolicitud(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // ------------------------------------------------------------------
    // SN-2: ENVIAR A EVALUACIÓN (PUT /creditos/{id}/enviar)
    // ------------------------------------------------------------------
    /**
     * Endpoint para el botón "ENVIAR A EVALUACIÓN" del frontend.
     * PUT /api/v1/creditos/{id}/enviar
     * Dispara el flujo SN-2: SE-3 (Validación de Documentos) -> SS-2 (Cambio de Estado).
     */

    @PutMapping("/creditos/{id}/enviar")
    public ResponseEntity<?> enviarExpedienteAEvaluacion(@PathVariable Integer id) {

        // Delega el flujo SN-2 al Orquestador
        expedienteBusinessService.enviarAEvaluacion(id);

        // Retorno de éxito
        return ResponseEntity.ok(Map.of("mensaje", "Expediente enviado a Evaluación correctamente"));
    }

}