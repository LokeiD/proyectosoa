package com.coopac.sistemasoa.expediente.controller;

// Asegúrate de que este import coincida con el nombre de tu interfaz generada
// Puede ser EvaluacionApi, CreditosApi o DefaultApi. Revisa tu carpeta target si da error.
import com.coopac.sistemasoa.evaluacion.api.EvaluacionApi;

import com.coopac.sistemasoa.evaluacion.model.dto.ExpedienteDetalleDTO;
import com.coopac.sistemasoa.evaluacion.model.dto.ExpedienteResumenDTO;
import com.coopac.sistemasoa.evaluacion.model.dto.RegistroEvaluacionRequest;
import com.coopac.sistemasoa.expediente.service.EvaluacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping; // <--- Importante
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1") // <--- ¡AQUÍ ESTABA EL PROBLEMA! Faltaba este prefijo.
public class EvaluacionController implements EvaluacionApi {

    @Autowired
    private EvaluacionService evaluacionService;

    @Override
    public ResponseEntity<List<ExpedienteResumenDTO>> listarExpedientesPendientes() {
        return ResponseEntity.ok(evaluacionService.listarPendientes());
    }

    @Override
    public ResponseEntity<ExpedienteDetalleDTO> obtenerExpedienteDetalle(Integer idExpediente) {
        return ResponseEntity.ok(evaluacionService.obtenerDetalle(idExpediente));
    }

    @Override
    public ResponseEntity<Void> registrarEvaluacion(RegistroEvaluacionRequest request) {
        evaluacionService.registrarEvaluacion(request);
        return ResponseEntity.ok().build();
    }
}